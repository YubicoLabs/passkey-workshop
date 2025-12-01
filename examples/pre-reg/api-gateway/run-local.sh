#!/bin/bash
# Load environment variables and run the API Gateway

set -e

if [ ! -f .env ]; then
    echo "Error: .env file not found"
    echo "Run: cp .env.template .env"
    exit 1
fi

set -a
source .env
set +a

mvn spring-boot:run -Dspring-boot.run.profiles=${ENVIRONMENT:-dev}
