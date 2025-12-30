#!/bin/bash
# Keycloak initialization script for pre-reg example
# Creates realm, client, and test user automatically

if [ -e /tmp/init.done ]; then
  echo "Keycloak already initialized - skipping"
  exit 0
fi

echo "Waiting for Keycloak to be ready..."
while ! /opt/keycloak/bin/kcadm.sh config credentials --server http://localhost:8080 --realm master --user admin --password admin --client admin-cli &> /dev/null ; do
    echo 'Waiting for Keycloak connection...'
    sleep 5
done
echo "Keycloak is ready!"

REALM_NAME=pre-reg
CLIENT_NAME=pre-reg-client
CLIENT_URL=${CLIENT_URL:-http://localhost}

echo "Creating realm: $REALM_NAME"
/opt/keycloak/bin/kcadm.sh create realms \
    -s realm=$REALM_NAME \
    -s enabled=true \
    -s registrationAllowed=true

echo "Creating client: $CLIENT_NAME"
CLIENT_ID=$(/opt/keycloak/bin/kcadm.sh create clients \
    -r $REALM_NAME \
    -s clientId=$CLIENT_NAME \
    -s protocol=openid-connect \
    -s enabled=true \
    -s publicClient=true \
    -s standardFlowEnabled=true \
    -s directAccessGrantsEnabled=true \
    -s "redirectUris=[\"$CLIENT_URL/*\"]" \
    -s "webOrigins=[\"$CLIENT_URL\"]" \
    --id)
echo "Client ID: $CLIENT_ID"

# Create a test user for development
echo "Creating test user: testuser"
USER_ID=$(/opt/keycloak/bin/kcadm.sh create users \
    -r $REALM_NAME \
    -s username=testuser \
    -s email=testuser@example.com \
    -s emailVerified=true \
    -s enabled=true \
    -s firstName=Test \
    -s lastName=User \
    --id)
echo "User ID: $USER_ID"

# Set password for test user
/opt/keycloak/bin/kcadm.sh set-password \
    -r $REALM_NAME \
    --username testuser \
    --new-password testuser

echo "==================================="
echo "Keycloak initialization complete!"
echo "==================================="
echo "Realm: $REALM_NAME"
echo "Client: $CLIENT_NAME"
echo "Test user: testuser / testuser"
echo "==================================="

touch /tmp/init.done
