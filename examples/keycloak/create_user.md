# Using password credentials

Master realm, `admin-cli` client settings:

- "Direct access grants" enabled means support of "Resource Owner Password Credentials Grant" for this client.

Get an access token from the KeyCloak token endpoint using the ROPC Grant:

    curl	\
	--location	\
	--request POST 'http://localhost:8080/realms/master/protocol/openid-connect/token'	\
	--header 'Content-Type: application/x-www-form-urlencoded'	\
	--data-urlencode 'grant_type=password'	\
	--data-urlencode 'client_id=admin-cli'	\
	--data-urlencode 'username=admin'	\
	--data-urlencode 'password=admin'	\
	> admin.token

# Using client credentials

New realm "myclient", 

OAuth 2 Client application type must be set to "confidential"

- the “Service Accounts Enabled” option is turned on.

- Switch "Client authentication" On, in order to set the OIDC type to confidential access type.
- Check the "Service accounts roles" option in oder to enable support of the 'Client Credentials Grant' for this client.
- retrieve the client credential from the Credentials tab.

Get an access token from the KeyCloak token endpoint using the Client Credentials Grant:


    curl	\
	--location	\
	--request POST 'http://localhost:8080/realms/master/protocol/openid-connect/token'	\
	--header 'Content-Type: application/x-www-form-urlencoded'	\
	--data-urlencode 'grant_type=client_credentials'	\
	--data-urlencode 'client_id=admin-cli'	\
	--data-urlencode 'client_secret=7fb49e15-2a86-4b7c-a648-27746c67895b'	\
	> admin.token

# Create a User


    curl	\
	--location	\
	--request POST 'http://localhost:8080/admin/realms/myclient/users' \
	--header 'Content-Type: application/json'	\
	--header 'Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJFY2dlX3Y0c09fZUZ0TnhqYWJjT19QLTBhQ3p6S2VfMW02OU5mRjlBc1VzIn0.eyJleHAiOjE1OTI1Njc4OTEsImlhdCI6MTU5MjU2NzgzMSwianRpIjoiNjJiMWRlODEtNTBhMS00NzA2LWFmN2MtYzhhZTc1YTg1OTJhIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwL2F1dGgvcmVhbG1zL21hc3RlciIsInN1YiI6IjNmYjc1YTM4LTA4NjctNGZlYi04ZTBlLWYzMTkxZTZlODZlMSIsInR5cCI6IkJlYXJlciIsImF6cCI6ImFkbWluLWNsaSIsInNlc3Npb25fc3RhdGUiOiJhMDMwNGNiMS0xMzViLTQzODItYjYwMi0xNjNmNzgzYWNlN2IiLCJhY3IiOiIxIiwic2NvcGUiOiJlbWFpbCBwcm9maWxlIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJhZG1pbjIifQ.G9-OiyrGWk8cY4S3Ho255Y_euk_gTKDgYmGmU8RPBSeBNtFb_A68tuPFJxFKbzhZ1lipKJCXQsHbStoihvXAmmRsKzud5hDIvvnrD7CcVxAIpbd2wV5i6mB2wVLocV0_FCrE0-DNi_GPAKnazjFiVu3TxxM2L8Zsw7PHN9sb8Ux_lRvAFyNY5bT7NTbmEmt6LsO2An7iTZdBLScK9Lk9ZW8_0WG4eLMy9fatrpVV3MXhINW56gZD8WsWISY0m-cbIftDreZ1f2lzIjMGfkDrgCjy-VZjeIpbmffN-YGrUVywziymBRwA7FFLAxcf6jS5548HVxxKeMPIvNEfDG7eWw'	\
	--data-raw '{"firstName":"John","lastName":"Doe", "email":"jd@example.com", "enabled":"true", "username":"john"}'

