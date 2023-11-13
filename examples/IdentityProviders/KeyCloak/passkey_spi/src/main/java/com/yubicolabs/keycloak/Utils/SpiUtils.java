package com.yubicolabs.keycloak.Utils;

import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.crypto.AsymmetricSignatureSignerContext;
import org.keycloak.crypto.KeyUse;
import org.keycloak.crypto.KeyWrapper;
import org.keycloak.jose.jws.JWSBuilder;
import org.keycloak.models.KeycloakContext;
import org.keycloak.models.UserModel;
import org.keycloak.representations.AccessToken;
import org.keycloak.services.Urls;

public class SpiUtils {
  public String getAccessToken(AuthenticationFlowContext context, UserModel userModel) {
    KeycloakContext keycloakContext = context.getSession().getContext();

    AccessToken token = new AccessToken();
    token.subject(userModel.getId());
    token.issuer(Urls.realmIssuer(keycloakContext.getUri().getBaseUri(), keycloakContext.getRealm().getName()));
    token.issuedNow();
    token.expiration((int) (token.getIat() + 100L));

    KeyWrapper key = context.getSession().keys().getActiveKey(keycloakContext.getRealm(), KeyUse.SIG, "RS256");

    return new JWSBuilder().kid(key.getKid()).type("JWT").jsonContent(token)
        .sign(new AsymmetricSignatureSignerContext(key));
  }
}
