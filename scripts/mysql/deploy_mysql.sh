#!/bin/bash

# Generate a random value, primarily used for generating a new password
function getRandomValue {
    echo $(ping -c 1 yubico.com |md5 | head -c$1; echo)
}

# Generating databse params
echo "Composing MYSQL database"

# Check if there is an ENV file
# If no ENV file, generate an env file
if [ ! -e .env ]; then
  echo "Creating new Docker container"
  echo "Generating database password"

  # Use password provided by userer, or generate a random one
  DATABASE_MASTER_PASSWORD=${1:-$(getRandomValue 16)}

  echo "Database password created"

  echo "Writing password to .env file"

  echo "MASTER_PASSWORD=$DATABASE_MASTER_PASSWORD" >> .env
fi

docker compose up -d