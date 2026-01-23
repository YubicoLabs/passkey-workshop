---
sidebar_position: 3
---

# Integration Guide

## API Overview

The gateway exposes three endpoints (all require JWT authentication):

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/countries` | GET | List supported shipping countries |
| `/api/addresses/validate` | POST | Validate a shipping address |
| `/api/shipments` | POST | Create a pre-registered YubiKey shipment |

## Authentication

Users authenticate via OIDC with Keycloak. The SPA receives a JWT token and includes it in all API requests.

### OIDC Configuration

```typescript
// src/config/auth.ts
export const authConfig = {
  authority: import.meta.env.VITE_OIDC_AUTHORITY,  // Keycloak realm URL
  client_id: import.meta.env.VITE_OIDC_CLIENT_ID,  // pre-reg-client
  redirect_uri: `${window.location.origin}/callback`,
  response_type: 'code',
  scope: 'openid profile email',
};
```

### Adding Token to Requests

```typescript
// Axios interceptor adds JWT to every request
this.client.interceptors.request.use(async (config) => {
  const token = await this.config.getIdToken();
  config.headers['Authorization'] = `Bearer ${token}`;
  return config;
});
```

## API Endpoints

### Get Countries

```http
GET /api/countries
Authorization: Bearer <token>
```

**Response:**
```json
{
  "countries": [
    { "country_code_2": "US", "country_name": "United States" },
    { "country_code_2": "CA", "country_name": "Canada" }
  ]
}
```

### Validate Address

```http
POST /api/addresses/validate
Authorization: Bearer <token>
Content-Type: application/json

{
  "street_line1": "123 Main St",
  "city": "San Francisco",
  "region": "CA",
  "postal_code": "94102",
  "country_code_2": "US"
}
```

**Response (valid):**
```json
{
  "status": "deliverable",
  "address": {
    "street_line1": "123 MAIN ST",
    "city": "SAN FRANCISCO",
    "region": "CA",
    "postal_code": "94102-1234",
    "country_code_2": "US"
  }
}
```

**Response (invalid):**
```json
{
  "status": "undeliverable",
  "details": ["Street address not found"]
}
```

### Create Shipment

```http
POST /api/shipments
Authorization: Bearer <token>
Content-Type: application/json

{
  "user_id": "user-123",
  "pin_request": { "type": "numeric", "length": 8 },
  "yubico_shipment_request": {
    "delivery_type": 1,
    "recipient": {
      "recipient_email": "user@example.com",
      "recipient_firstname": "Jane",
      "recipient_lastname": "Doe",
      "recipient_telephone": "555-1234"
    },
    "mailing_address": {
      "street_line1": "123 Main St",
      "city": "San Francisco",
      "region": "CA",
      "postal_code": "94102",
      "country_code_2": "US"
    },
    "shipment_items": [
      { "product_id": 1, "inventory_product_id": 1, "product_quantity": 1 }
    ]
  }
}
```

**Response:**
```json
{
  "shipment_id": "ship_abc123"
}
```

## Order Flow

1. **User logs in** → OIDC flow with Keycloak → SPA receives JWT
2. **Load countries** → `GET /api/countries` → populate dropdown
3. **Enter address** → `POST /api/addresses/validate` → show errors or confirmed address
4. **Submit order** → `POST /api/shipments` → backend creates shipment and registers credential
5. **Confirmation** → display order confirmation with estimated delivery

## Error Handling

All endpoints return standard error responses:

```json
{
  "error": "validation_error",
  "message": "Invalid postal code format",
  "details": ["postalCode: must match US format (12345 or 12345-6789)"]
}
```

| Status | Meaning |
|--------|---------|
| 400 | Validation error (check `details`) |
| 401 | Missing or invalid JWT |
| 403 | Insufficient permissions |
| 500 | Server error |

## Next Steps

- [Setup](./setup.md) — Run the reference implementation
