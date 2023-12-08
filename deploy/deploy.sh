#!/bin/bash

# This script automates the deployment of all components

# make sure we have docker compose v2
echo "### checking docker compose version"
docker compose version | grep v2 || { echo docker compose v2 is required; exit; }

# use a default environment file unless already present
if [[ ! -f ".env" ]] ; then
  echo "### .env file missing - copying default environment file"
  cp default.env ".env"
fi

# load current environment
source ".env"

if [ "$DEPLOYMENT_ENVIRONMENT" == "devtunnel" ]; then 
  echo "### checking for devtunnel"
  command -v devtunnel || { echo please install devtunnel first; exit; }
  devtunnel user show | grep '^Not logged in' && { echo "logon to devtunnel first ('devtunnel user login')"; exit; }
fi

# a DEVELOPMENT_TEAM is required to build iOS apps (only makes sense for MacOS)
if [ "$(uname)" == "Darwin" ]; then
	echo "### checking Apple Developer Team ID"
	# Apple dev team needs to be changed, retrieve from .env file or from keychain...
	if [[ -z "$DEVELOPMENT_TEAM" ]] ; then
	  # dev team is in OU field of subject DN in dev certificate
	  DEVELOPMENT_TEAM=$(security find-certificate -a -c 'Apple Development: ' -p | awk -v cmd='openssl x509 -noout -subject' '/BEGIN/{close(cmd)}; {print | cmd}' | egrep -o 'OU[[:blank:]]*=[[:blank:]]*[A-Z0-9]{10,}' | tr -d '[[:blank:]]' | cut -d= -f2)
	  N=$(echo $DEVELOPMENT_TEAM | wc -w)
	  if [[ $N -lt 1 ]] ; then
	    echo Cannot find a DEVELOPMENT_TEAM
	    echo "Please edit your .env file and fill in your DEVELOPMENT_TEAM"
	    exit
	  fi
	  if [[ $N -gt 1 ]] ; then
	    echo You have more than one Team ID
	    echo "Please edit your .env file and fill in your DEVELOPMENT_TEAM"
	    echo We found the following Team IDs in your KeyChain: $DEVELOPMENT_TEAM
	    security find-certificate -a -c 'Apple Development:' -p | awk -v cmd='openssl x509 -noout -subject'   '/BEGIN/{close(cmd)}; {print | cmd}' | tr '/,' '\n' | grep -e CN -e OU | cut -d= -f2
	    exit
	  fi
	fi
	# use the team ID found
	echo DEVELOPMENT_TEAM=$DEVELOPMENT_TEAM
fi

# stop and remove any running containers as they may need to be restarted
echo "### removing any running containers"
docker compose stop
docker compose rm

# copy sources so they can be copied into docker images

# frontend
echo "### copying relying party front-end source code"
cp -r ../examples/clients/web/react/passkey-client/ react-app/source/

# backend
echo "### copying relying party back-end source code"
cp -r  ../examples/relyingParties/java-spring/ java-app/source/

# IdP
echo "### copying keycloak passkey authenticator source code"
cp -r ../examples/IdentityProviders/KeyCloak/passkey_authenticator/ keycloak/source/
cp -r ../examples/IdentityProviders/KeyCloak/passkey_spi/ keycloak/bank_source/

# bank application
echo "### copying bank application source code"
cp -r ../examples/clients/web/react/bank-client/ bank-react-app/source/
cp -r ../examples/high_assurance/bank_app/ bank-java-app/source/

# TODO: use profiles to select containers to run
#       depending on DEPLOYMENT_CLIENTS and DATABASE_TYPE

# modify and rebuild the demo web app
rebuild=$(grep A6586UA84V react-app/source/public/.well-known/apple-app-site-association)
if [[ ! -z "$rebuild" ]] ; then
  echo "### rebuilding passkey-client image"
  sed -i '' "s/A6586UA84V/$DEVELOPMENT_TEAM/g" react-app/source/public/.well-known/apple-app-site-association
  docker compose build passkey-client
fi

# deploy using a devtunnel
if [ "$DEPLOYMENT_ENVIRONMENT" == "devtunnel" ]; then 
	echo "### bringing up the tunnel"

	TUNNELID=$(devtunnel list --labels passkey-workshop --limit 1 | grep passkey-workshop | awk '{ print $1; }')
	if [[ -z "$TUNNELID" ]] ; then
		echo "### create tunnel"
		devtunnel create --allow-anonymous --labels passkey-workshop --host-header unchanged --origin-header unchanged
	fi
	TUNNELID=$(devtunnel list --labels passkey-workshop --limit 1 | grep passkey-workshop | awk '{ print $1; }')

	echo "### setting up ports"
	devtunnel port list $TUNNELID | grep '^3000\b' || devtunnel port create $TUNNELID  -p 3000 --description 'app'
	devtunnel port list $TUNNELID | grep '^8080\b' || devtunnel port create $TUNNELID  -p 8080 --description 'api'
	devtunnel port list $TUNNELID | grep '^8081\b' || devtunnel port create $TUNNELID  -p 8081 --description 'idp'
	devtunnel port list $TUNNELID | grep '^3002\b' || devtunnel port create $TUNNELID  -p 3002 --description 'bank-client'
	devtunnel port list $TUNNELID | grep '^8082\b' || devtunnel port create $TUNNELID  -p 8082 --description 'bank-api'

	hostname=$TUNNELID.devtunnels.ms
	echo "### tunnel hostname is: $(tput bold) $hostname $(tput sgr0)"

	# hostname is of the form <host>.<region>.devtunnels.ms
	HOST=$(echo $hostname | cut -d. -f1)
	REGION=$(echo $hostname | cut -d. -f2-)
	# Note: as passkeys can be both registered in Keycloak _and_ added from the banking client, they need to share an RP_ID
	# When using devtunnels, this means the RP_ID must match <region>.devtunnels.ms. Beware that when your tunnel endpoint changes,
	#	passkeys from previous tunnels will be shared as well. You may want to remove those first.
	RP_ID=$REGION
	echo "### RP_ID is: $RP_ID"

	echo "### editing .env file"
	sed -i '' -E "/^#/!s#localhost:([0-9]+)#$HOST-\1.$REGION#g" .env
	sed -i '' -E "s#host.docker.internal:([0-9]+)#$HOST-\1.$REGION#g" .env
	sed -i '' 's#http://#https://#' .env
	sed -i '' "/^RP_ID=/s/localhost/$RP_ID/" .env

	echo "### editing Pawskey sources"
	sed -i '' "s/A6586UA84V/$DEVELOPMENT_TEAM/"	../examples/clients/mobile/iOS/PawsKey/PawsKey.xcodeproj/project.pbxproj
	sed -i '' \
		-e "s#^API_BASE_URI[= ].*#API_BASE_URI = $hostname:8080/v1#" \
		-e "s#^RP_ID[= ].*#RP_ID = $hostname#" \
		../examples/clients/mobile/iOS/PawsKey/Constants.xcconfig

	# TODO: instead of editing source files, make endpoints configurable
	sed -i '' "s#http://host.docker.internal#https://$hostname#;s#http://localhost#https://$hostname#" keycloak/source/src/main/java/com/yubicolabs/PasskeyAuthenticator/PasskeyAuthenticator.java
	sed -i '' "s#http://host.docker.internal#https://$hostname#;s#http://localhost#https://$hostname#" keycloak/source/src/main/java/com/yubicolabs/PasskeyAuthenticator/PasskeyRegistrationAuthenticator.java

	echo "### launching containers (this may take a minute)"
	docker compose up -d

	echo "### starting devtunnel. Type ^C to stop the tunnel and take down all containers"
	devtunnel host $TUNNELID

	docker compose down
	exit
fi

# default is deploy on localhost

if (echo $DEPLOYMENT_CLIENTS | tr ',' '\n' | grep -Fqx demo); then
	echo your demo application will be deployed here:
	echo http://localhost:3000/test_panel
fi

if (echo $DEPLOYMENT_CLIENTS | tr ',' '\n' | grep -Fqx bank); then
	echo your bank application will be deployed here:
	echo http://localhost:3002/
fi

echo "### starting containers. Type 'docker compose down' to take down all containers"
docker compose up -d
