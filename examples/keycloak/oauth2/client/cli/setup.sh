#!/bin/bash

PORT=8000

curl --location \
	--silent \
	--header 'Content-Type: application/x-www-form-urlencoded' \
	--data-urlencode 'grant_type=password' \
	--data-urlencode 'client_id=admin-cli' \
	--data-urlencode 'username=admin' \
	--data-urlencode 'password=admin' \
	--request POST \
	"http://localhost:$PORT/realms/master/protocol/openid-connect/token"  > admin.token
TOKEN=$(cat admin.token  |jq -r .access_token)

# generate new client-secret for messaging-client
echo generating client secret...
# TODO lookup clientid
secret=$(curl -s http://localhost:$PORT/admin/realms/demo/clients/51235b7f-8ba8-4811-bca9-2ddb9bf2310b/client-secret --header "Authorization: Bearer $TOKEN"  -X POST)

CLIENT_SECRET=$(echo $secret | jq -r .value)
CLIENT_ID="messaging-client"
echo $CLIENT_ID:$CLIENT_SECRET
sed -i '' "/^CLIENT_SECRET/s/=.*/=$CLIENT_SECRET/" common.sh 


# create user john with password doe

USERS=http://localhost:$PORT/admin/realms/demo/users

echo create user...
curl \
 --silent \
 --location \
 --request POST "$USERS" \
 --header 'Content-Type: application/json' \
 --header "Authorization: Bearer $TOKEN" \
 --data-raw '{"firstName":"John","lastName":"Doe", "email":"jd@example.com", "enabled":"true", "username":"john"}'

curl \
 --silent \
 --location \
 --request GET "$USERS" \
 --header 'Content-Type: application/json' \
 --header "Authorization: Bearer $TOKEN" \
 > users.json

#jq . users.json

ID=$(cat users.json  | jq -r .[].id | tail -1)

echo reset user password...
curl \
        --location \
        --request PUT "$USERS"/$ID/reset-password \
        --header "Authorization: Bearer $TOKEN" \
        --header 'Content-Type: application/json' \
        --data-raw '{"value":"doe"}'
