package com.yubicolabs.keycloak.registration;

import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.authenticators.util.AcrStore;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yubicolabs.keycloak.models.AttestationResponse;

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

public class PasskeyRegister implements Authenticator {

  ObjectMapper mapper = new ObjectMapper();

  private static final Logger logger = Logger.getLogger(PasskeyRegister.class);

  @Override
  public void close() {
    // NOP
  }

  @Override
  public void authenticate(AuthenticationFlowContext context) {
    Response form = context.form()
        .setAttribute("action_type", "USERNAME")
        .createForm("passkey-register-username.ftl");

    context.challenge(form);
  }

  @Override
  public void action(AuthenticationFlowContext context) {
    String actionType = getActionType(context);

    if (actionType.equals("USERNAME")) {
      String chosenUsername = getUsername(context);
      System.out.println(chosenUsername);

      if (chooseUsername_Action(context, chosenUsername)) {
        System.out.println("Username valid");
        Response form = context.form()
            .setAttribute("action_type", "PASSKEY_CREATE")
            .setAttribute("username", chosenUsername)
            .setAttribute("ATTESTATION_OPTIONS", chosenUsername)
            .createForm("passkey-register.ftl");

        context.challenge(form);
      } else {
        System.out.println("Username NOT valid");
        context.failure(AuthenticationFlowError.INVALID_CREDENTIALS);
        /*
         * Return form, with a message noting that the username is not available
         */
      }
    } else if (actionType.equals("PASSKEY_CREATE")) {
      try {
        String formResult = getFormResult(context);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://host.docker.internal:8080/v1/attestation/result"))
            .header(HTTP.CONTENT_TYPE, "application/json")
            .header("accept", "application/json")
            .POST(BodyPublishers.ofString(formResult))
            .build();

        HttpResponse<String> response = HttpClient.newBuilder().build().send(request,
            BodyHandlers.ofString());

        if (response.statusCode() == HttpStatus.SC_OK) {
          String chosenUsername = getUsername(context);
          String userHandle = getUserHandle(context);

          UserModel um = context.getSession().users().addUser(context.getRealm(), userHandle, chosenUsername, false,
              false);

          um.setEnabled(true);
          context.setUser(um);

          AttestationResponse attestationResponse = mapper.readValue(response.body(), AttestationResponse.class);

          AcrStore acrStore = new AcrStore(context.getAuthenticationSession());
          acrStore.setLevelAuthenticated(attestationResponse.getCredential().isHighAssurance() ? 2 : 1);
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
  }

  /**
   * True if the username is valid, and available
   * False is otherwise
   * 
   * @param context Authentication context
   * @return
   */
  private boolean chooseUsername_Action(AuthenticationFlowContext context, String username) {
    UserModel um = context.getSession().users().getUserByUsername(context.getRealm(), username);
    if (um == null) {
      return true;
    } else {
      return false;
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
    String secret = formData.getFirst("attestationResult_String");
    return secret;
  }

  private String getActionType(AuthenticationFlowContext context) {
    MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
    String action_type = formData.getFirst("action_type");
    return action_type;
  }

  private String getUsername(AuthenticationFlowContext context) {
    MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
    String username = formData.getFirst("username");
    return username;
  }

  private String getUserHandle(AuthenticationFlowContext context) {
    MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
    String secret = formData.getFirst("userHandle");
    return secret;
  }

}