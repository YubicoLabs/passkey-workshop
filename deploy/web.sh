#!/bin/bash

# This script automates the deployment of all components on localhost
# To make the front and backend available on the Internet (for instance for use on a mobile device), see cloudflared.sh

[[ -f .env ]] || cp default.env .env
[[ -d react-app/source ]] || cp -r ../examples/clients/web/react/passkey-client/ react-app/source/
[[ -d java-app/source ]] || cp -r  ../examples/relyingParties/java-spring/ java-app/source/
[[ -d keycloak/source ]] || cp -r ../examples/IdentityProviders/KeyCloak/passkey_authenticator/ keycloak/source/
docker compose --profile web up -d
