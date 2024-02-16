# About

The PKBank iOS app is a native Swift mobile client is a sample app allowing a user to perform the same
bank transactions as the web client using the PK Bank API. The bank API is token based and the client
authenticates via an ASWebAuthentication system browser to an authorization server (Keycloak) via an
OAUTH 2.0 Authorization Code Grant flow.

## How it Works

The user launches the app and is requested to register or log in. Both options trigger the built-in
WebSession browser after getting user consent (one-time). The registration and authentication of a PK
Bank user occurs on the Keycloak authorization server website rendered within the embedded browser.
After the user successfully authenticates, Keycloak returns an authorization code and user info. The
WebSession is dismissed and the app makes a background API requesting an access token from Keycloak
in exchange for the authorization code. The access token is required for the client to make API calls to the
PK Bank API on behalf of the user.

## Dependencies / Constants

The iOS project depends on the [SimpleKeychain](https://github.com/auth0/SimpleKeychain) Swift library (as an SPM) from auth0 for
integrating with the iOS Keychain for storing API credentials such as the token id, access and
refresh tokens.

Requires the backend devTunnel deployment: The client connects to the PK Bank authorization
and API server through the required devTunnel endpoints running in Docker. During the
deployment of the backend, the script programmatically updates the *Constants.xcconfig* file in the
Xcode project with the PK Bank authorization server and API endpoints.

## Not Support (TODO)
* Proof Key for Code Exchange (PKCE)
The iOS client does not currently implement the optional Proof Key for Code Exchange (PKCE)
security layer that ensures a public client that finishes the code exchange flow is the same one
that initiated it.

* Registering/Adding NEW Passkeys
The registration of new passkeys for an authenticated user is not currently supported in the iOS
client. It does support new user registration and low or high assurance passkey creation during
registration of the new user.

## Getting Started
1. Clone the repository
2. Change directory to: *~/examples/clients/mobile/iOS/PKBank*
3. Launch project in Xcode:
    > xed .
4. Build and Run 

