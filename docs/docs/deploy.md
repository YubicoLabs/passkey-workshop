---
sidebar_position: 4
---

# Deploy project

In this section we are going to deploy the project to your local environment. At the end of this section you should be able to access all of the components that will allow you to test, and further understand the workings of a passkey application.

## Prerequisites

The following prerequisites items are required in order to run, and test this application in your local environment.

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

Once the repository is cloned, navigate into the scripts folder.

```bash
cd passkey-workshop/scripts
```

## Deployment configurations

Regardless of environment, both folders contain a file that will allow you to configure certain aspects of your passkey application.

The file `DeployProject.conf` will allow you to set specific parameters for use in the application (if no values are provided, our deploy script will apply default values based on our best practices).

::::danger Ensure you understand the configurations before changing them
If this is your first time deploying this application, or you are unfamiliar with passkeys, then we recommend that you utilize the default settings.
::::

Below is an overview of the configurations file, along with the acceptable parameters

```bash
# Denotes the valid domain that identifies the relying party
# Reference: https://www.w3.org/TR/webauthn-2/#relying-party-identifier
# Note - If you do not have a dedicated custom domain, then it is best
# to use the default localhost value.
# Default: localhost
# Options: free text
RP_ID=

# Human readable name that relates to the name of the site listed from the RP_ID
# Default: "My app"
# Options: free text
RP_NAME=

# Requirement from the java-webauthn-server library. Denotes the origins that can generate a credential that
# will be accepted by the relying party.
# This should closely correspond to the domain denoted in the RP_ID. Note that unlike the RP_ID, this
# value will require the port number
# DO NOT include the protocol header (http/https), the relying party automatically allows for both
# Multiple domains can be listed through comma delimitation
# Default: localhost:3000
# Options: free text
# Example (single domain): localhost:3000
# Example (multiple domains): localhost:3000,localhost:3002,my.domain.com
RP_ALLOWED_ORIGINS=

# Requirement from the Spring-Boot framework, this denotes the allowed cross-origins domains that are allowed to
# interact with the API.
# For the most part, this field should correspond to what is listed in the RP_ALLOWED_ORIGINS property
# Default: localhost:3000
# Options: free text
# Example (single domain): localhost:3000
# Example (multiple domains): localhost:3000,localhost:3002,my.domain.com
RP_ALLOWED_CROSS_ORIGINS=

# Sets the webauthn applications attestation preference
# Reference: https://www.w3.org/TR/webauthn-2/#enumdef-attestationconveyancepreference
# Default: DIRECT
# Options: DIRECT, INDIRECT, ENTERPRISE, NONE
RP_ATTESTATION_PREFERENCE=

# Allows you to configure your application to reject registrations that don't include
# some form of trusted attestation
# Default: false
# Options: true, false
RP_ALLOW_UNTRUSTED_ATTESTATION=

# Allows you to deploy the application to different environments
# At this moment, the only option that is configured is for a local deployment, but this example will be
# extended to allow new environments, such as cloud deployments
# some form of trusted attestation
# Default: local
# Options: local
DEPLOYMENT_ENVIRONMENT=

# Allows you to select the data source used by the application
# The default option is to deploy a full MySQL server in Docker
# in-mem will not deploy a MySQL instance, and instead rely on in-memory storage in the Java application.
# Note that the in-mem option will not persist data if the Java container is stopped
# Default: mysql
# Options: mysql, in-mem
DATABASE_TYPE=

# Allows you to set your desired password for the root account in the MySQL server.
# In the case that this field is left empty, the deploy script will generate a random one for you
# Default: randomly generated string
# Options: free text
DATABASE_ROOT_PASSWORD=

# Allows you to set the client application that you wish to deploy
# At this moment, the only option that is configured is a react based deployment.
# You may set this option as none, if you are only leveraging the relying party aspect of this example
# Default: react
# Options: react, none
CLIENT_TYPE=

# Allows you to set the identity provider and authorization server for the application
# At this moment, the only option that is configured is a keycloak based deployment.
# You may set this option as none, if you are leveraging another identity provider
# Default: keycloak
# Options: keycloak, none
IDP_TYPE=
```

## Deploying the project

To deploy the application, run the following command:

**For Mac-Linux**

```bash
./DeployProject.sh
```

**For Windows**

```powershell
\DeployProject.ps1
```

As noted in the previous section, all "arguments" come in the form of the configurations set in the `DeployProject_Settings.json` file.

Note: this script is used for both deploying a fresh version of the application, and for restarting the service if it has been stopped.

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
    - Password: Randomly generated (unless specified), can be found in the `.env` file that is generated in the current directory's mysql folder
- React-app (web client) - [http://localhost:3000](http://localhost:3000)
  - Navigating to this resource will bring you to the web client application

### Deploying a native iOS example

@TODO - Dennis to add this section

## Stopping the project

To stop the application containers, run the following command:

**For Mac-Linux**

```bash
./StopProject.sh
```

**For Windows**

```powershell
\StopProject.ps1
```

This will look for any instance of the containers set by our deploy script, and stop them from running.

**If the application has been stopped, run the deployment script to restart your application.**

## Removing the project

To remove the application containers, run the following command:

**For Mac-Linux**

```bash
./RemoveProject.sh
```

**For Windows**

```powershell
\RemoveProject.ps1
```

This will look for any instance of the containers and images set by our deploy script, and remove them from Docker instance.

Once the application has been removed, you will need to run the deploy application script, where you will be given a fresh, "factory-default", version of the application. Any passkeys registered to the old deployment will be unusable.
