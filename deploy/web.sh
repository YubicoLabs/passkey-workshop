#!/bin/bash

[[ -f .env ]] || cp default.env .env
[[ -f react-app/source ]] || cp -r ../examples/clients/web/react/passkey-client/ react-app/source/
[[ -f bank-app/source ]] || cp -r ../examples/clients/web/react/bank-client/ bank-app/source/
[[ -f java-app/source ]] || cp -r  ../examples/relyingParties/java-spring/ java-app/source/
[[ -f keycloak/passkey_authenticator.jar ]] || cp ../examples/IdentityProviders/KeyCloak/pre-build/passkey_authenticator.jar keycloak/
[[ -f keycloak/passkey_spi.jar ]] || cp ../examples/IdentityProviders/KeyCloak/pre-build/passkey_spi.jar keycloak/
[[ -f keycloak/bank_realm_export.jar ]] || cp ../examples/IdentityProviders/KeyCloak/pre-build/realm_exports/bank_realm_export.json keycloak/

docker compose --profile web up -d
