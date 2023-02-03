#!/bin/bash

# common configuration settings for OAuth2 client (CLI), resource server (API), and Authorization Server

# Client:

CLIENT_ID="myclient"
#CLIENT_SECRET="93sm81WqQv93kvcUhfjYxP4bsPvVofee" # for confidential clients
CLIENT_SECRET= # for public clients
PORT=8000
REDIRECT_URI=http://localhost:$PORT
TOKENFILE=token.json

# Resource Server:

API=http://localhost:3000/secured

# Authorization server:

OIDC_CONF=http://localhost:8080/realms/myrealm/.well-known/openid-configuration
ISSUER=$(curl -s $OIDC_CONF | jq -r .issuer)
AUTHZ_URL=$(curl -s $OIDC_CONF | jq -r .authorization_endpoint)
TOKEN_URL=$(curl -s $OIDC_CONF | jq -r .token_endpoint)
INTRO_URL=$(curl -s $OIDC_CONF | jq -r .introspection_endpoint)
JWKS=$(curl -s $OIDC_CONF | jq -r .jwks_uri)
