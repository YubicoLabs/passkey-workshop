# Pre-Registration Reference Architecture

Reference implementation for ordering pre-registered YubiKeys through the Yubico Enterprise Delivery API.

## Prerequisites

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) (includes Docker Compose v2)
- Yubico Enterprise Delivery API token from [console.dev.yubico.com](https://console.dev.yubico.com)

## Quick Start

```bash
cd examples/pre-reg
cp default.env .env
# Edit .env and set DOWNSTREAM_API_TOKEN
./deploy.sh
```

Open http://localhost and login with `testuser` / `testuser`

## Architecture

```
┌─────────────┐     ┌─────────────┐     ┌─────────────────┐
│   Client    │────▶│   Gateway   │────▶│  Yubico YED API │
│  (React)    │     │  (Spring)   │     │                 │
└─────────────┘     └──────┬──────┘     └─────────────────┘
                           │
                           ▼
                    ┌─────────────┐
                    │  Keycloak   │
                    │   (OIDC)    │
                    └─────────────┘
```

| Service | Port | Description |
|---------|------|-------------|
| Client | 80 | React SPA for the order flow |
| Gateway | 8086 | Spring Cloud Gateway with JWT validation |
| Keycloak | 8082 | Identity provider (auto-configured) |

## Configuration

All configuration is in `.env`. Copy from `default.env` to get started:

| Variable | Required | Default |
|----------|----------|---------|
| `DOWNSTREAM_API_TOKEN` | Yes | - |
| `CLIENT_PORT` | No | 80 |
| `GATEWAY_PORT` | No | 8086 |
| `KEYCLOAK_PORT` | No | 8082 |

## Commands

```bash
# Deploy
./deploy.sh

# View logs
docker compose logs -f

# Stop
docker compose down

# Reset (removes all data)
docker compose down -v
```

## Troubleshooting

**Port conflicts**: Edit `.env` to change ports:
```env
CLIENT_PORT=3000
GATEWAY_PORT=8087
KEYCLOAK_PORT=8083
```

**View specific service logs**:
```bash
docker compose logs -f gateway
docker compose logs -f keycloak
docker compose logs -f client
```

## Component Documentation

- [API Gateway](./api-gateway/README.md)
- [Client](./client/README.md)

## Security Notes

⚠️ **Development/testing only.** For production:
- Change Keycloak credentials
- Use HTTPS
- Store secrets in a vault
- Configure proper CORS origins
