#!/bin/bash

echo -e "\n****************************************"
echo "Building the Java application"
echo "****************************************"

echo -e "\n****************************************"
echo "Copying source folder to local directory"
echo "****************************************"
cp -r ../../examples/high_assurance/bank_app/ ./source/

# Check if there is an ENV file
# If no ENV file, generate an env file
if [ ! -e .env ]; then
  echo -e "\n****************************************"

  echo "Writing env variables to .env file"

  cat >.env <<EOL
RP_ALLOWED_CROSS_ORIGINS=$1
DATABASE_TYPE=$2
DATABASE_PASSWORD=$3
EOL

cat .env > /dev/null

echo ".env file created"
echo "Writting to application.properties"
cat .env >> ./source/src/main/resources/application.properties
echo "****************************************"

fi

echo -e "\n****************************************"
echo "Composing new docker container"
docker compose up -d
echo "Docker container composed"
echo "****************************************"