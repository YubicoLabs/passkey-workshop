# GET
curl -s -X GET http://localhost:8001/message -i | head -1
# POST
curl -s -X POST http://localhost:8001/message -i | head -1

# access token without any scopes:
export TOKEN=$(curl -sX POST messaging-client:4ohbjjPCEmNLCHR0Toe7cHAmskDnInG7@localhost:8000/realms/demo/protocol/openid-connect/token -d "grant_type=client_credentials" | jq -r .access_token)
echo $TOKEN | cut -d. -f2 | basenc -d --base64url 2>/dev/null | jq -r .scope

# GET
curl -sH "Authorization: Bearer $TOKEN" -X GET http://localhost:8001/message -i | head -1
# POST
curl -sH "Authorization: Bearer $TOKEN" -X POST http://localhost:8001/message -i | head -1

for scope in "message:read" "message:write" "message:read+message:write"
do
  export TOKEN=$(curl -sX POST messaging-client:4ohbjjPCEmNLCHR0Toe7cHAmskDnInG7@localhost:8000/realms/demo/protocol/openid-connect/token -d "grant_type=client_credentials" -d "scope=$scope" | jq -r .access_token)
  echo $TOKEN | cut -d. -f2 | basenc 2>/dev/null -d --base64url | jq -r .scope
  curl -sH "Authorization: Bearer $TOKEN" -X GET http://localhost:8001/message -i | head -1
  curl -sH "Authorization: Bearer $TOKEN" -X POST http://localhost:8001/message -i | head -1
done
