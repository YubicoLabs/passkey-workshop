---
sidebar_position: 2
---

# Architecture

## Overview

The reference implementation uses a layered architecture with an API Gateway pattern:

| Layer | Component | Purpose |
|-------|-----------|---------|
| Frontend | React SPA | User interface for ordering YubiKeys |
| Gateway | Spring Cloud Gateway | Security perimeter, JWT validation, routing |
| Backend | FIDO Connector | Business logic, Yubico API integration |
| Identity | Keycloak | OAuth 2.0/OIDC, user management, credential storage |
| External | Yubico API | Address validation, shipment creation, fulfillment |

## Why API Gateway?

The gateway is the single entry point for all API traffic:

| Concern | How Gateway Handles It |
|---------|------------------------|
| Authentication | Validates JWT tokens from Keycloak |
| Authorization | Ensures only authenticated users access protected endpoints |
| Credential Security | API tokens stay server-side, never reach the browser |
| CORS | Manages cross-origin requests from the SPA |

**Without a gateway**, API credentials would be exposed in the browser or backend access would be inconsistent.

## Components

### React SPA (Client)

User-facing application for:
- Authentication via Keycloak (OIDC)
- YubiKey ordering flow
- Address entry and validation
- Order confirmation

**Tech**: React 18, TypeScript, MUI, React Query, `oidc-client-ts`

### API Gateway

Security perimeter protecting backend services:
- JWT token validation via Keycloak JWKS
- Request routing to backend services
- Rate limiting
- CORS handling

**Tech**: Spring Cloud Gateway, Java 17

### FIDO Connector

Business logic layer coordinating the pre-registration flow:
- Address validation via Yubico API
- Shipment creation
- Credential registration to IdP
- PIN generation

**Tech**: Spring Boot, Java 17

### Keycloak

Identity provider with WebAuthn support:
- User authentication
- Token issuance (ID/access tokens)
- WebAuthn credential storage
- Custom SPI for programmatic credential registration

## Request Flow

1. User authenticates with Keycloak, SPA receives JWT
2. User submits order, SPA sends request with JWT to Gateway
3. Gateway validates JWT against Keycloak's JWKS
4. Gateway forwards request to FIDO Connector
5. Connector validates address and creates shipment via Yubico API
6. Connector registers returned credential to user in Keycloak
7. Response flows back through Gateway to SPA
8. Yubico ships YubiKey to user
9. User receives key and authenticates immediately

## Key Properties

- **Zero Trust**: All backend calls require validated JWTs
- **Pre-Registration**: Credentials bound to accounts before device delivery
- **Separation of Concerns**: Each layer has a single responsibility
- **Extensible**: Keycloak SPI pattern can adapt to other IdPs

## Next Steps

- [Integration Guide](./integration.md) — API details and code examples
- [Setup](./setup.md) — Run the reference implementation
