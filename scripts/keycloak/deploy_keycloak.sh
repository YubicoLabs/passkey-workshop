#!/bin/bash

# Generating database params
echo "Composing Keycloak"

if [ ! -e build/passkey_authenticator.jar ]; then
  mkdir build && cp -r ../../examples/IdentityProviders/KeyCloak/pre-build/passkey_authenticator.jar ./build/
fi

docker-compose up -d
