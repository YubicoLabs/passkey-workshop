#!/bin/bash

# Pre-Registration Reference Architecture - Deployment Script
# Usage: ./deploy.sh

set -e

echo ""
echo "### Pre-Registration Reference Architecture"
echo ""

# Check Docker Compose v2
echo "### Checking Docker Compose version"
docker compose version | grep -q "v2" || { echo "Error: Docker Compose v2 required"; exit 1; }

# Create .env if missing
if [[ ! -f ".env" ]]; then
    echo "### Creating .env from default.env"
    cp default.env .env
    echo ""
    echo "⚠️  Edit .env and add your DOWNSTREAM_API_TOKEN"
    echo "   Get token from: https://console.dev.yubico.com"
    echo "   Then run: ./deploy.sh"
    echo ""
    exit 1
fi

# Load and validate environment
source .env
if [[ -z "$DOWNSTREAM_API_TOKEN" ]]; then
    echo "Error: DOWNSTREAM_API_TOKEN not set in .env"
    echo "Get token from: https://console.dev.yubico.com"
    exit 1
fi

# Stop existing containers
echo "### Stopping existing containers"
docker compose down 2>/dev/null || true

# Build and start
echo "### Building and starting containers"
docker compose up -d --build

# Wait for Keycloak to be ready
echo "### Waiting for services to be ready"
until docker compose exec -T keycloak /opt/keycloak/bin/kcadm.sh config credentials \
    --server http://localhost:8080 --realm master --user admin --password admin 2>/dev/null; do
    sleep 2
done

# Output URLs
CLIENT_URL=${CLIENT_URL:-http://localhost}
KEYCLOAK_URL=${KEYCLOAK_URL:-http://localhost:8082}
GATEWAY_URL=${GATEWAY_URL:-http://localhost:8086}

echo ""
echo "### Deployment complete!"
echo ""
echo "Application:  ${CLIENT_URL}"
echo "Keycloak:     ${KEYCLOAK_URL} (admin/admin)"
echo "API Gateway:  ${GATEWAY_URL}"
echo "Test user:    testuser / testuser"
echo ""
echo "### Showing logs (Ctrl+C to exit, containers keep running)"
docker compose logs -f

