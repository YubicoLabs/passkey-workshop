#!/bin/bash

# Validate the  access token in $TOKENFILE

# macos: brew install jq
# debian/ubuntu: apt install jq

source common.sh

if ! [ -f "$TOKENFILE" ]
then
  echo "generate $TOKENFILE first"
  exit
fi

curl -s $JWKS > jwks.json


# TODO: API audience
AUDIENCE=account

echo === Validate access token locally:

cat $TOKENFILE | jq --raw-output .access_token | step crypto jwt verify --jwks jwks.json --issuer $ISSUER --audience $AUDIENCE

[[ -z "$CLIENT_SECRET" ]] && exit	# stop here if public client

# for remote validation, we need a confidential client with a client secret
echo === Validate access token remotely:

INTRO=$(curl -sX POST $INTRO_URL \
  -d client_id=$CLIENT_ID \
  -d client_secret=$CLIENT_SECRET \
  -d token=$(jq --raw-output .access_token $TOKENFILE))

echo $INTRO | jq
echo Active: ; echo $INTRO | jq .active
