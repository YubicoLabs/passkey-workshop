#!/bin/bash

# use a default environment file unless already present
if [[ ! -f ".env" ]] ; then
  echo "### copying default environment file"
  cp tunnel.env ".env"
fi

# load current environment
source ".env"

# Apple dev team needs to be changed
if grep -q "UVWXYZ1234" ".env"; then
  echo "Please edit your .env file and fill in your DEVELOPMENT_TEAM"
  exit
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
if [[ -z "$rebuild" ]] ; then
  echo "### rebuilding passkey-client image"
  sed -i '' "s/A6586UA84V/$DEVELOPMENT_TEAM/" react-app/source/public/.well-known/apple-app-site-association
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
sed -i '' "s#http://localhost:8080#https://$hostname#"	../examples/clients/mobile//iOS/PawsKey/Shared/RelyingParty.swift 
sed -i '' "s/passkey.fyi/$hostname/"	../examples/clients/mobile/iOS/PawsKey/Shared/AccountManager.swift
sed -i '' "s/passkey.fyi/$hostname/"	../examples/clients/mobile/iOS/PawsKey/Shared/PawsKey.entitlements
sed -i '' "s/passkey.fyi/$hostname/"	../examples/clients/mobile/iOS/PawsKey/PawsKeyDebug.entitlements

echo "### launching containers"
docker compose --profile mobile up -d
