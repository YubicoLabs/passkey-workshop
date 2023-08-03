package com.yubicolabs.keycloak.authentication;

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

public class PasskeyAuthenticate implements Authenticator {

  ObjectMapper mapper = new ObjectMapper();

  private static final Logger logger = Logger.getLogger(PasskeyAuthenticate.class);

  @Override
  public void close() {
    // NOP
  }

  @Override
  public void authenticate(AuthenticationFlowContext context) {
    Response form = context.form()
        .createForm("passkey-authenticate.ftl");
    context.challenge(form);
  }

  @Override
  public void action(AuthenticationFlowContext context) {
    try {
      String formResult = getFormResult(context);
      String userHandle = getUserHandle(context);

      System.out.println(formResult);

      HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create("http://host.docker.internal:8080/v1/assertion/result"))
          .header(HTTP.CONTENT_TYPE, "application/json")
          .header("accept", "application/json")
          .POST(BodyPublishers.ofString(formResult))
          .build();

      HttpResponse<String> response = HttpClient.newBuilder().build().send(request, BodyHandlers.ofString());
      System.out.println(response.toString());
      System.out.println(response.statusCode() + "   :   " + HttpStatus.SC_OK);

      if (response.statusCode() == HttpStatus.SC_OK) {
        UserModel um = context.getSession().users().getUserById(context.getRealm(), userHandle);

        System.out.println(um.toString());

        context.setUser(um);
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
    return false;
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
    String secret = formData.getFirst("assertionResult_String");
    return secret;
  }

  private String getUserHandle(AuthenticationFlowContext context) {
    MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
    String secret = formData.getFirst("userHandle");
    return secret;
  }

}