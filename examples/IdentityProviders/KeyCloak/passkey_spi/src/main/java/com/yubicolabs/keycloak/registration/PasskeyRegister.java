package com.yubicolabs.keycloak.registration;

import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.CredentialRegistrator;
import org.keycloak.authentication.InitiatedActionSupport;
import org.keycloak.authentication.RequiredActionContext;
import org.keycloak.authentication.RequiredActionProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yubicolabs.keycloak.models.AttestationOptionsRequest;
import com.yubicolabs.keycloak.models.AuthenticatorSelection;
import com.yubicolabs.keycloak.models.AuthenticatorSelection.ResidentKeyEnum;
import com.yubicolabs.keycloak.models.AuthenticatorSelection.UserVerificationEnum;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.apache.http.HttpStatus;
import org.apache.http.protocol.HTTP;
import org.jboss.logging.Logger;

public class PasskeyRegister implements Authenticator {

  ObjectMapper mapper = new ObjectMapper();

  private static final Logger logger = Logger.getLogger(PasskeyRegister.class);

  @Override
  public void close() {
    // NOP
  }

  @Override
  public void authenticate(AuthenticationFlowContext context) {
    UserModel userModel = context.getUser();

    AuthenticatorSelection authSelect = AuthenticatorSelection.builder()
        .residentKey(ResidentKeyEnum.PREFERRED)
        .userVerification(UserVerificationEnum.REQUIRED)
        .build();

    AttestationOptionsRequest attestationOptionsRequest = AttestationOptionsRequest.builder()
        .userName(userModel.getUsername())
        .displayName(userModel.getUsername())
        .authenticatorSelection(authSelect)
        .build();

    try {
      String optionsRequestBody = mapper.writeValueAsString(attestationOptionsRequest);

      HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create("http://host.docker.internal:8080/v1/attestation/options"))
          .header(HTTP.CONTENT_TYPE, "application/json")
          .header("accept", "application/json")
          .POST(BodyPublishers.ofString(optionsRequestBody))
          .build();

      HttpResponse<String> response = HttpClient.newBuilder().build().send(request, BodyHandlers.ofString());

      if (response.statusCode() == HttpStatus.SC_OK) {
        Response form = context.form()
            .setAttribute("ATTESTATION_OPTIONS", response.body())
            .createForm("passkey-register.ftl");

        context.challenge(form);
      } else {
        throw new Exception("The HTTP call did not return 200: " + response.body());
      }
    } catch (Exception e) {
      e.printStackTrace();
      logger.error("There was an issue getting attestation options: " + e.getMessage());
    }
  }

  @Override
  public void action(AuthenticationFlowContext context) {
    try {
      String formResult = getFormResult(context);

      HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create("http://host.docker.internal:8080/v1/attestation/result"))
          .header(HTTP.CONTENT_TYPE, "application/json")
          .header("accept", "application/json")
          .POST(BodyPublishers.ofString(formResult))
          .build();

      HttpResponse<String> response = HttpClient.newBuilder().build().send(request, BodyHandlers.ofString());

      if (response.statusCode() == HttpStatus.SC_OK) {
        context.success();
      } else {
        throw new Exception("The HTTP call did not return 200: " + response.body());
      }
    } catch (Exception e) {
      context.failure(AuthenticationFlowError.INVALID_CREDENTIALS);
    }
  }

  @Override
  public boolean requiresUser() {
    return true;
  }

  @Override
  public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
    return true;
  }

  @Override
  public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
    // NOP
  }

  private String getFormResult(AuthenticationFlowContext context) {
    MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
    String secret = formData.getFirst("attestationResult_String");
    return secret;
  }

}