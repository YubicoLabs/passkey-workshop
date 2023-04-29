#!/bin/bash

# Generating database params
echo "Composing MYSQL database"

# Check if there is an ENV file
# If no ENV file, generate an env file
if [ ! -e .env ]; then
  echo "Creating new Docker container"

  echo "Writing password to .env file"

  echo "MASTER_PASSWORD=$1" >> .env

fi

docker-compose up -d

MY_CURRENT_PASSWORD=$(cat .env | xargs | sed 's/[^=]*=//')

echo "Waiting for the MySQL server to finish initializing"
while ! docker exec -it passkey-mysql mysql -uroot -p$MY_CURRENT_PASSWORD -h localhost --protocol=TCP -e "status" &> /dev/null ; do
    echo "Waiting for database connection..."
    sleep 5
done 
