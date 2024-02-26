---
sidebar_position: 4
---

# Deploy example

In this section, we will deploy the Banking application.
It uses the same script that was used to [deploy the demo application](/docs/deploy), but here we need to configure the application a little differently.

## Prerequisites

As before, we need [Docker](https://www.docker.com/) to deploy the environment locally.
But to use the banking application with high assurance, a FIDO2 security key like a YubiKey is required.
We assume you already cloned the repository.

## Deployment configurations

The file `.env` will need to be edited for the specific parameters for use in the banking application.

Here is an example configuration with settings relevant to the banking API (other settings can be left intact)

```bash
# Client applications to deploy
DEPLOYMENT_CLIENTS=bank

# Domain that identifies the relying party
# Reference: https://www.w3.org/TR/webauthn-2/#relying-party-identifier
RP_ID=localhost

# Human readable name that relates to the name of the site listed from the RP_ID
RP_NAME=PKBank

...

# Allow registrations that don't include some form of trusted attestation
RP_ALLOW_UNTRUSTED_ATTESTATION=true

# Leverage attestation through the FIDO Metadata Service (MDS)
RP_ATTESTATION_TRUST_STORE=mds

# Allowed origins for java-app
# Denotes the origins that can generate a credential that will be accepted by the relying party.
# This should closely correspond to the domain denoted in the RP_ID. 
# Note that unlike the RP_ID, this value will require the port number.
RP_ALLOWED_ORIGINS=localhost:8081,localhost:3002

# Spring-Boot framework config for allowed cross-origins domains (CORS)
# shared between java-app and bank-app
RP_ALLOWED_CROSS_ORIGINS=localhost:8081,localhost:3002

# Allowed authenticators, referenced by AAGUID. Leave empty to allow any authenticator
ALLOW_LIST_AAGUIDS=73bb0cd4-e502-49b8-9c6f-b59445bf720b,85203421-48f9-4355-9bc8-8a53846e5083,c1f9a0bc-1dd2-404a-b27f-8e29047a43fd

...

# Location of the Bank client
BANK_CLIENT_URL=http://localhost:3002
# Location of the the RP API keycloak can connect to for passkey registration/validation
# Note that this is a backend call so connect to the docker host when deploying on localhost
RP_API=http://host.docker.internal:8080/v1


# bank-react-app HA client configuration
REACT_APP_BANK_API=http://localhost:8082/v1
REACT_APP_KEYCLOAK_URL=http://localhost:8081
REACT_APP_CLIENT_ID=BankApp
```

Note the differences with the demo application. In particular:

- The banking web client is deployed on localhost:3002
- The banking API is deployed on localhost:8082
- Origin settings have been updated accordingly
- The setting for `ALLOW_LIST_AAGUIDS` has now been set. The AAGUIDS are for the YubiKey 5 Series, but you can change that to match your specific make and model or leave it blank to accept any authenticator.

## Deploying the project

As before, use the deploy scripts (`deploy.sh` or `deploy.ps1`) to deploy the application.

To stop, start, or remove the project, use docker compose:

```bash
docker compose stop
docker compose start
docker compose down
```

## Running the mobile client

The workshop repository also includes an iOS native app.
Because this app is run on a separate device, you will need to deploy the server components using devtunnel in order for your iOS device to be able to communicate with those components.

Deploying with devtunnel works the same as before. Edit your `.env` file and include the following variable:

	DEPLOYMENT_ENVIRONMENT=devtunnel

Then run the deploy scripts (`deploy.sh` or `deploy.ps1`) again.

The deploy script will launch your devtunnel and edit the iOS sources to point to your tunneled endpoints.

With that, you should be able to compile and run the mobile client as follows:

1. Change directory to `examples/clients/mobile/iOS/PKBank`.
2. Launch project `PKBank.xcodeproj` in Xcode.
3. Build and Run the project on your iOS device.

