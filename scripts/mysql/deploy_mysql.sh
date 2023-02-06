#!/bin/bash

function getRandomValue {
    echo $(ping -c 1 yubico.com |md5 | head -c$1; echo)
}

# Generating databse params
echo "Generating database name, and master password"
DATABASE_NAME=$(getParam DATABASE_NAME webauthnkitdb$SUFFIX | sed 'y/ABCDEFGHIJKLMNOPQRSTUVWXYZ/abcdefghijklmnopqrstuvwxyz/') 
DATABASE_MASTER_USERNAME=dbmaster
DATABASE_MASTER_PASSWORD=$(getRandomValue 16)

if [ -e .env ]; then
  echo "Removing old ENV file"
  rm .env
fi

echo "MASTER_PASSWORD=$DATABASE_MASTER_PASSWORD" >> .env
# @TODO - Consider removing the env file, and printing the DB password once???

docker compose up -d --wait

echo "Waiting for the MySQL server to finish initializing"
while ! docker exec -it passkey-mysql mysql -uroot -p$DATABASE_MASTER_PASSWORD -h 127.0.0.1 --protocol=TCP -e "status" &> /dev/null ; do
    echo "Waiting for database connection..."
    sleep 5
done 

docker exec -i passkey-mysql mysql -uroot -p$DATABASE_MASTER_PASSWORD -h 127.0.0.1 --protocol=TCP < db.sql 2>/dev/null | grep -v "mysql: [Warning] Using a password on the command line interface can be insecure."
