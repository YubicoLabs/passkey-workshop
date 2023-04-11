# Getting started

These are items to help to configure keycloak to reg/auth using a custom Authenticator SPI to handle Passkey transactions with our custom WebAuthn server

It is highly recommend that you deploy keycloak through our deploy script, by following the instructions on our [deployment page](https://yubicolabs.github.io/passkey-workshop/docs/deploy).

Once the application is deployed, you may access the web client at [localhost:8081](http://localhost:8081)

If you wish to deploy the items manually, the steps are below.

1. Deploying a Docker container running Keycloak
2. Building this Java project, and dropping it into the Keycloak providers folder
3. Restarting the Keycloak server to add the custom SPI
4. Going into Keycloak to make the necessary configurations

## Start the project

### Deploy instance of Keycloak

Run a default instance of KeyCloak using Docker:

    docker run \
        -p 8081:8080 \
        -e KEYCLOAK_ADMIN=admin \
        -e KEYCLOAK_ADMIN_PASSWORD=admin \
        quay.io/keycloak/keycloak:21.0.0 start-dev

### Add Java project to providers

Build the project, then copy the JAR file into KeyCloak

    mvn clean package
    docker cp target/passkey_authenticator-1.0.0.jar {name_of_your_container}:/opt/keycloak/providers

Once this is done, restart the docker container

### Keycloak configs

```
Please note that this is my very hacky way of setting this configs - I need to clean up this implementation with some better standards, and best practices. But these settings will ensure this works for now
```

Create a new realm - The current default in the React app is named "normal"

Create a new client - The current default in the React app is named "my_custom_client2"

- Ensure that Standard flow and Direct access grants are enabled
- Set these three URIs under the "Valid redirect URIs": http://localhost:3000/logout, http://localhost:3000/oidc/callback, /\*
- Set these two options for "Web origins": _, /_

Click on the "Authentication" tab

- Click on the 3 dots to the right of browser - Click duplicate
- Enter the item, "Copy of browser"
- Delete all the items
- Click add an execution
- Select "Passkey Authenticator"
- Click "Action" on the top-right
- Click Bind Flow
- Choose "Browser"

Return to the "Authentication" tab

- Click on the 3 dots to the right of registration - Click duplicate
- Enter the item, "Copy of registration"
- Delete all the items
- Click add an execution
- Select "Passkey Registration Authenticator"
- Click "Action" on the top-right
- Click Bind Flow
- Choose "Registration"

Enter the "Realm Settings" tab

- Click on the "Login" header
- Make sure the "User registration" switch is on
