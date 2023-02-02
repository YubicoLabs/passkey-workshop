#!/bin/bash

# call a secured API using the access token in $TOKENFILE

source common.sh

if ! [ -f "$TOKENFILE" ]
then
  echo "generate $TOKENFILE first"
  exit
fi

ACCESS_TOKEN=$(cat $TOKENFILE | jq -r .access_token)

curl -s $API --header "Authorization: Bearer $ACCESS_TOKEN" | xargs echo
