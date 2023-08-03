package com.yubicolabs.keycloak.authentication;

import java.util.Collections;
import java.util.List;

import static org.keycloak.provider.ProviderConfigProperty.STRING_TYPE;

import org.jboss.logging.Logger;
import org.keycloak.Config.Scope;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.authentication.RequiredActionFactory;
import org.keycloak.authentication.RequiredActionProvider;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.AuthenticationExecutionModel.Requirement;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.EnvironmentDependentProviderFactory;
import org.keycloak.provider.ProviderConfigProperty;

public class PasskeyAuthenticateFactory implements AuthenticatorFactory {

  private static final Authenticator AUTHENTICATOR_INSTANCE = new PasskeyAuthenticate();

  private static final String ID = "passkey-authenticate";

  private static final Logger logger = Logger.getLogger(PasskeyAuthenticateFactory.class);

  @Override
  public Authenticator create(KeycloakSession session) {
    return AUTHENTICATOR_INSTANCE;
  }

  @Override
  public void init(Scope config) {
    // NOP
  }

  @Override
  public void postInit(KeycloakSessionFactory factory) {
    // NOP
  }

  @Override
  public void close() {
    // NOP
  }

  @Override
  public String getId() {
    return ID;
  }

  @Override
  public String getDisplayType() {
    return "Passkey authentication";
  }

  @Override
  public String getReferenceCategory() {
    return null;
  }

  @Override
  public boolean isConfigurable() {
    return true;
  }

  @Override
  public Requirement[] getRequirementChoices() {
    return new AuthenticationExecutionModel.Requirement[] { AuthenticationExecutionModel.Requirement.REQUIRED };
  }

  @Override
  public boolean isUserSetupAllowed() {
    return false;
  }

  @Override
  public String getHelpText() {
    return "Authenticate with passkey";
  }

  @Override
  public List<ProviderConfigProperty> getConfigProperties() {
    ProviderConfigProperty name = new ProviderConfigProperty();

    name.setType(STRING_TYPE);
    name.setName("Passkey authenticate 1");
    name.setLabel("Authenticate with a passkey");
    name.setHelpText("Authenticate with a passkey");

    return Collections.singletonList(name);
  }

}
