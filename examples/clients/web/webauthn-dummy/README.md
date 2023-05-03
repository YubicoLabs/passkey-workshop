# WebAuthn demo

Minimal demo of the WebAuthn API, illustrating Registration and Authentication using passkeys.
Note that this is intended to explain how a Relying Party can use the WebAuthn API to interact with Authenticators.

The server-part of the Relying Party is absent, so credentials IDs, public Keys, attestations, etc are not stored,
and assertions are not verified.

To run on localhost:

1. from this directory, run a local web server:

      python3 -m http.server

2. point your browser to http://localhost:8000
3. open the JavaScript console to view the responses from your authenticator
4. register a passkey (create)
5. authenticate using your passkey (get)

