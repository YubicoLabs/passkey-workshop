#!/bin/bash

# obtain an access (and refesh) token and store it in $TOKENFILE

source common.sh

# Use PKCE to prevent authorization code injection attacks:
CODE_VERIFIER=$(cat /dev/random | head -c24 | xxd -p)
CODE_CHALLENGE=$(echo -n $CODE_VERIFIER | openssl dgst -sha256 -binary | base64 | tr '/+' '_-' | sed 's/=*$//')

# State is normally used for CSRF, but here we use PKCE for that, so we use a dummy state
STATE=dummy

# Initiate authorization code flow:
open "$AUTHZ_URL?response_type=code&client_id=$CLIENT_ID&state=$STATE&redirect_uri=$REDIRECT_URI&code_challenge=$CODE_CHALLENGE&code_challenge_method=S256"

# obtain authorization code through front-channel:
AUTHORIZATION_CODE=$(echo -e "HTTP/1.1 200 OK\n\nSuccess - please close this browser window and return to your client" \
	| nc -l $PORT \
	| head -1  | cut -d' ' -f2 | cut -d'?' -f2 | tr '&' '\n' | grep ^code | cut -d= -f2)
echo "# AUTHORIZATION_CODE $AUTHORIZATION_CODE"
# ignoring state here...

# Exchange authorization code for an access token:
curl -sX POST $TOKEN_URL \
  -d grant_type=authorization_code \
  -d redirect_uri=$REDIRECT_URI \
  -d client_id=$CLIENT_ID \
  -d client_secret=$CLIENT_SECRET \
  -d code_verifier=$CODE_VERIFIER \
  -d code=$AUTHORIZATION_CODE > $TOKENFILE

ACCESS_TOKEN=$(cat $TOKENFILE | jq -r .access_token)
echo "# ACCESS_TOKEN=$ACCESS_TOKEN"
