#!/bin/bash

[[ -f .env ]] || cp default.env .env
[[ -f react-app/source ]] || cp -r ../examples/clients/web/react/passkey-client/ react-app/source/
[[ -f java-app/source ]] || cp -r  ../examples/relyingParties/java-spring/ java-app/source/
[[ -f keycloak/passkey_authenticator.jar ]] || cp ../examples/IdentityProviders/KeyCloak/pre-build/passkey_authenticator.jar keycloak/
docker compose --profile web up -d
