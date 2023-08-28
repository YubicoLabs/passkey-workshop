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
/opt/keycloak/bin/kcadm.sh update realms/$REALM_NAME -s registrationFlow=passkeyRegFlow

echo $AUTH_OUTPUT
# Setting the browser flow isnt' working - look into this
echo /opt/keycloak/bin/kcadm.sh create clients -r $REALM_NAME -s clientId=$CLIENT_NAME -s protocol=openid-connect -s enabled=true -s "redirectUris=[\"$CLIENT_URL/logout\", \"/*\", \"$CLIENT_URL/oidc/callback\"]" -s 'webOrigins=["*", "/*"]' -s 'authenticationFlowBindingOverrides={"browser": "'$AUTH_OUTPUT'", "direct_grant": ""}'
# TODO: configure callbacks
/opt/keycloak/bin/kcadm.sh create clients -r $REALM_NAME -s clientId=$CLIENT_NAME -s protocol=openid-connect -s enabled=true -s "redirectUris=[\"$CLIENT_URL/logout\", \"/*\", \"$CLIENT_URL/oidc/callback\"]" -s 'webOrigins=["*", "/*"]' -s 'authenticationFlowBindingOverrides={"browser": "'$AUTH_OUTPUT'", "direct_grant": ""}'

echo "Finalizing initalization"
> init.done
echo "Keycloak initialized"
