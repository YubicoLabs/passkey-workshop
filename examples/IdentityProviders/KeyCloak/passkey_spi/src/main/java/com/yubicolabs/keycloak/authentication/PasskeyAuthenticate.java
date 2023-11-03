package com.yubicolabs.keycloak.authentication;

import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.authenticators.util.AcrStore;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yubicolabs.keycloak.models.AssertionResponse;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;

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
    if (context.getHttpRequest().getUri().getQueryParameters().get("username") != null
        && !context.getHttpRequest().getUri().getQueryParameters().get("username").isEmpty()) {
      String currentUser = context.getHttpRequest().getUri().getQueryParameters().get("username").get(0);
      Response form = context.form()
          .setAttribute("username", currentUser)
          .createForm("passkey-stepup.ftl");
      context.challenge(form);
    } else {
      Response form = context.form()
          .createForm("passkey-authenticate.ftl");
      context.challenge(form);
    }
  }

  @Override
  public void action(AuthenticationFlowContext context) {
    try {
      String formResult = getFormResult(context);
      String userHandle = getUserHandle(context);

      HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create("http://host.docker.internal:8080/v1/assertion/result"))
          .header(HTTP.CONTENT_TYPE, "application/json")
          .header("accept", "application/json")
          .POST(BodyPublishers.ofString(formResult))
          .build();

      HttpResponse<String> response = HttpClient.newBuilder().build().send(request, BodyHandlers.ofString());
      System.out.println(response.toString());
      System.out.println(response.statusCode() + "   :   " + HttpStatus.SC_OK);
      System.out.println(response.body());

      if (response.statusCode() == HttpStatus.SC_OK) {
        UserModel um = context.getSession().users().getUserById(context.getRealm(), userHandle);

        System.out.println(um.toString());

        context.setUser(um);

        AssertionResponse assertionResponse = mapper.readValue(response.body(), AssertionResponse.class);

        /**
         * 
         * This is where we can set the ACR that will be provided with the auth token
         * You will need to set this for both successful auth and reg
         * 
         * The passkey API should return a response that denotes if a the request
         * was a HA or LA (both reg and auth)
         * 
         * The Bank API should evaluate the ID token's ACR before any action is allowed
         * 
         * ACR of 1 is LA
         * ACR of 2 is HA
         * 
         * 
         */
        AcrStore acrStore = new AcrStore(context.getAuthenticationSession());
        acrStore.setLevelAuthenticated(assertionResponse.getLoa());
        System.out.println("User LoA: " + acrStore.getLevelOfAuthenticationFromCurrentAuthentication());
        context.success();
      } else {
        throw new Exception("The HTTP call did not return 200: " + response.body());
      }
    } catch (Exception e) {
      e.printStackTrace();
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