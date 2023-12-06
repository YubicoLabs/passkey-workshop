#!/bin/bash
if [ -e init.done ]; then
  echo "Configurations already initialized - Exiting script"
  exit
fi

KEYCLOAK_PATH="/opt/keycloak/bin/kcadm.sh"
CONTAINER_NAME=affectionate_carver

REALM_NAME=passkeyDemo
AUTH_FLOW_NAME=passkeyAuthFlow
REG_FLOW_NAME=passkeyRegFlow
CLIENT_NAME=passkeyClient
DOCKER_EXEC="docker exec -it $CONTAINER_NAME .$KEYCLOAK_PATH"

# client URL default:
CLIENT_URL=http://localhost:3000
[[ -z "$1" ]] || CLIENT_URL=$1
echo "Configuring keycloak client on $CLIENT_URL"

while ! /opt/keycloak/bin/kcadm.sh config credentials --server http://localhost:8080 --realm master --user admin --password admin --client admin-cli &> /dev/null ; do
    echo 'Waiting for connection'
    sleep 5
done
echo "Connection found"

/opt/keycloak/bin/kcadm.sh create realms -s realm=$REALM_NAME -s enabled=true -s registrationAllowed=true

# Create the Flow for passkey authentication
AUTH_OUTPUT=$(/opt/keycloak/bin/kcadm.sh create authentication/flows -s alias=$AUTH_FLOW_NAME -s providerId=basic-flow -s topLevel=true -r $REALM_NAME --id)
/opt/keycloak/bin/kcadm.sh create authentication/flows/$AUTH_FLOW_NAME/executions/execution -b '{"provider": "passkeyauthenticator"}' -r $REALM_NAME

# Create the Flow for passkey registration
REG_OUTPUT=$(/opt/keycloak/bin/kcadm.sh create authentication/flows -s alias=$REG_FLOW_NAME -s providerId=basic-flow -s topLevel=true -r $REALM_NAME --id)
/opt/keycloak/bin/kcadm.sh create authentication/flows/$REG_FLOW_NAME/executions/execution -b '{"provider": "passkeyregister"}' -r $REALM_NAME
# Set the new registration flow as the default for the realm
/opt/keycloak/bin/kcadm.sh update realms/$REALM_NAME -s registrationFlow=$REG_FLOW_NAME

echo $AUTH_OUTPUT
# Setting the browser flow isnt' working - look into this
echo /opt/keycloak/bin/kcadm.sh create clients -r $REALM_NAME -s clientId=$CLIENT_NAME -s protocol=openid-connect -s enabled=true -s "redirectUris=[\"$CLIENT_URL/logout\", \"/*\", \"$CLIENT_URL/oidc/callback\"]" -s 'webOrigins=["*", "/*"]' -s 'authenticationFlowBindingOverrides={"browser": "'$AUTH_OUTPUT'", "direct_grant": ""}'
# TODO: configure callbacks
/opt/keycloak/bin/kcadm.sh create clients -r $REALM_NAME -s clientId=$CLIENT_NAME -s protocol=openid-connect -s enabled=true -s "redirectUris=[\"$CLIENT_URL/logout\", \"/*\", \"$CLIENT_URL/oidc/callback\"]" -s 'webOrigins=["*", "/*"]' -s 'authenticationFlowBindingOverrides={"browser": "'$AUTH_OUTPUT'", "direct_grant": ""}'

### High Assurance banking example

REALM_NAME=BankApp
AUTH_FLOW_NAME="Passkey_auth"
REG_FLOW_NAME="Passkey_reg"
CLIENT_NAME=BankApp
MOBILE_CLIENT_NAME=BankAppMobile

# RP API URL default:
RP_API=http://localhost:8080/v1
[[ -z "$2" ]] || RP_API=$2
echo "Configuring RP API backend URL on $RP_API"

# Bank client URL default:
BANK_CLIENT_URL=http://localhost:3002
[[ -z "$3" ]] || BANK_CLIENT_URL=$3
echo "Configuring keycloak bank client on $BANK_CLIENT_URL"

# Bank mobile client URI default:
BANK_MOBILE_CLIENT_URI=pkbank://
[[ -z "$3" ]] || BANK_CLIENT_URL=$3
echo "Configuring keycloak bank mobile client on $BANK_MOBILE_CLIENT_URI"

/opt/keycloak/bin/kcadm.sh create realms \
	-s realm=$REALM_NAME \
	-s enabled=true \
	-s registrationAllowed=true

# Create the Flow for passkey authentication
AUTH_OUTPUT=$(/opt/keycloak/bin/kcadm.sh create authentication/flows \
	-s alias="$AUTH_FLOW_NAME" \
	-s providerId=basic-flow \
	-s topLevel=true \
	-r $REALM_NAME --id)
echo Authenticion flow ID: $AUTH_OUTPUT
AUTH_EXEC=$(/opt/keycloak/bin/kcadm.sh create authentication/flows/$AUTH_FLOW_NAME/executions/execution \
	-b '{"provider": "passkey-authenticate"}' \
	-r $REALM_NAME \
	--id)
echo Authenticion Execution ID: $AUTH_EXEC
/opt/keycloak/bin/kcadm.sh create authentication/executions/$AUTH_EXEC/config \
	-r $REALM_NAME \
	-b "{\"alias\":\"authConfig\",\"config\":{\"webauthn-api-url\":\"$RP_API\"}}"

# Create the Flow for passkey registration
REG_OUTPUT=$(/opt/keycloak/bin/kcadm.sh create authentication/flows \
	-s alias="$REG_FLOW_NAME" \
	-s providerId=basic-flow \
	-s topLevel=true \
	-r $REALM_NAME \
	--id)
echo Registration flow ID: $REG_OUTPUT
REG_EXEC=$(/opt/keycloak/bin/kcadm.sh create authentication/flows/$REG_FLOW_NAME/executions/execution \
	-b '{"provider": "passkey-register"}' \
	-r $REALM_NAME \
	--id)
echo Registration Execution ID: $REG_EXEC
/opt/keycloak/bin/kcadm.sh create authentication/executions/$REG_EXEC/config \
	-r $REALM_NAME \
	-b "{\"alias\":\"regConfig\",\"config\":{\"webauthn-api-url\":\"$RP_API\"}}"

# Set the new registration and browser flows as the default for the realm
/opt/keycloak/bin/kcadm.sh update realms/$REALM_NAME \
	-s registrationFlow="$REG_FLOW_NAME"
/opt/keycloak/bin/kcadm.sh update realms/$REALM_NAME \
	-s browserFlow=$AUTH_FLOW_NAME

CLIENT_ID=$(/opt/keycloak/bin/kcadm.sh create clients \
	-r $REALM_NAME \
	-s clientId=$CLIENT_NAME \
	-s protocol=openid-connect \
	-s enabled=true \
	-s "redirectUris=[\"$BANK_CLIENT_URL/callback/auth\"]" \
	-s "webOrigins=[\"$BANK_CLIENT_URL\"]" \
	--id)
echo Client ID: $CLIENT_ID

MOBILE_CLIENT_ID=$(/opt/keycloak/bin/kcadm.sh create clients \
	-r $REALM_NAME \
	-s clientId=$MOBILE_CLIENT_NAME \
	-s protocol=openid-connect \
	-s enabled=true \
	-s "redirectUris=[\"$BANK_MOBILE_CLIENT_URI*\"]" \
	-s "webOrigins=[\"$BANK_MOBILE_CLIENT_URI\"]" \
	--id)
echo Client ID: $MOBILE_CLIENT_ID

/opt/keycloak/bin/kcadm.sh update clients/$CLIENT_ID -r $REALM_NAME -s "attributes={\"post.logout.redirect.uris\": \"$BANK_CLIENT_URL\"}"
/opt/keycloak/bin/kcadm.sh update clients/$MOBILE_CLIENT_ID -r $REALM_NAME -s "attributes={\"post.logout.redirect.uris\": \"$BANK_MOBILE_CLIENT_URI\"}"

# Set login theme
/opt/keycloak/bin/kcadm.sh update realms/$REALM_NAME -s loginTheme=passkey

### Finish

echo "Finalizing initalization"
> init.done
echo "Keycloak initialized"
