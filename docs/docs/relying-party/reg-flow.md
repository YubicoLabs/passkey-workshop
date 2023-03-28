---
sidebar_position: 3
---

# Registration flows

In this section we are going to go over the registration flow for a new passkey. Note that the use of the term registration refers to the creation of a passkey, not the initial registration of a user (which is an entirely different subject).

By the end of this section you will understand how to use and implement both of the `/attestation/options` and `/attestation/result` methods (as defined by our [API](relying-party/api-def))

## Flow overview

The diagram below demonstrates how the relying party works with the client, and authenticator to register a new passkey. When interacting with the relying party, the client will leverage both of the `/attestation` methods.

![Docusaurus logo](/img/reg-flow.jpg)

The first call ([`/attestation/options`](http://localhost:8080/swagger-ui/index.html#/v1/serverPublicKeyCredentialCreationOptionsRequest)) is used to receive an object that includes the options/configurations that should be used when creating a new credential.

The second call ([`/attestation/result`](http://localhost:8080/swagger-ui/index.html#/v1/serverAuthenticatorAttestationResponse)) is used to send the newly created passkey to be stored in the relying party.

## Attestation options method

In this section we are going to outline, in detail, the attestation options method as well as provide a sample implementation in Java using the [java-webauthn-server library](https://github.com/Yubico/java-webauthn-server).

### API request and response schema

#### Request

Below is the request body of the `/attestation/options` method

```json
{
  "userName": "user@acme.com",
  "displayName": "User 1",
  "authenticatorSelection": {
    "residentKey": "preferred",
    "authenticatorAttachment": "cross-platform",
    "userVerification": "preferred"
  },
  "attestation": "direct"
}
```

These properties will help the client declare the options that it wishes to use when invoking the `navigator.credentials.create` method. The properties can be broken into two categories:

- Characteristics of the user
- Rules for generating the passkey

`userName`, and `displayName` are used to declare the user that we are creating a passkey for. Our implementation separates these into two separate properties as the display name is what the user might see in your app, and the username may be some sort of unique identifier such as an email address.

The other properties will instruct your client, specifically your ecosystem (platform + browser) on how to generate the credential. These properties are defined in various sections in the WebAuthn specification, which will be noted below.

`attestation` allows you to set the [AttestationConveyancePreference](https://www.w3.org/TR/webauthn-2/#enum-attestation-convey). This will allow you to detect characteristics of the authenticator. More information about attestation can be found at [this location](https://developers.yubico.com/Passkeys/Passkey_relying_party_implementation_guidance/Attestation/). The options for this property are:

- direct (default)
- indirect
- enterprise
- none

`residentKey` defines the [ResidentKeyRequirement](https://www.w3.org/TR/webauthn-2/#enum-residentKeyRequirement), or the relying party's preference for creating a [discoverable credential](linktofundamentalsection). It's important to remember that passkeys MUST be a discoverable credential.

- required
- preferred (default)
- discouraged

::::danger Behaviors of preferred
Different browsers may treat required in different ways. In most cases preferred will attempt to create a passkey, if the option is available (this applies to both security keys and platform authenticators).
::::

::::danger Limits on hardware authenticators
Keep in mind that some devices, like security keys, may have a limit on the number of passkeys that can be registered
::::

`userVerifications` defines the [UserVerificationRequirement](https://www.w3.org/TR/webauthn-2/#enumdef-userverificationrequirement), or the preference to perform [user verification](linktofundsection). In short, the RP can declare if they want to force the user to use something like a security key PIN, biometric scan (Face ID), or other forms of verification.

- required
- preferred (default)
- discouraged

`authenticatorAttachment` defines the [AuthenticatorAttachment](https://www.w3.org/TR/webauthn-2/#enum-attachment) requirement, or the modality that HAS to be leveraged when creating a credential. Cross platform will force the user to leverage a security key, or hybrid flow. Platform will force the user to use a platform authenticator (like Touch ID, Face ID, Windows Hello) if available. Excluding this option will allow a user to use ANY modality.

::::tip Best to be permissive
Err on the side of caution when using this setting. For most use cases you should not set this setting, and instead allow your users to use any authenticator that they want to use. Allow for both the convenience of platform authenticators, and the high degree of assurance of security keys for the users who want it.
::::

- platform
- cross-platform
- Empty | none | exclude property (default)

#### Response

Below is the response body of the `/attestation/options` method

```json
{
  "requestId": "B-J4odOi9vcV-4TN_gpokEb1f1EI...",
  "publicKey": {
    "rp": {
      "name": "RP Name",
      "id": "rp.id.com"
    },
    "user": {
      "id": "abc123",
      "name": "user@acme.com",
      "displayName": "User 1"
    },
    "challenge": "uhUjPNlZfvn7onwuhNdsLPkkE5Fv-lUN",
    "pubKeyCredParams": [
      {
        "type": "public-key",
        "alg": -7
      }
    ],
    "timeout": 10000,
    "excludeCredentials": [
      {
        "type": "public-key",
        "id": "opQf1WmYAa5au-kk-pUKJIQp..."
      }
    ],
    "authenticatorSelection": {
      "residentKey": "preferred",
      "authenticatorAttachment": "cross-platform",
      "userVerification": "preferred"
    },
    "attestation": "direct"
  }
}
```

In the WebAuthn specification, this object is referred to as the [PublicKeyCredentialCreationOptions](https://www.w3.org/TR/webauthn-2/#dictdef-publickeycredentialcreationoptions).

Using the example above, you may be able to identify the options that we set in the request body. The `user.name`, `user.displayName`, `authenticatorSelection.residentKey`, `authenticatorSelection.authenticatorAttachment`, `authenticatorSelection.userVerification`, and `attestation` should all correlate to the options that we set in the previous section.

`rp.id`, `timeout` and `publicKeyCredParams` are typically defaults provided by the relying party.

The `challenge` should be randomly generated. This random number generated should be based on the RNG requirements set by your security policy. In our example, this challenge is randomly generated using Java's random number generator.

`excludeCredentials` is a mechanism that will prevent your user from attempting to generate endless passkeys on a device. If a credential on your authenticator includes an ID that matches to one found in this list, then the authenticator will not be allowed to generate a new passkey.

`requestId` is a best practice field that we include in our API. This helps to ensure that the user leveraging a valid request, and is used to prevent replay attacks (attempting to register a credential without a users knowledge).

### Implementation

Below is a sample implementation of the /attestation/options method. Note that `request` should correlate to the request body mentioned in the previous section, and `response` should correlate to the request response mentioned in the previous section.

```java
public AttestationOptionsResponse attestationOptions(AttestationOptionsRequest request) throws Exception {
  try {

    /**
     * See if the user exists
     */
    Optional<ByteArray> maybeUID = CredentialStorage.getUserHandleForUsername(request.getUserName());

    UserIdentity userIdentity = UserIdentity.builder()
        .name(request.getUserName())
        .displayName(request.getDisplayName())
        /**
         * If there is an existing user, attach their userhandle
         * otherwise generate a new one
         */
        .id(maybeUID.isPresent() ? maybeUID.get() : generateRandomByteArray(16))
        .build();

    /*
      * This method has been abstracted as there are a lot of "is not null" checks
      * The goal is to convert the string values into valid ENUM for the
      * AuthenticatorSelectionCriteria
      */
    AuthenticatorSelectionCriteria optionsSelectionCriteria = resolveAuthenticatorSelectionCriteria(
        request.getAuthenticatorSelection());

    PublicKeyCredentialCreationOptions pkc = relyingPartyInstance.getRelyingParty()
        .startRegistration(StartRegistrationOptions.builder()
            .user(userIdentity)
            .authenticatorSelection(optionsSelectionCriteria)
            .timeout(180000) // 3 minutes
            .build());

    ByteArray requestId = generateRandomByteArray(32);

    /**
     * This method is a helper that converts the values in the pkc object to
     * string that conforms to the API
     */
    AttestationOptionsResponse response = AttestationOptionsResponseConverter.PKCtoResponse(pkc, requestId);

    /*
      * Insert the request into the attestation request storage to prevent replay
      * attacks
      */
    AttestationRequestStorage.insert(pkc, requestId.getBase64Url());

    return response;

  } catch (Exception e) {
    e.printStackTrace();
    throw new Exception("There was an issue while generating AttestationOptions: " + e.getMessage());
  }
}

private static AuthenticatorSelectionCriteria resolveAuthenticatorSelectionCriteria(
    AttestationOptionsRequestAuthenticatorSelection request) {
  /*
    * Generate authenticator selection
    * 1. Start by inferring the values from the request, if the values were not
    * present, then select a default
    * 2. Determine if the object needs to contain an authenticatorAttachment
    */
  AuthenticatorSelectionCriteriaBuilder authSelectBuilder = AuthenticatorSelectionCriteria.builder();

  /*
    * Set the UV requirement, set to preferred if not present
    */

  if (request.getUserVerification() == null || request.getUserVerification().getValue().equals("")) {
    authSelectBuilder.userVerification(UserVerificationRequirement.PREFERRED);
  } else {
    String uvReq = request.getUserVerification().getValue().toUpperCase();
    authSelectBuilder.userVerification(UserVerificationRequirement.valueOf(uvReq));
  }

  /*
    * Set the resident key requirement, set to preferred if not present
    */
  if (request.getResidentKey() == null || request.getResidentKey().getValue().equals("")) {
    authSelectBuilder.residentKey(ResidentKeyRequirement.PREFERRED);
  } else {
    String rkReq = request.getResidentKey().getValue().toUpperCase();
    authSelectBuilder.residentKey(ResidentKeyRequirement.valueOf(rkReq));
  }

  /*
    * Set the auth attachment requirement, do not include the property if not
    * included
    */
  if (request.getAuthenticatorAttachment() != null) {
    String aaReq = request.getAuthenticatorAttachment().getValue();
    if (aaReq.equals("cross-platform")) {
      authSelectBuilder.authenticatorAttachment(AuthenticatorAttachment.CROSS_PLATFORM);
    } else if (aaReq.equals("platform")) {
      authSelectBuilder.authenticatorAttachment(AuthenticatorAttachment.PLATFORM);
    }
  }

  return authSelectBuilder.build();
}
```

## Attestation result method

In this section we are going to outline, in detail, the attestation result method as well as provide a sample implementation in Java using the [java-webauthn-server library](https://github.com/Yubico/java-webauthn-server).

### API request and response schema

#### Request

Below is the request body of the `/attestation/result` method

```json
{
  "requestId": "B-J4odOi9vcV-4TN_gpokEb1f1EI...",
  "makeCredentialResult": {
    "id": "LFdoCFJSJUHc-c72yraRc_1mDvruywA",
    "response": {
      "clientDataJSON": "eyJj0OiJIiwidHlwZSI6IndlYmF1dGhyZWF0ZSJ9...",
      "attestationObject": "o2NmbXRNAQELBQADgbpNt2IOL4i4z96Kqo1rqSUmonen..."
    },
    "type": "public-key",
    "clientExtensionResults": {}
  }
}
```

These properties will include the result of the `navigator.credentials.create` method, along with the requestId, that was included in the `/attestation/options` response.

In most cases, you will directly pass the result of the `navigator.credentials.create` method into the `makeCredentialResult` property.

#### Response

Below is the response body of the `/attestation/result` method

```json
{
  "status": "created"
}
```

In this case, the result is simple. We include a property, `status`, that denotes the result of the registration. If successful, then the status should be returned as created, signaling to the caller that the credential was successfully saved. Otherwise, an error occurred and should be conveyed to the user.

### Implementation

Below is a sample implementation of the /attestation/result method. Note that `request` should correlate to the request body mentioned in the previous section, and `response` should correlate to the request response mentioned in the previous section.

```java
 public AttestationResultResponse attestationResult(AttestationResultRequest response) throws Exception {
    try {
      /*
       * Check if there is an active registration request
       */
      Optional<AttestationOptions> maybeOptions = AttestationRequestStorage.getIfPresent(response.getRequestId());

      AttestationOptions options;

      if (maybeOptions.isPresent()) {
        options = maybeOptions.get();
      } else {
        throw new Exception("Registration request not present");
      }

      /*
       * Check if the request is still active
       */

      if (!options.getIsActive()) {
        // Not active, return error
        throw new Exception("Registration request is no longer active");
      } else {
        // Invalidate the request so that it can't be used again
        AttestationRequestStorage.invalidate(response.getRequestId());
      }

      RegistrationResult newCred = relyingPartyInstance.getRelyingParty()
          .finishRegistration(FinishRegistrationOptions.builder()
              .request(options.getAttestationRequest())
              .response(parseRegistrationResponse(response.getMakeCredentialResult()))
              .build());

      // Encode the credential in a way that can be stored using your data schema
      CredentialRegistration toStore = buildCredentialDBO(options.getAttestationRequest(), newCred);

      if (CredentialStorage.addRegistration(toStore)) {
        return new AttestationResultResponse().status("created");
      } else {
        throw new Exception("There was an unknown issue creating your credential");
      }

    } catch (Exception e) {
      e.printStackTrace();
      throw new Exception("There was an issue while registering your credential: " + e.getMessage());
    }
  }
```
