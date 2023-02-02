#!/bin/bash

# refresh access token in $TOKENFILE

source common.sh

if ! [ -f "$TOKENFILE" ]
then
  echo "generate $TOKENFILE first"
  exit
fi


ACCESS_TOKEN=$(cat $TOKENFILE | jq -r .access_token)
REFRESH_TOKEN=$(cat $TOKENFILE | jq -r .refresh_token)

curl -sX POST $TOKEN_URL \
  -d grant_type=refresh_token \
  -d client_id=$CLIENT_ID \
  -d client_secret=$CLIENT_SECRET \
  -d refresh_token=$REFRESH_TOKEN \
  | tee $TOKENFILE | jq .expires_in | xargs echo access token expires in:

