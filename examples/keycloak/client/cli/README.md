# command line client

Simple OAuth demo for securing an API with OAuth 2.0

## Dependencies

The command line scripts depend on 

- `jq`, for parsing JSON files
- `step`, for validating OAuth access tokens
- `netcat`, for launching a port listener to obtain a front-channel authorization code

For instance, on macos:

    brew install jq step

Note that netcat comes with macos and is typically installed with your linux distribution.

## Demo

Use the Makefile to run a demo:

    make

This will run the CLI scripts that implement a simple OAuth client.
The scripts will obtain an access token (`token.sh`), and call a simple API using that access token (`client.sh`).

A short description of these `.sh` files:

### common

This file contains configuration parameters for the OAuth client, Resource Server, and Authorization Server

### token

Obtain an access (and refesh) token.

This script uses the Authorization Code flow with PKCE to obtain an authorization code (front channel),
which is exchanged for an access token (back channel), which is stored in the file `token.json`.

### client

Call the API at the Resource Server, using the access token in `token.json`.

### validate

Validate an access token.

This is mostly for debugging, as access tokens are opaque to OAuth clients.

Ths script will first try local validation (for JWT access tokens), and subsequently try remote validation.
Remote validation is only possible with a client secret.

When an access token is valid, the script outputs its payload for JWT access tokens.

Otherwise, an error message is deplayed.

For instance, when an access token is expired:

	$ ./validate.sh 
	=== Validate access token locally:
	validation failed: token is expired by 17.662s (exp)

### refresh
