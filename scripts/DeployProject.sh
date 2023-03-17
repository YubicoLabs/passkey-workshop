#!/bin/bash

# Each parameter enters here. If no param specified (empty) in deployStarterKit.json, create a temp name (passed in as $2)
# EXAMPLE call to this function: AWS_REGION=$(getParam AWS_REGION us-east-1); us-east-1 is passed in as the default value, if none specified and read as $2
function getParam {
    DEFVALUE=$(grep $1 $CONFIG_FILE | sed -e 's/.*://;s/"//g;s/,//g')
    echo $(if test -z $(grep $1 $CONFIG_FILE | sed -e 's/.*://;s/"//g;s/,//g')
        then echo $2
        else echo $DEFVALUE
    fi)
}

function getParamAllowOrigins {
  ##@TODO - this needs to account for https - don't append http/https, add both origins to the backend s/\"//g;"
    DEFVALUE=$(grep $1 $CONFIG_FILE | sed -e "s/\"$1\": //;s/\"//g;" | sed 's/,*$//g')
    echo $(if test -z $(grep $1 $CONFIG_FILE | sed -e 's/.*://;s/"//g;s/,//g')
        then echo $2
        else echo $DEFVALUE
    fi)
}

# Create a random number for repeated automated deployment
function getRandomValue {
    echo $(ping -c 1 yubico.com |md5 | head -c$1; echo)
}

echo -e "\n****************************************"
echo "Initializing application environment variables"

CONFIG_FILE="DeployProject_Settings.json"

RP_ALLOWED_ORIGINS=$(getParamAllowOrigins RP_ALLOWED_ORIGINS "localhost:3000")

# ------------------------------------------------
# DECLARE SYSTEM VARIABLES BASED ON SETTINGS FILE
# ------------------------------------------------

# ------------------------------------------------
# Options: custom
# Denotes the RP ID used by your application
# ------------------------------------------------
RP_ID=$(getParam RP_ID localhost)

# ------------------------------------------------
# Options: custom
# Denotes the RP NAME used by your application
# ------------------------------------------------
RP_NAME=$(getParam RP_NAME "My app")

# ------------------------------------------------
# Options: custom
# Denotes the origins allowed to submit registrations to your app
# Current script allows for one allowed origins, without the protocol heading
# ex. localhost:3000 will allow both http://localhost:3000 and https://localhost:3000
# ------------------------------------------------
RP_ALLOWED_ORIGINS=$(getParamAllowOrigins RP_ALLOWED_ORIGINS "localhost:3000")

# ------------------------------------------------
# Options: custom
# Denotes the origins allowed to access the API (CORS)
# ex. localhost:3000 will allow both http://localhost:3000 and https://localhost:3000
# ------------------------------------------------
RP_ALLOWED_CROSS_ORIGINS=$(getParamAllowOrigins RP_ALLOWED_CROSS_ORIGINS "localhost:3000")

# ------------------------------------------------
# Options: DIRECT, INDIRECT, ENTERPRISE, NONE
# Values should be all caps to be utilized by java webauthn library
# ------------------------------------------------
RP_ATTESTATION_PREFERENCE=$(getParam RP_ATTESTATION_PREFERENCE DIRECT)

# ------------------------------------------------
# Options: true/false
# Will denote if your application will require attstation
# for every new registration
# ------------------------------------------------
RP_ALLOW_UNTRUSTED_ATTESTATION=$(getParam RP_ALLOW_UNTRUSTED_ATTESTATION true)

# ------------------------------------------------
# Options: local
# Currently local is the only option, this can be used
# to create logic that will change based on your
# development environment
# ------------------------------------------------
DEPLOYMENT_ENVIRONMENT=$(getParam DEPLOYMENT_ENVIRONMENT local)

# ------------------------------------------------
# Options: in-mem, mysql
# local will leverage in memory data storage, all registrations will be lost when the application is shut down
# mysql will deploy a local instance of mysql through Docker
# ------------------------------------------------
DATABASE_TYPE=$(getParam DATABASE_TYPE mysql)

# ------------------------------------------------
# Options: custom string
# Allow you to set your own database password, or generate a random one
# ------------------------------------------------
DATABASE_ROOT_PASSWORD=$(getParam DATABASE_ROOT_PASSWORD $(getRandomValue 16) )

# ------------------------------------------------
# Options: react | none
# Allow you to choose the client you will be using for testing
# none - deploys only the java app
# ------------------------------------------------
CLIENT_TYPE=$(getParam CLIENT_TYPE react )

# ------------------------------------------------
# Options: keycloak | none
# Allow you to choose the IDP you will be using for testing
# none - no IDP deployed
# keycloak - initializes local instance of keycloak
# ------------------------------------------------
IDP_TYPE=$(getParam IDP_TYPE keycloak )

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
