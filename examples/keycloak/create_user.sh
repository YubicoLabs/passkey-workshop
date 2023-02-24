#!/bin/bash

echo get access token...
curl \
 --silent \
 --location \
 --request POST 'http://localhost:8080/realms/master/protocol/openid-connect/token' \
 --header 'Content-Type: application/x-www-form-urlencoded' \
 --data-urlencode 'grant_type=password' \
 --data-urlencode 'client_id=admin-cli' \
 --data-urlencode 'username=admin' \
 --data-urlencode 'password=admin' \
 > admin.token

TOKEN=$(cat admin.token  |jq -r .access_token)
#echo $TOKEN

USERS=http://localhost:8080/admin/realms/master/users

echo create user...
curl \
 --silent \
 --location \
 --request POST "$USERS" \
 --header 'Content-Type: application/json' \
 --header "Authorization: Bearer $TOKEN" \
 --data-raw '{"firstName":"John","lastName":"Doe", "email":"jd@example.com", "enabled":"true", "username":"john"}'

echo retrieve user...
curl \
 --silent \
 --location \
 --request GET "$USERS" \
 --header 'Content-Type: application/json' \
 --header "Authorization: Bearer $TOKEN" \
 > users.json

jq . users.json

ID=$(cat users.json  | jq -r .[].id | tail -1)

echo delete user $ID ...
curl \
 --silent \
 --location \
 --request DELETE "$USERS/$ID" \
 --header 'Content-Type: application/json' \
 --header "Authorization: Bearer $TOKEN"

echo remaining users...
curl \
 --silent \
 --location \
 --request GET "$USERS" \
 --header 'Content-Type: application/json' \
 --header "Authorization: Bearer $TOKEN" \
 | jq

echo done!

