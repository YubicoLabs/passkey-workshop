#!/bin/bash

# make sure we have docker compose v2
echo "### checking docker compose version"
docker compose version | grep v2 || { echo docker compose v2 is required; exit; }

# use a default environment file unless already present
if [[ ! -f ".env" ]] ; then
  echo "### .env file missing - copying example environment file"
  cp cloudflared.env.example ".env"
fi

# load current environment
source ".env"

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
docker compose --profile web --profile mobile stop
docker compose --profile web --profile mobile rm

# copy sources so they can be copied into docker images

# frontend
if [[ ! -d react-app/source ]] ; then
  echo "### copying relying party front-end source code"
  cp -r ../examples/clients/web/react/passkey-client/ react-app/source/
fi

# backend
if [[ ! -d java-app/source ]] ; then
  echo "### copying relying party back-end source code"
  cp -r  ../examples/relyingParties/java-spring/ java-app/source/
fi

# IdP
if [[ ! -f keycloak/passkey_authenticator.jar ]] ; then
  echo "### copying keycloak passkey authenticator"
  cp ../examples/IdentityProviders/KeyCloak/pre-build/passkey_authenticator.jar keycloak/
fi

# modify and rebuild the web app
rebuild=$(grep A6586UA84V react-app/source/public/.well-known/apple-app-site-association)
if [[ ! -z "$rebuild" ]] ; then
  echo "### rebuilding passkey-client image"
  sed -i '' "s/A6586UA84V/$DEVELOPMENT_TEAM/g" react-app/source/public/.well-known/apple-app-site-association
  docker compose build passkey-client
fi

tunnel=$(docker compose --profile tunnel ps -q)
if [[ -z "$tunnel" ]] ; then
  echo "### bringing up the tunnel"
  docker compose --profile tunnel up -d
fi

# wait for tunnel
while :
do
  hostname=$(docker compose --profile tunnel logs | grep INF | grep -o '[a-z-]*\.trycloudflare\.com')
  [[ -z "$hostname" ]] || break
  echo -n "."
  sleep .5
done
hostname=$(docker compose --profile tunnel logs | grep INF | grep -o '[a-z-]*\.trycloudflare\.com' | tail -1)
echo "### tunnel hostname is: $(tput bold) $hostname $(tput sgr0)"

echo "### editing .env file"
sed -i '' "s/[a-z-]*\.trycloudflare\.com/$hostname/"	".env"

echo "### editing Pawskey sources"
sed -i '' "s/A6586UA84V/$DEVELOPMENT_TEAM/"	../examples/clients/mobile/iOS/PawsKey/PawsKey.xcodeproj/project.pbxproj
sed -i '' "s/[a-z-]*\.trycloudflare.com/$hostname/" ../examples/clients/mobile/iOS/PawsKey/Constants.xcconfig

echo "### launching containers (this may take a minute)"
docker compose --profile mobile up -d

echo please find your web application here:
echo https://$hostname/test_panel
