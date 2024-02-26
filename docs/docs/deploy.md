---
sidebar_position: 4
---

# Deploy project

In this section we are going to deploy the project to your local environment. At the end of this section you should be able to access all of the components that will allow you to test, and further understand the workings of a passkey application.

## Prerequisites

The following prerequisite items are required in order to run, and test this application in your local environment.

- [Docker](https://www.docker.com/)
- [Git](https://git-scm.com/)
- FIDO2 authenticator
  - A security key, like a Yubikey
  - A device with a platform authenticator such as TouchID/FaceID, or Windows Hello
  - A browser that supports virtual authenticators, such as [Google Chrome](https://developer.chrome.com/docs/devtools/webauthn/)
- A platform (browser and operating system) that supports WebAuthn/passkeys ([browser support matrix](https://passkeys.dev/device-support/))

## Clone the repository

Your first step will be to clone the repository from GitHub, using git.

With HTTPS

```bash
git clone https://github.com/YubicoLabs/passkey-workshop.git
```

Once the repository is cloned, navigate into the `deploy` folder.

```bash
cd passkey-workshop/deploy
```

## Deployment configurations

The file `.env` will allow you to set specific parameters for use in the application.
You will need to create that file first, but there is an example file `default.env` with defaults for a deployment to localhost.

::::danger Ensure you understand the configurations before changing them
If this is your first time deploying this application, or you are unfamiliar with passkeys, then we recommend that you utilize the default settings.
::::

Below is an overview of the configurations file, along with the default parameters.
The file `default.env` contains comments with alternative settings for some configuration parameters.


```bash
DEPLOYMENT_ENVIRONMENT=localhost

# Client applications to deploy
DEPLOYMENT_CLIENTS=demo

# Domain that identifies the relying party
# Reference: https://www.w3.org/TR/webauthn-2/#relying-party-identifier
RP_ID=localhost

# Human readable name that relates to the name of the site listed from the RP_ID
RP_NAME=Workshop

# Set the webauthn applications attestation preference
# Reference: https://www.w3.org/TR/webauthn-2/#enumdef-attestationconveyancepreference
RP_ATTESTATION_PREFERENCE=DIRECT

# Allow registrations that don't include some form of trusted attestation
RP_ALLOW_UNTRUSTED_ATTESTATION=true

# Leverage attestation through the FIDO Metadata Service (MDS)
RP_ATTESTATION_TRUST_STORE=mds

# Allowed origins for java-app
RP_ALLOWED_ORIGINS=localhost:8081,localhost:3000

# Spring-Boot framework config for allowed cross-origins domains (CORS)
# shared between java-app and bank-app
RP_ALLOWED_CROSS_ORIGINS=localhost:8081,localhost:3000

# Allowed authenticators, referenced by AAGUID. Leave empty to allow any authenticator
ALLOW_LIST_AAGUIDS=

# database shared with java-app and bank-api
DATABASE_TYPE=mysql
# root password when using mysql
DATABASE_PASSWORD=somegobbledygookhere

# Location of the demo client
DEMO_CLIENT_URL=http://localhost:3000

# Location of the the RP API keycloak can connect to for passkey registration/validation
RP_API=http://host.docker.internal:8080/v1

# react-app demo client configuration
REACT_APP_RP_API=http://localhost:8080/v1
REACT_APP_OIDC=http://localhost:8081/realms/passkeyDemo/protocol/openid-connect
REACT_APP_REDIRECT_URI=http://localhost:3000/oidc/callback
```

## Deploying the project

The default configuration will deploy the demo application on localhost. 
This means the application can be accessed using a web browser running on the same system.
Later, we will also look at a client running on your mobile device.

To deploy the application, run the following command:

**For Mac-Linux**

```bash
./deploy.sh
```

**For Windows**

```powershell
\deploy.ps1
```

These scripts use docker compose to build all components and run them in containers. 

As noted in the previous section, all "arguments" come in the form of the configurations set in the `.env` file.

Note: these scripts are used for both deploying a fresh version of the application, and for restarting the service if it has been stopped.

Once the application has been deployed, your resources will be available through the following URLs

- Java-app (relying party) - [http://localhost:8080](http://localhost:8080)
  - Navigating to this resource will bring you to the API documentation
- Keycloak (identity and authorization provider) - [http://localhost:8081](http://localhost:8081)
  - Navigating to this resource will bring you to the Keycloak administrator screen
  - The default credentials for the admin account is:
    - Username: admin
    - Password: admin
- MySQL server (database) - [http://localhost:3306](http://localhost:3306)
  - You won't be able to directly navigate to this resource. Please use a MySQL client to connect to the database
  - The default credentials for the root account is:
    - Username: root
    - Password: somegobbledygookhere (see the value of `DATABASE_PASSWORD` in the `.env` file)
- React-app (web client) - [http://localhost:3000](http://localhost:3000)
  - Navigating to this resource will bring you to the web client application

### Deploying a native iOS example

Follow the specific instructions found in the [mobile-client: getting started](/docs/mobile-client/getting-started) section.

## Stopping the project

To stop the application containers, run the following command:

**For Mac-Linux** or **Windows**

```bash
docker compose stop
```

This will look for any instance of the containers set by our deploy script, and stop them from running.

To start all containers again, use:

```bash
docker compose start
```

## Removing the project

To remove the application containers, run the following command:

**For Mac-Linux** or **Windows**

```bash
docker compose down
```

This will look for any instance of the containers and images set by our deploy script, and remove them from Docker instance.

Once the application has been removed, you will need to run the deploy application script, where you will be given a fresh, "factory-default", version of the application. 
Any passkeys registered to the old deployment will be unusable.
