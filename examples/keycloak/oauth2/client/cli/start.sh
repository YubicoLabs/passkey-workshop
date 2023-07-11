#!/bin/bash

# obtain an access (and refesh) token and store it in $TOKENFILE

source common.sh

TOKENFILE=token.json

# Use PKCE to prevent authorization code injection attacks:
CODE_VERIFIER=$(cat /dev/random | head -c24 | xxd -p)
CODE_CHALLENGE=$(echo -n $CODE_VERIFIER | openssl dgst -sha256 -binary | base64 | tr '/+' '_-' | sed 's/=*$//')

# State is normally used for CSRF, but here we use PKCE for that, so we use a dummy state
STATE=dummy

PORT=12345
REDIRECT_URI=http://localhost:$PORT

# Initiate authorization code flow:
open "$AUTHZ_URL?response_type=code&client_id=$CLIENT_ID&state=$STATE&redirect_uri=$REDIRECT_URI&code_challenge=$CODE_CHALLENGE&code_challenge_method=S256&scope=message:read"

# obtain authorization code through front-channel:
AUTHORIZATION_CODE=$(echo -e "HTTP/1.1 200 OK\n\nSuccess - please close this browser window and return to your client" \
	| /usr/bin/nc -l $PORT \
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

# call a secured API using the access token in $TOKENFILE
echo calling API GET
curl -s $API --header "Authorization: Bearer $ACCESS_TOKEN" -i | head -1
echo calling API POST
curl -s $API --header "Authorization: Bearer $ACCESS_TOKEN" -X POST -i | head -1
