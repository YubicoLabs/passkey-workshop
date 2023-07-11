# OAuth2 demo

This is a minimal implementation of an API with the following features:

- protected with OAuth2
- API is implemented using Spring Boot
- API code is generated from an OpenAPI spec
- KeyCloak is used as the Authorisation Server

# Running the demo

The demo has three components:

1. Authorisation Server (Keycloak)
2. Resource server (a Spring Boot API)
3. Client (a simple shell script)

To run the demo, start each component in the following order.

## Authorisation Server

    cd authorizationserver
    ./start.sh

This script will start a recent docker image for Keycloak and imports a realm named `demo` that contains a registration for our client
The client is named `message-client` and has two scopes defined: `message:read` and `message:write`.

## Resource server

    cd resourceserver
    ./start.sh

The resource server is a Spring Boot application whose code is generated using [openapi-generator](https://openapi-generator.tech).
The OpenAPI spec is in the file [api.yaml](resource-server/api.yaml) and describes a `message` endpoint for a GET and a POST message.
The GET request requires scope 	`message:read` and the POST request requires scope `message:write`.
Note that the methods do not return any content. We are only interested in the HTTP response status.

## Client

The client is a simple shell script.
Before running the script, make sure to run the setup.sh script that will retrieve the client secret and create a user with username `john`.
The setup.sh script only needs to be run once after every restart of the authorisation server.

Then run the start.sh script to test the API.
You will need to sign in with username `john` and password `doe`.
The script will obtain an access token with scope `message:read` and then call the GET and POST endpoints.
The former will result in a HTTP response status 200,
The latter will result in a HTTP response status 403, as the `message:write` scope is not provided.


    cd client/cli
    ./setup.sh
    ./start.sh

