#!/bin/bash

CONFIG_FILE="DeployProject.conf"

# include configuration file
source $CONFIG_FILE

# Create a random number for repeated automated deployment
function getRandomValue {
    echo $(ping -c 1 yubico.com |md5 | head -c$1; echo)
}

echo -e "\n****************************************"
echo "Initializing application environment variables"

# ------------------------------------------------
# DECLARE SYSTEM VARIABLES BASED ON SETTINGS FILE
# ------------------------------------------------

# ------------------------------------------------
# Options: custom
# Denotes the RP ID used by your application
# ------------------------------------------------
RP_ID="${RP_ID:-localhost}"

# ------------------------------------------------
# Options: custom
# Denotes the RP NAME used by your application
# ------------------------------------------------
RP_NAME="${RP_NAME:-My app}"

# ------------------------------------------------
# Options: custom
# Denotes the origins allowed to submit registrations to your app
# Current script allows for one allowed origins, without the protocol heading
# ex. localhost:3000 will allow both http://localhost:3000 and https://localhost:3000
# ------------------------------------------------
RP_ALLOWED_ORIGINS="${RP_ALLOWED_ORIGINS:-localhost:3000}"

# ------------------------------------------------
# Options: custom
# Denotes the origins allowed to access the API (CORS)
# ex. localhost:3000 will allow both http://localhost:3000 and https://localhost:3000
# ------------------------------------------------
RP_ALLOWED_CROSS_ORIGINS="${RP_ALLOWED_CROSS_ORIGINS:-localhost:3000}"

# ------------------------------------------------
# Options: DIRECT, INDIRECT, ENTERPRISE, NONE
# Values should be all caps to be utilized by java webauthn library
# ------------------------------------------------
RP_ATTESTATION_PREFERENCE="${RP_ATTESTATION_PREFERENCE:-DIRECT}"

# ------------------------------------------------
# Options: true/false
# Will denote if your application will require attstation
# for every new registration
# ------------------------------------------------
RP_ALLOW_UNTRUSTED_ATTESTATION="${RP_ALLOW_UNTRUSTED_ATTESTATION:-true}"

# ------------------------------------------------
# Options: mds, none
# Will denote if your application will leverage attestation
# Through the FIDO MDS
# ------------------------------------------------
RP_ATTESTATION_TRUST_STORE="${RP_ATTESTATION_TRUST_STORE:-mds}"

# ------------------------------------------------
# Options: local
# Currently local is the only option, this can be used
# to create logic that will change based on your
# development environment
# ------------------------------------------------
DEPLOYMENT_ENVIRONMENT="${DEPLOYMENT_ENVIRONMENT:-local}"

# ------------------------------------------------
# Options: in-mem, mysql
# local will leverage in memory data storage, all registrations will be lost when the application is shut down
# mysql will deploy a local instance of mysql through Docker
# ------------------------------------------------
DATABASE_TYPE="${DATABASE_TYPE:-mysql}"

# ------------------------------------------------
# Options: custom string
# Allow you to set your own database password, or generate a random one
# ------------------------------------------------
DATABASE_ROOT_PASSWORD="${DATABASE_ROOT_PASSWORD:-$(getRandomValue 16)}"

# ------------------------------------------------
# Options: react | none
# Allow you to choose the client you will be using for testing
# none - deploys only the java app
# ------------------------------------------------
CLIENT_TYPE="${CLIENT_TYPE:-react}"

# ------------------------------------------------
# Options: keycloak | none
# Allow you to choose the IDP you will be using for testing
# none - no IDP deployed
# keycloak - initializes local instance of keycloak
# ------------------------------------------------
IDP_TYPE="${IDP_TYPE:-keycloak}"

echo -e "\nVariables initialized"
echo "****************************************"

# If deploying mysql, ensure that the DB is instantiated before deploying the java app
if [ "$DATABASE_TYPE" == "mysql" ]; then
  echo -e "\n****************************************"
  echo "MYSQL database requested - creating local mysql instance"
  (cd mysql && ./deploy_mysql.sh $DATABASE_ROOT_PASSWORD)
  echo -e "\n mysql instance created"
  echo "****************************************"
fi

if [ "$DEPLOYMENT_ENVIRONMENT" == "local" ]; then
  echo -e "\n****************************************"
  echo "Deploying java application in Docker"

  (cd java-app && ./deploy_java_app.sh "$RP_ID" \
    "$RP_NAME" \
    "$RP_ALLOWED_ORIGINS" \
    "$RP_ALLOWED_CROSS_ORIGINS" \
    "$RP_ATTESTATION_PREFERENCE" \
    "$RP_ALLOW_UNTRUSTED_ATTESTATION" \
    "$DEPLOYMENT_ENVIRONMENT" \
    "$DATABASE_TYPE" \
    "$DATABASE_ROOT_PASSWORD" \
    "$RP_ATTESTATION_TRUST_STORE" \
  )
  echo -e "\n Java application deployed"
  echo "****************************************"
fi

if [ "$IDP_TYPE" == "keycloak" ]; then
  echo -e "\n****************************************"
  echo "Deploying keycloak in docker"

  (cd keycloak && ./deploy_keycloak.sh  )

  echo -e "\n Keycloak deployed"
  echo "****************************************"
fi

if [ "$CLIENT_TYPE" == "react" ]; then
  echo -e "\n****************************************"
  echo "Deploying React app in docker"

  (cd react-app && ./deploy_react_app.sh  )

  echo -e "\n React application deployed"
  echo "****************************************"
fi
