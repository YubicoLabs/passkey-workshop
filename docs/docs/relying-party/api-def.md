---
sidebar_position: 1
---

# API definition

Before we discuss the code used to build the relying party, we should discuss the API that defines a client's ability to interact with the relying party application. By the end of this section you should understand best practices for the API that should translate to better comprehension on how the application should behave.

Note that these best practices are for general passkey applications. The API defined in this section may be changed in later sections, especially to accommodate high assurance use cases where the relying party may need to limit options from the client.

## Prerequisites

Ensure that you have deployed the relying application sample found in this project. Having the project deployed will help with comprehension by providing you with the APIs documentation, and will allow you to create sample requests.

[Follow the instructions on this page to deploy the application.](/docs/deploy)

## Accessing the API documentation

Once your project is deployed, the APIs documentation can be found at: [http://localhost:8080](http://localhost:8080)

## Overview of API methods

The section below will outline the responsibilities of the different APIs.

### API status

The `/status` method can be used to check the status of your application. This can help your client application determine if the service is running. Not essential to passkey applications, but overall a best practice

### Registration

The `/attestation` methods will be used to register a new passkey. Methods for the WebAuthn registration flow requires two calls.

The first call ([`/attestation/options`](http://localhost:8080/swagger-ui/index.html#/v1/serverPublicKeyCredentialCreationOptionsRequest)) is used to receive an object that includes the options/configurations that should be used when creating a new credential.

The second call ([`/attestation/result`](http://localhost:8080/swagger-ui/index.html#/v1/serverAuthenticatorAttestationResponse)) is used to send the newly created passkey to be stored in the relying party.

This topic will be covered in more detail in the following section on [Registration Flows](@TODO add new link).

### Authentication

The `/assertion` methods will be used to authenticate using the authenticator that generated your passkey. Methods for the WebAuthn authentication flow requires two calls.

The first call ([`/assertion/options`](http://localhost:8080/swagger-ui/index.html#/v1/serverPublicKeyCredentialGetOptionsRequest)) is used to receive an object that includes the options/configurations that should be used when trying to validate a challenge for authentication.

The second call ([ `/assertion/result`](http://localhost:8080/swagger-ui/index.html#/v1/serverAuthenticatorAssertionResponse)) is used to send the signed challenge to verify if a valid credential was used.

This topic will be covered in more detail in the following section on [Authentication Flows](@TODO add new link).

### Credential management

The `/user/credentials` methods will help you with credential management. In passkey applications, credential management refers to the process of managing a users credential by allowing them to delete or rename a credential.

In the current paradigm, password management would allow a user to rotate, or change their password as a form of best practice. This rotation is not needed for passkeys - in fact, the user should never be allowed to change the credential that was passed to the relying party,

The user should only have two options:

- Update attributes that are used to help them identity a passkey (like a nickname)
- Delete the passkey from use (good in the case of a lost or stolen authenticator)

Both of these options are provided in our example. They can be tested once a credential has been registered to the relying party.
