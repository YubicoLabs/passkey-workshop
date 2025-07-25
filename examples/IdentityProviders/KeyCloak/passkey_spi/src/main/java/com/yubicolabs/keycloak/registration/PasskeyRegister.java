package com.yubicolabs.keycloak.registration;

import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.authenticators.util.AcrStore;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yubicolabs.keycloak.Utils.SpiUtils;
import com.yubicolabs.keycloak.models.AttestationResponse;
import com.yubicolabs.keycloak.models.CredentialResponse;
import com.yubicolabs.keycloak.models.CredentialResponseInner;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Optional;

import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

import org.apache.http.HttpStatus;
import org.apache.http.protocol.HTTP;
import org.jboss.logging.Logger;

public class PasskeyRegister implements Authenticator {

  ObjectMapper mapper = new ObjectMapper();

  public static final String DEFAULT_WEBAUTHN_API_URL = "http://host.docker.internal:8080/v1";

  private static final Logger logger = Logger.getLogger(PasskeyRegister.class);

  private SpiUtils spiUtils = new SpiUtils();

  @Override
  public void close() {
    // NOP
  }

  private String getWebAuthnAPIurl(AuthenticationFlowContext context) {
    String webauthnAPIurl = null;

    if (context.getAuthenticatorConfig() != null) {
      webauthnAPIurl = context.getAuthenticatorConfig().getConfig().get(PasskeyRegisterFactory.CONFIG_WEBAUTHN_API_URL);
    }
    if (webauthnAPIurl == null) {
      webauthnAPIurl = DEFAULT_WEBAUTHN_API_URL;
    }
    logger.info("Using WebAuthn API URL: " + webauthnAPIurl);
    return webauthnAPIurl;
  }

  @Override
  public void authenticate(AuthenticationFlowContext context) {
    Response form = context.form()
        .setAttribute("action_type", "USERNAME")
        .setAttribute("alert_message", "")
        .createForm("passkey-register-username.ftl");

    context.challenge(form);
  }

  @Override
  public void action(AuthenticationFlowContext context) {
    String actionType = getActionType(context);
    String webauthnAPIurl = getWebAuthnAPIurl(context);

    if (actionType.equals("USERNAME")) {
      String chosenUsername = getUsername(context);

      if (chooseUsername_Action(context, chosenUsername)) {
        String url = webauthnAPIurl;
        if (url.startsWith("http://host.docker.internal", 0)) {
          url = url.replaceFirst("host.docker.internal", "localhost"); // kludge when running in a docker container
        }
        logger.info("Using frontend WebAuthn API URL: " + url);
        Response form = context.form()
            .setAttribute("action_type", "PASSKEY_CREATE")
            .setAttribute("username", chosenUsername)
            .setAttribute("ATTESTATION_OPTIONS", chosenUsername)
            .setAttribute("webauthnAPI", url)
            .createForm("passkey-register.ftl");

        context.challenge(form);
      } else {
        /**
         * Username not valid. Display error to user asking them to select another
         */
        context.failure(AuthenticationFlowError.INVALID_CREDENTIALS);
        Response form = context.form()
            .setAttribute("action_type", "USERNAME")
            .setAttribute("alert_message", "The username you selected is not available")
            .createForm("passkey-register-username.ftl");

        context.challenge(form);
      }
    } else if (actionType.equals("PASSKEY_CREATE")) {
      try {
        // Pull all of the credentials from the new user, and see if any of them were
        // high assurance
        String chosenUsername = getUsername(context);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(webauthnAPIurl + "/user/credentials/" + chosenUsername))
            .header(HTTP.CONTENT_TYPE, "application/json")
            .header("accept", "application/json")
            .GET()
            .build();

        HttpResponse<String> response = HttpClient.newBuilder().build().send(request,
            BodyHandlers.ofString());

        if (response.statusCode() == HttpStatus.SC_OK) {

          CredentialResponse credentialResponse = mapper.readValue(response.body(), CredentialResponse.class);

          if (credentialResponse.getCredentials().size() > 0) {
            String userHandle = getUserHandle(context);

            UserModel um = context.getSession().users().addUser(context.getRealm(), userHandle, chosenUsername, false,
                false);

            um.setEnabled(true);
            context.setUser(um);

            Optional<CredentialResponseInner> haCred = credentialResponse.getCredentials().stream()
                .filter(cred -> cred.isHighAssurance()).findFirst();
            AcrStore acrStore = new AcrStore(context.getSession(), context.getAuthenticationSession());
            acrStore.setLevelAuthenticated(haCred.isPresent() ? 2 : 1);

            context.success();
          } else {
            throw new Exception("User does not have any registered credentials");
          }
        } else {
          throw new Exception("The HTTP call did not return 200: " + response.body());
        }
      } catch (Exception e) {
        e.printStackTrace();
        context.failure(AuthenticationFlowError.INVALID_CREDENTIALS);
        Response form = context.form()
            .setAttribute("action_type", "USERNAME")
            .setAttribute("alert_message", "There was an unknown error, please try again")
            .createForm("passkey-register-username.ftl");

        context.challenge(form);
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

  private String getFormResult2(AuthenticationFlowContext context) {
    MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
    String secret = formData.getFirst("attestationResult2_String");
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
