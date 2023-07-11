#!/bin/bash

# common configuration settings for OAuth2 client (CLI), resource server (API), and Authorization Server

# Client:

CLIENT_ID="messaging-client"
CLIENT_SECRET=mf1DDZ3sEMUZhdHqf5Fl3TY6hJbDeu5y

# Resource Server:

API=http://localhost:8001/message

# Authorization server:

OIDC_CONF=http://localhost:8000/realms/demo/.well-known/openid-configuration
ISSUER=$(curl -s $OIDC_CONF | jq -r .issuer)
AUTHZ_URL=$(curl -s $OIDC_CONF | jq -r .authorization_endpoint)
TOKEN_URL=$(curl -s $OIDC_CONF | jq -r .token_endpoint)
INTRO_URL=$(curl -s $OIDC_CONF | jq -r .introspection_endpoint)
JWKS=$(curl -s $OIDC_CONF | jq -r .jwks_uri)
