# Project deployment

This folder will allow you to deploy the sample passkey application from your environment to Docker. The following sections will outline options that you can set for your deployment, along with an overview on how to use the different scripts to manage your deployment.

## Deployment configurations

The file `DeployProject_Settings.json` will allow you to set specific parameters for use in the application (if no values are provided, our deploy script will apply default values based on our best practices).

Below is a preview of this config file, along with the different enumeration values, and other considerations.

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

## Scripts

Below is an overview of the various scripts used to manage the application.

### Deploy

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

**Note:** this script is used for both deploying a fresh version of the application, and for restarting the service if it has been stopped.

### Stop

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

If the application has been stopped, run the deployment script to restart your application.

### Remove

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

### Managing individual components

Each component folder (java-app, react-app, mysql, keycloak) contains a corresponding deploy, stop, and remove script. If you need to manage a component individually, navigate into the respective folder and run the applicable script.

**Note:** that the component script relies on arguments that are generated by the top level deploy script, so be prepared to pass the arguments manually.
