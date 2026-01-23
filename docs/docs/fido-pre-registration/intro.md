---
sidebar_position: 1
---

# FIDO Pre-Registration

## What is FIDO Pre-Registration?

FIDO Pre-Registration (FPR) allows organizations to ship YubiKeys with passkey credentials **already registered** to user accounts. When users receive their YubiKey, it works immediately—no setup required.

## How It Works

1. **Your app** requests a YubiKey shipment via Yubico API
2. **Yubico** generates a credential on the YubiKey and returns credential data
3. **Your app** registers the credential to the user's account in Keycloak
4. **Yubico** ships the YubiKey to the user
5. **User** receives the key and authenticates immediately

**Example**: A bank offers "Advanced Protection" to high-value customers. When a customer enrolls, a pre-registered YubiKey ships to them. On arrival, they can immediately use it to log in—no registration flow needed.

## When to Use This Pattern

| Use Case | Benefit |
|----------|---------|
| Enterprise security programs | Deploy hardware keys without IT involvement |
| High-value accounts | Banks, crypto exchanges offering premium security |
| Regulated industries | Meet compliance for strong authentication |
| Account recovery | Ship replacement keys pre-registered |
| Remote workforce | Onboard employees without physical key exchange |

## Key Benefits

- **Zero-touch enrollment** — no technical knowledge required
- **Reduced support burden** — no "how do I register?" tickets
- **Immediate protection** — account secured on delivery
- **Scalable** — ship thousands without manual intervention

## Prerequisites

- YubiEnterprise Delivery API access ([console.yubico.com](https://console.yubico.com))
- Keycloak (or similar IdP) with WebAuthn support
- Backend service to orchestrate the flow

## Next Steps

- [Architecture](./architecture.md) — Components and how they interact
- [Integration Guide](./integration.md) — API endpoints and code examples
- [Setup](./setup.md) — Run the reference implementation
