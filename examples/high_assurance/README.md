This is an example resource server for the banking API, protected with OAuth2

To test, you need Keycloak and a banking client.

## KeyCloak

You can start a fresh KeyCloak instance without any specific configuration:

    docker run -p 8081:8080 -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin quay.io/keycloak/keycloak:21.1.2 start-dev

Use the KeyClock cli tools to configure KeyCloak:

    export KEYCLOAK_HOME=/opt/keycloak/
    export PATH=$PATH:$KEYCLOAK_HOME/bin

    kcadm.sh config credentials --server http://localhost:8080 --realm master --user admin --password admin

Credentials will be stored in `$KEYCLOAK_HOME/.keycloak/kcadm.config`.

Create the bank realm:

    kcadm.sh create realms -s realm=bank -s enabled=true

Create the client:

    kcadm.sh create clients -r bank -s clientId=bankclient -s 'redirectUris=["http://localhost:12345/*"]' -s publicClient=true

Create a user to test with:

    kcadm.sh create users -r bank -s username=john -s enabled=true
    kcadm.sh set-password -r bank --username john --new-password  password

## client

Use the smallstep tools with jq as a simple client

    brew install step jq

Set variables for the client:

    export CLIENT_ID="bankclient"
    export PORT=12345

the resource server:

    export APIV1="http://localhost:8082/v1

and the authorization server:

    export OIDC_CONF=http://localhost:8081/realms/bank/.well-known/openid-configuration
    export AUTHZ_URL=$(curl -s $OIDC_CONF | jq -r .authorization_endpoint)
    export TOKEN_URL=$(curl -s $OIDC_CONF | jq -r .token_endpoint)

Get an access token:

    ACCESS_TOKEN=$(step oauth \
	--client-id $CLIENT_ID \
	--authorization-endpoint $AUTHZ_URL \
	--token-endpoint $TOKEN_URL \
	--listen=localhost:$PORT \
	| jq -r .access_token)

and call some API methods:

status:

    curl -sX 'GET' $APIV1/status --header "Authorization: Bearer $ACCESS_TOKEN" -H 'accept: application/json'

create account:

    curl -sX 'POST' $APIV1/accounts --header "Authorization: Bearer $ACCESS_TOKEN" -H 'accept: application/json' -H 'Content-Type: application/json' -d '{"accountId":123456}'

accounts:

    curl -sX 'GET' $APIV1/accounts --header "Authorization: Bearer $ACCESS_TOKEN" -H 'accept: application/json'

new account:

    ID=$(curl -sX 'GET' $APIV1/accounts --header "Authorization: Bearer $ACCESS_TOKEN" -H 'accept: application/json' | jq .accounts[].accountId)

account details:

    curl -sX 'GET' $APIV1/account/$ID --header "Authorization: Bearer $ACCESS_TOKEN" -H 'accept: application/json'

enable advanced protection:

    curl -sX 'PUT' $APIV1/account/$ID/advanced-protection --header "Authorization: Bearer $ACCESS_TOKEN" -H 'accept: application/json' -H 'Content-Type: application/json' -d '{ "enabled": true }'

advanced protection status:

    curl -sX 'GET' $APIV1/account/$ID/advanced-protection --header "Authorization: Bearer $ACCESS_TOKEN" -H 'accept: application/json'

new transaction:

    curl -sX 'POST' $APIV1/account/$ID/transactions --header "Authorization: Bearer $ACCESS_TOKEN" -H 'accept: application/json' -H 'Content-Type: application/json' -d '{ "type": "deposit", "amount": 120, "description": "birthday gift" }'

transactions:

    curl -sX 'GET' $APIV1/account/$ID/transactions --header "Authorization: Bearer $ACCESS_TOKEN" -H 'accept: application/json'


