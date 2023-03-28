---
sidebar_position: 4
---

# Authentication flows

In this section we are going to go over the authentication flows for logging in with a passkey.

By the end of this section you will understand how to use and implement both of the `/assertion/options` and `/assertion/result` methods (as defined by our [API](relying-party/api-def))

## Flow overview

The diagram below demonstrates how the relying party works with the client, and authenticator to authenticate with a passkey. When interacting with the relying party, the client will leverage both of the `/assertion` methods.

![Docusaurus logo](/img/auth-flow.jpg)

The first call ([`/attestation/options`](http://localhost:8080/swagger-ui/index.html#/v1/serverPublicKeyCredentialCreationOptionsRequest)) is used to receive an object that includes the options/configurations that should be used when creating a new credential.

The second call ([`/attestation/result`](http://localhost:8080/swagger-ui/index.html#/v1/serverAuthenticatorAttestationResponse)) is used to send the newly created passkey to be stored in the relying party.

## Assertion options method

In this section we are going to outline, in detail, the assertion options method as well as provide a sample implementation in Java using the [java-webauthn-server library](https://github.com/Yubico/java-webauthn-server).

### API request and response schema

#### Request

Below is the request body of the `/assertion/options` method

```json
{
  "userName": "user@acme.com"
}
```

In this case, we are only signaling to the relying party the user which we are trying to authenticate. This method is implemented in a way that will support **BOTH** discoverable and non-discoverable credential flows. For the sake of this workshop, our focus will be on the discoverable credential flow, but we will outline how the non-discoverable credential flow is handled.

##### Discoverable credential flow

As you may recall from the fundamentals section, a WebAuthn credential must be discoverable in order to be a passkey. A discoverable credential refers to the ability for a relying party to attempt to utilize a credential on an authenticator without the user providing a user handle. This means that in our API we will signal to the RP that we wish to use a discoverable credential flow by passing in an empty username.

```json
{
  "userName": ""
}
```

##### Non-discoverable credential flow

In order to trigger the use of a non-discoverable credential flow, you will include the `userName` associated to the user who is trying to authenticate.

```json
{
  "userName": "user@acme.com"
}
```

#### Response

As indicated in the previous section, this method will include two different types of responses. In the WebAuthn specification, this object is referred to as the [PublicKeyCredentialCRequestOptions](https://www.w3.org/TR/webauthn-2/#dictionary-assertion-options), but will contain extra data depending on the flow being used.

##### Discoverable credential flow

Below is the response body of the `/attestation/options` method for a discoverable credential flow

```json
{
  "requestId": "WlEPtNrJps-E03p_rwfXqASFkbrQ6Ml3Oy031JW4TYo",
  "publicKey": {
    "challenge": "NGc3jpB4Q-VnOmbhFBnDAczlYPT4soKA7xviGeJmDhc",
    "timeout": 180000,
    "rpId": "localhost",
    "userVerification": "preferred"
  }
}
```

##### Non-discoverable credential flow

Below is the response body of the `/attestation/options` method for a non-discoverable credential flow

```json
{
  "requestId": "B-J4odOi9vcV-4TN_gpokEb1f1EI...",
  "publicKey": {
    "challenge": "m7xl_TkTcCe0WcXI2M-4ro9vJAuwcj4m",
    "timeout": 20000,
    "rpId": "example.com",
    "allowCredentials": [
      {
        "id": "opQf1WmYAa5aupUKJIQp",
        "type": "public-key"
      }
    ],
    "userVerification": "preferred"
  }
}
```

Note the major difference in both responses is the inclusion of the `allowCredentials` property, which **only exists for non-discoverable credential flows**. The `id` field in an `allowCredentials` entry relates to the credential ID of a WebAuthn credential that belongs to the user identified in the request body.

This means that the WebAuthn ceremony will only succeed if the user can demonstrate ownership of one of the credentials provided in the `allowedCredentials` list. The credential present in this list should be all of the credentials belonging to that user.

The **exclusion** of this list will allow the user to select a passkey (discoverable credential) on their authenticator, if one exists.

The key difference is that the non-discoverable flow is looking for a specific set of credentials, while the discoverable flow is looking for the user to select one of their credentials.

### Implementation

Below is a sample implementation of the /attestation/options method. Note that `request` should correlate to the request body mentioned in the previous section, and `response` should correlate to the request response mentioned in the previous section.

```java
public AssertionOptionsResponse assertionOptions(AssertionOptionsRequest request) throws Exception {
  try {
    /**
     * Begin to build the PKC options
     */

    StartAssertionOptionsBuilder optionsBuilder = StartAssertionOptions.builder();

    // Configure the options with default values
    optionsBuilder.userVerification(UserVerificationRequirement.PREFERRED).timeout(180000);

    /**
     * Check if the user has a credential stored in the DB
     */
    Collection<CredentialRegistration> credentials = relyingPartyInstance.getStorageInstance().getCredentialStorage()
        .getRegistrationsByUsername(request.getUserName());

    /**
     * If the user has no credentials, or the username was blank, then
     * do not attempt to attach the allowCredentials property
     */
    if (credentials.size() != 0 || request.getUserName() != "") {
      /*
        * To preserve privacy, if a request was made with a non-existent username, or
        * missing a username
        * then a discoverable credentials flow will be enabled
        */

      optionsBuilder.username(request.getUserName());
    }
    AssertionRequest pkc = relyingPartyInstance.getRelyingParty()
        .startAssertion(optionsBuilder.build());

    ByteArray requestId = generateRandomByteArray(32);

    /**
     * Helper method to translate the pkc object into
     * strings for use in the JSON request
     */
    AssertionOptionsResponse response = AssertionOptionsResponseConverter
        .PKCtoResponse(pkc.getPublicKeyCredentialRequestOptions(), requestId);

    AssertionRequestStorage.insert(pkc, requestId.getBase64Url());

    return response;
  } catch (Exception e) {
    e.printStackTrace();
    throw new Exception("There was an issue while generating AssertionOptions: " + e.getMessage());
  }
}
```

## Assertion result method

In this section we are going to outline, in detail, the assertion result method as well as provide a sample implementation in Java using the [java-webauthn-server library](https://github.com/Yubico/java-webauthn-server).

### API request and response schema

#### Request

Below is the request body of the `/assertion/result` method

```json
{
  "requestId": "B-J4odOi9vcV-4TN_gpokEb1f1EI...",
  "assertionResult": {
    "id": "LFdoCFJTyB82ZzSJUHc-c72yraRc_1mPvG",
    "response": {
      "authenticatorData": "SZYN5Gh0NBcPZHZgW4_krrmihjzzuoMdl2MBAAAAAA...",
      "signature": "ME8fLjd5y6TUOLWt5l9DQIhANiYig9newAJZYTzG1i5lwP",
      "userHandle": "string",
      "clientDataJSON": "eyJjaGFTBrTmM4uIjoiaHR0cDovL2xvY2FsF1dGhuLmdldCJ9..."
    },
    "type": "public-key",
    "clientExtensionResults": {}
  }
}
```

These properties will include the result of the `navigator.credentials.get` method, along with the requestId, that was included in the `/assertion/options` response.

In most cases, you will directly pass the result of the `navigator.credentials.get` method into the `assertionResult` property.

#### Response

Below is the response body of the `/assertion/result` method

```json
{
  "status": "ok"
}
```

In this case, the result is simple. We include a property, `status`, that denotes the result of the authentication ceremony. If successful, then the status should be returned as `ok`, signaling to the caller that the authentication ceremony was successful. Otherwise, an error occurred and should be conveyed to the user.

### Implementation

Below is a sample implementation of the /assertion/result method. Note that `request` should correlate to the request body mentioned in the previous section, and `response` should correlate to the request response mentioned in the previous section.

```java
public AssertionResultResponse assertionResponse(AssertionResultRequest response) throws Exception {
  try {
    /**
     * Check for assertion request
     */
    Optional<AssertionOptions> maybeOptions = AssertionRequestStorage.getIfPresent(response.getRequestId());

    AssertionOptions options;

    if (maybeOptions.isPresent()) {
      options = maybeOptions.get();
    } else {
      throw new Exception("Registration request not present");
    }

    /**
     * Check if the request is still active
     */
    if (!options.getIsActive()) {
      // Not active, return error
      throw new Exception("Registration request is no longer active");
    } else {
      AssertionRequestStorage.invalidate(response.getRequestId());
    }

    /**
     * Build assertion request
     */
    AssertionRequestBuilder requestBuilder = AssertionRequest.builder()
        .publicKeyCredentialRequestOptions(options.getAssertionRequest().getPublicKeyCredentialRequestOptions());

    // Check if a username was present in the request
    if (options.getAssertionRequest().getUsername().isPresent()) {
      requestBuilder.username(options.getAssertionRequest().getUsername().get());
    }

    AssertionResult result = relyingPartyInstance.getRelyingParty().finishAssertion(FinishAssertionOptions.builder()
        .request(requestBuilder.build())
        .response(parseAssertionResponse(response.getAssertionResult()))
        .build());

    if (result.isSuccess()) {
      return AssertionResultResponse.builder().status("ok").build();
    } else {
      throw new Exception("Your assertion failed for an unknown reason");
    }
  } catch (Exception e) {
    e.printStackTrace();
    throw new Exception("There was an issue finalizing your assertion your credential: " + e.getMessage());
  }
}
```
