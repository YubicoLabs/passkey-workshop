# create settings

cp tunnel.env .env

# react app

cp -r ../examples/clients/web/react/passkey-client/ react-app/source


## now:

### react-app/source/public/.well-known

$ cat react-app/source/public/.well-known/apple-app-site-association 
{
  "webcredentials": {
    "apps": [
      "VFQBXPXJH6.nl.joostd.pawskey"
    ]
  }
}

### react-app/source/src/services/PasskeyServices.js

    const baseURl = "http://localhost:8080/v1"
=>
    const baseURl = `${window.location.origin.toString()}/v1`;

## later:

### react-app/source/src/pages/sign_up/sign_up.jsx

    redirect_uri: "http://localhost:3000/oidc/callback",

### react-app/source/src/services/OIDCServices.js

    baseUri: "http://localhost:8081/realms/passkeyDemo/protocol/openid-connect",

# java-app

cp -r  ../examples/relyingParties/java-spring/ java-app/source/


# keycloack

cp ../examples//IdentityProviders/KeyCloak/pre-build/passkey_authenticator.jar keycloak/

