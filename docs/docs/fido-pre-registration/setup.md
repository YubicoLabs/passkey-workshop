---
sidebar_position: 4
---

# Setup & Deployment

## Prerequisites

- **Docker Desktop** (includes Docker Compose) — [Download](https://www.docker.com/products/docker-desktop/)
- **Yubico Enterprise Delivery API token** — [console.yubico.com](https://console.yubico.com)
- **Git**

## Quick Start (Docker)

```bash
# Clone repository
git clone https://github.com/YubicoLabs/passkey-workshop.git
cd passkey-workshop

# Deploy
cd deploy
cp default.env .env
# Edit .env and set DOWNSTREAM_API_TOKEN=your_token_here
./deploy.sh pre-reg
```

**Access:**

| Service | URL | Credentials |
|---------|-----|-------------|
| Application | http://localhost | testuser / testuser |
| Keycloak Admin | http://localhost:8082 | admin / admin |
| API Gateway | http://localhost:8086 | — |

## Services

Docker Compose starts three services:

| Service | Port | Description |
|---------|------|-------------|
| Client (nginx) | 80 | React SPA |
| Gateway (Spring) | 8086 | API Gateway |
| Keycloak | 8082 | Identity Provider |

Keycloak is pre-configured with:
- Realm: `pre-reg`
- Client: `pre-reg-client`
- Test user: `testuser` / `testuser`

## Configuration

Edit `.env` to configure:

| Variable | Required | Default | Description |
|----------|----------|---------|-------------|
| `DOWNSTREAM_API_TOKEN` | Yes | — | Yubico API token |
| `CLIENT_PORT` | No | 80 | React app port |
| `GATEWAY_PORT` | No | 8086 | API Gateway port |
| `KEYCLOAK_PORT` | No | 8082 | Keycloak port |

## Manual Setup (Development)

For local development without Docker:

### 1. Keycloak

```bash
docker run -p 8082:8080 \
  -e KEYCLOAK_ADMIN=admin \
  -e KEYCLOAK_ADMIN_PASSWORD=admin \
  quay.io/keycloak/keycloak:26.2.0 start-dev
```

Import the realm from `deploy/keycloak/realm-export.json` or manually create:
- Realm: `pre-reg`
- Client: `pre-reg-client` (public, PKCE)

### 2. API Gateway

```bash
cd examples/pre-reg/api-gateway/source
cp .env.template .env
# Edit .env with your settings
./mvnw spring-boot:run
```

### 3. React Client

```bash
cd examples/pre-reg/client
npm install
npm run dev
```

## Troubleshooting

### Port Conflicts

Change ports in `.env`:
```env
CLIENT_PORT=3000
GATEWAY_PORT=8087
KEYCLOAK_PORT=8083
```

### View Logs

```bash
docker compose logs -f          # All services
docker compose logs -f gateway  # Specific service
```

### Reset Everything

```bash
docker compose down -v
./deploy.sh pre-reg
```

## Testing

### Run Unit Tests

```bash
# Frontend
cd examples/pre-reg/client
npm test

# Gateway
cd examples/pre-reg/api-gateway/source
./mvnw test
```

## OpenAPI Specification

The gateway's OpenAPI spec is available at:
- Runtime: `http://localhost:8086/v3/api-docs`
- File: `/api/Yubico-OpenAPI3-Passkey-PreReg-Schema-v1.yaml`
