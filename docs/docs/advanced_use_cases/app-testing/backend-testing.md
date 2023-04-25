---
sidebar_position: 1
---

# Backend testing

Backend testing refers to the testing methods used for the server-side/database component of your application. On this page we will outline testing strategies for the relying party component of your passkey application. Note that this page is not meant to teach you every technique used for server side testing; we will focus only on the testing related to the passkey specific methods in your application.

Overall, our goal is to test the passkey related methods in order to ensure that your implementation is able to construct valid public key options for registration and authentication, as well as the ability to register new credentials, and validate assertion results.

## Virtual authenticators

What makes unit testing different for passkey related applications, as opposed to standard applications? The main difference is the need to generate credentials and assertion results. In order to test a passkey application, you need to create new credentials to test your attestation options/result methods, and you need to be able to use that same credential to create assertions that test your assertion options/results methods.

In the real world your users will be utilizing authenticators such as Yubikeys, Windows Hello, Touch ID, etc. When you're performing tests in an isolated backend environment, these authenticators may not be readily available, or they may not allow for automated testing without some form of user presence. So how do you leverage an authenticator in your backend environment?

A virtual authenticator is meant to be a full software implementation that performs and mimics the same actions as a real authenticator. The software authenticator should be capable of creating new credentials, and utilizing those credentials to perform assertions.

When considering a software authenticator for your testing strategy you should ensure:

- The virtual authenticator can be used in your environment (operating system, programming language/framework, testing framework)
- The virtual authenticator supports your required WebAuthn features (resident keys, user verification, and authenticator attachment)

In our examples below, we will be leveraging [softauthn](https://github.com/adessoSE/softauthn), which is a virtual authenticator based in Java. This implementation is built leveraging the java-webauthn-server, so it makes it easy to incorporate into our example.

::::danger Supporting attestation
One major drawback of leveraging virtual authenticators for automated testing is their ability to support attestation. If your relying party is used for high assurance scenarios, and requires the presence of an attestation statement with a valid AAGUID and trust root certificate, then you may be unable to take this approach. Virtual authenticators may not include attestation as there is no guarantee that the credential originated from a trusted source.  
::::

## Testing attestation methods

This section will dive into how to test the attestation (registration) methods in your relying party.

### Attestation options method testing

First we will attempt to test the method that will be used to generate attestation options. To recap, the [attestation options method](/docs/relying-party/reg-flow#attestation-options-method) is what will be used to convey to the client the characteristics of the passkey that we want created.

**Assumptions:**

- A successful result returned by the method contains a valid `PublicKeyCredentialCreationOptions` object

#### Test cases:

Below are the base test cases to ensure that your method is returning a valid `PublicKeyCredentialCreationOptions`, based on some defined values:

- Ensure that the resulting object of a successful call is an AttestationOptionsResult object (as defined by a Java class in our project)
- Ensure that the resulting `PublicKeyCredentialCreationOptions` includes the defined relying party ID
- Ensure that the username and displayName provided to the method are represented in the `user` object in the result
- Ensure that the authenticatorSelection criteria provided to the method are represented in the result

```java
@Test
void attestationOptions() {
  String username = "assertion_options1@acme.com";
  String displayName = "assertion_options1";

  /**
   * Check that the encoding method is working properly
   * JSON -> AttestationOptionsRequest
   */
  AttestationOptionsRequest request = attestationOptionsRequestGenerator(
      username,
      displayName,
      "direct",
      "preferred",
      "preferred",
      "cross-platform");
  assertInstanceOf(AttestationOptionsRequest.class, request);
  try {
    /**
     * Pass the AttestationOptionsRequest object into our AttestationOptions method
     */
    AttestationOptionsResponse response = passkeyOperations.attestationOptions(request);
    assertInstanceOf(AttestationOptionsResponse.class, response);

    /**
     * Ensure the correct RP ID is conveyed
     */
    assertEquals(env.getProperty("RP_ID"), response.getPublicKey().getRp().getId());

    /**
     * Test user attributes
     */
    assertEquals(username, response.getPublicKey().getUser().getName());
    assertEquals(displayName, response.getPublicKey().getUser().getDisplayName());

    /**
     * Test residentKey option
     */
    assertEquals("preferred",
              response_rkDiscouraged.getPublicKey().getAuthenticatorSelection()
                  .getResidentKey().getValue());

    /**
     * Test user verification option
     */
    assertEquals("preferred",
        response_uvDiscouraged.getPublicKey().getAuthenticatorSelection()
            .getUserVerification().getValue());

    /**
     * Test authenticator attachment option
     */
    assertEquals("cross-platform",
      response_aaCrossPlatform.getPublicKey().getAuthenticatorSelection()
          .getAuthenticatorAttachment()
          .getValue());


  } catch (Exception e) {
    fail("Test failed failed: " + e.getMessage());
  }
}

/**
 * Used to construct an AttestationOptionsRequest object to be processed by our
 * passkeyServices instance
 *
 * This is meant to mimic a JSON encoded request that will be sent to the API
 */
private AttestationOptionsRequest attestationOptionsRequestGenerator(
    String userName,
    String displayName,
    String attestation,
    String residentKey,
    String userVerification,
    String authenticatorAttachment) {
  ObjectNode rootNode = mapper.createObjectNode();

  ObjectNode authenticatorSelection = mapper.createObjectNode();
  authenticatorSelection.put("residentKey", residentKey);
  authenticatorSelection.put("userVerification", userVerification);
  authenticatorSelection.put("authenticatorAttachment", authenticatorAttachment);

  rootNode.set("authenticatorSelection", authenticatorSelection);

  rootNode.put("userName", userName);
  rootNode.put("displayName", displayName);
  rootNode.put("attestation", attestation);

  return mapper.convertValue(rootNode, AttestationOptionsRequest.class);
}
```

Some additional test cases that can be performed include:

- Test different permutations of the residentKey setting (discouraged, preferred, required, none, field not included, non-valid enumeration)
- Test different permutations of the userVerification setting (discouraged, preferred, required, none, field not included, non-valid enumeration)
- Test different permutations of the authenticatorAttachment setting (cross-platform, platform, field not included, field is null)

### Attestation result method testing

Next we will attempt to test the method that will be used to register a new passkey to the application using the [attestation result method](/docs/relying-party/reg-flow#attestation-result-method). In this scenario the client would send a new credential, along with a valid requestID to be processed by the relying party.

**Assumptions:**

- We have validated that the attestation options method will return a valid `PublicKeyCredentialCreationOptions` object, with our chosen settings.

**Creating a virtual authenticator**

Before we dive into the implementation of our unit test, let's understand how to define a virtual authenticator. This virtual authenticator should accept the options generated by our relying party, generate a valid credential, and send that credential to our relying party.

We are leveraging the [softauthn](https://github.com/adessoSE/softauthn) virtual authenticator. Below is an example of how to leverage this library to create a virtual authenticator. These instructions will change based on your chosen virtual authenticator, but the general flow and principles should remain the same.

We will define this example as a method, as you may want to reuse this logic to create different authenticators for each test case, so that you don't inadvertently negatively influence any other test cases.

```java
/**
 * Used to initialize a CredentialContainer from the softauthn library
 * https://github.com/adessoSE/softauthn
 * Acts as a virtual authenticator that mimics a real WebAuthn authenticator
 *
 * @return object that can invoke create and get requests for unit testing
 */
private CredentialsContainer createCredentialContainer() {
  var authenticator = WebAuthnAuthenticator.builder()
      .attachment(AuthenticatorAttachment.CROSS_PLATFORM)
      .supportClientSideDiscoverablePublicKeyCredentialSources(true)
      .supportUserVerification(true)
      .build();

  var origin = new Origin("http", "localhost", 3000, null);

  List<Authenticator> authList = new ArrayList<>();
  authList.add(authenticator);

  return new CredentialsContainer(origin, authList);
}
```

Note the options used above. We are declaring an authenticator that is meant to mimic a security key ("CROSS_PLATFORM), that also allows for discoverable credentials (passkeys), and that supports user verification. You can change these, or even create multiple different methods to initialize different authenticator types.

#### Test cases:

Below are the base test cases to ensure that your method is able to register a newly created credential

- Ensure that the resulting object of a successful call is an AttestationResultResponse object with a status defined as "created"

```java
@Test
void attestationResult() {
  /**
   * Create a credential using the method defined above
   */
  CredentialsContainer credentials = createCredentialContainer();

  try {
    /**
     * Leverage the same logic above to create attestation options
     */
    AttestationOptionsResponse attestationOptionsResponse = //use the logic above

    /**
     * Format the PublicKey included in the attestationOptionsResponse to be usable by our virtual auuthenticator
     *
     * Note that this is not necessary from the client itself, this is done mostly to appease Java's strict class definitions and encoding
     */
    String attestationOptionsResponse_json = mapper.writeValueAsString(attestationOptionsResponse.getPublicKey());
    PublicKeyCredentialCreationOptions options = PublicKeyCredentialCreationOptions.fromJson(attestationOptionsResponse_json);

    /**
     * Attempt to create a new credential using the softauthn Java library
     * https://github.com/adessoSE/softauthn
     */
    PublicKeyCredential<AuthenticatorAttestationResponse, ClientRegistrationExtensionOutputs> pkc = credentials
        .create(options);

    /**
     * Convert the requestId and the newly created credential to JSON then to an
     * AttestationResultRequest
     * This is meant to mimic the real world where a credential will be returned
     * from a `create` call as a JSON object, then encoded by the ObjectMapper used
     * by our API
     */
    AttestationResultRequest attestationResult = buildAttestationResultResponse(
        attestationOptionsResponse.getRequestId(), pkc);

    AttestationResultResponse attestationResultResponse = passkeyOperations
        .attestationResult(attestationResult);

    /**
     * Ensure that method returns with a status of created
     */
    assertEquals("created", attestationResultResponse.getStatus());
  } catch (Exception e) {
    fail("Test failed: " + e.getMessage());
  }
}
```

Some additional test cases that can be performed are:

- Test to ensure that a requestID cannot be used after it has been leveraged to register a new credential
- Test to ensure that requestID provided correlates to the `PublicKeyCredentialCreationOptions` used to create a credential

## Testing assertion methods

This section will dive into how to test the assertion (authentication) methods in your relying party.

### Assertion options method testing

First we will attempt to test the method that will be used to generate assertion options. To recap, the [assertion options method](/docs/relying-party/auth-flow#assertion-options-method) is what will be used to convey to the client the characteristics of the passkey that should be used for the assertion.

As you may recall, our relying party example supports both discoverable and non-discoverable credentials. Each credential type has its own specific mechanisms when being invoked, and may or may not include an allowCredentials list.

**Assumptions:**

- A successful result returned by the method contains a valid `PublicKeyCredentialRequestOptions` object for the user denoted
- If a user is not denoted, or the user does not have any passkeys in the application, then a `PublicKeyCredentialRequestOptions` object will be returned without an `allowCredentials` list
- If a user is denoted, and has passkeys registered in the application, then a `PublicKeyCredentialRequestOptions` object will be returned with an `allowCredentials` list populated with the credential IDs belonging to the user

#### Test cases:

The first test case will be for discoverable credentials (passkeys). We will pass an empty userName into the assertion options method to denote that we are leveraging a discoverable credentials flow.

Below are the base test cases to ensure that your method is able to create a discoverable credentials assertion object

- Ensure that the resulting `PublicKeyCredentialRequestOptions` includes the defined relying party ID
- Ensure that the resulting `PublicKeyCredentialRequestOptions` does not include an allowCredentials list

```java
@Test
void assertionOptions_DiscoverableCredentials() {
  try {
    /**
     * Discoverable credential flow, so we will pass an empty username
     */
    AssertionOptionsRequest assertionOptionsRequest = assertionOptionsRequestGenerator("");

    AssertionOptionsResponse assertionOptionsResponse = passkeyOperations.assertionOptions(assertionOptionsRequest);
    assertInstanceOf(AssertionOptionsResponse.class, assertionOptionsResponse);

    /**
     * Ensure the correct RP ID is conveyed
     */
    assertEquals(env.getProperty("RP_ID"), assertionOptionsResponse.getPublicKey().getRpId());

    /**
     * Ensure the allowCredentials list is not present
     */
    assertEquals(null, assertionOptionsResponse.getPublicKey().getAllowCredentials());

  } catch (Exception e) {
    fail("Test failed: " + e.getMessage());
  }
}

/**
 * Builds an AssertionOptionsRequest to be processed by the passkeyServices
 * instance
 *
 * Meant to mimic a JSON encoded object that will be sent to the API
 */
private AssertionOptionsRequest assertionOptionsRequestGenerator(
    String userName) {
  ObjectNode rootNode = mapper.createObjectNode();
  rootNode.put("userName", userName);

  return mapper.convertValue(rootNode, AssertionOptionsRequest.class);
}
```

The next test case will be for non-discoverable credentials. We will pass an empty userName into the assertion options method to denote that we are leveraging a discoverable credentials flow.

Below are the base test cases to ensure that your method is able to create a non-discoverable credentials assertion object

- Ensure that the resulting `PublicKeyCredentialRequestOptions` includes the defined relying party ID
- Ensure that the resulting `PublicKeyCredentialRequestOptions` includes an allowCredentials list with one credential

Note that we will need to create a credential registration for the specific user. This is done to ensure that the `allowCredentials` list supplied in the response will have at least one credential ID listed.

```java
@Test
void assertionOptions_NonDiscoverableCredentials() {
  try {
    CredentialsContainer credentials = createCredentialContainer();
    String username = "assertion_options1@test.com";

    /**
     * Copy the logic in the previous section to create a new credential for testing
     */
    // Insert credential creation logic here

    /**
     * Create assertionOptions for the specified user
     */
    AssertionOptionsRequest assertionOptionsRequest = assertionOptionsRequestGenerator(username);

    AssertionOptionsResponse assertionOptionsResponse = passkeyOperations.assertionOptions(assertionOptionsRequest);
    assertInstanceOf(AssertionOptionsResponse.class, assertionOptionsResponse);

    /**
     * Ensure the correct RP ID is conveyed
     */
    assertEquals(env.getProperty("RP_ID"), assertionOptionsResponse.getPublicKey().getRpId());

    /**
     * Ensure the allowCredentials list is contains one entry
     */
    assertEquals(1, assertionOptionsResponse.getPublicKey().getAllowCredentials().size());
  } catch (Exception e) {
    fail("@assertionOptions_NonDiscoverableCredentials failed: " + e.getMessage());
  }
}
```

### Assertion result method testing

Finally we will attempt to test the method that will be used to validate an assertion provided by a passkey, using the [assertion result method](/docs/relying-party/auth-flow#assertion-result-method). This step will leverage a combination of all of the concepts above as we will need to create attestation options, then use those options to register a credential, then create assertion options, then create an assertion with the options, then send the assertion to the relying party for validation.

#### Test cases:

The test case will be used to ensure that we authenticate with a passkey (discoverable credential flow).

Below are the base test cases to ensure that your method to validate an assertion result created by an authenticator

- Ensure that none of the subsequent steps (attestation options/result, and assertion options) do not fail
- Ensure that the assertion result method returns with a status of "ok".

```java
@Test
void assertionResult_DiscoverableCredentials() {
  try {
    CredentialsContainer credentials = createCredentialContainer();

    /**
     * Copy the logic in the previous section to create a new credential for testing,
     * and for creating assertionOptions using an empty username
     */
    // Insert credential creation logic here
    AssertionOptionsResponse asseertionOptionsResponse = //Generate assertion response with a empty username

    /**
     * Format the PublicKey included in the assertionOptionsResponse to be usable by our virtual authenticator
     *
     * Note that this is not necessary from the client itself, this is done mostly to appease Java's strict class definitions and encoding
     */
    String assertionOptionsResponse_json = mapper.writeValueAsString(assertionOptionsResponse.getPublicKey());
    PublicKeyCredentialRequestOptions pkc_assertionOptions = assertionOptionstoPKC(assertionOptionsResponse_json);

    /**
     * assertionResult provided by the WebAuthn GET ceremony
     */
    PublicKeyCredential<AuthenticatorAssertionResponse, ClientAssertionExtensionOutputs> credential = credentials
        .get(pkc_assertionOptions);

    /**
     * Package the assertionResult, and requestID into a format that can be used by
     * our RP
     *
     * This will mimic the JSON encoded string that will be sent by our client
     */
    AssertionResultRequest assertionResultRequest = buildAssertionResultResponse(
        assertionOptionsResponse.getRequestId(), credential);

    AssertionResultResponse assertionResultResponse = passkeyOperations.assertionResponse(assertionResultRequest);

    /**
     * Ensure the status property returned by the API denotes "ok"
     *
     * This means that a successful auth ceremony was completed
     */
    assertTrue(assertionResultResponse.getStatus().equals("ok"));

  } catch (Exception e) {
    fail("@assertionResult_DiscoverableCredentials failed: " + e.getMessage());
  }
}

/**
 * Need this method to add the extensions property to our PKC object
 *
 * @param jsonString
 * @return
 */
private PublicKeyCredentialRequestOptions assertionOptionstoPKC(
    String jsonString) throws Exception {

  try {
    ObjectNode rootNode = mapper.readValue(jsonString, ObjectNode.class);

    ObjectNode extensionRoot = mapper.createObjectNode();

    rootNode.set("extensions", extensionRoot);
    return mapper.convertValue(rootNode, PublicKeyCredentialRequestOptions.class);
  } catch (Exception e) {
    throw e;
  }

}

/**
 * Helper method to turn the new credential into an AttestationResultRequest
 * object to be processed by our API
 *
 * @param requestId id of the attestation request
 * @param pkc       newly created credential, in a Java object format
 * @return requestid and pkc packaged as an AttestationResultRequest, from an
 *         encoded JSON string
 */
private AssertionResultRequest buildAssertionResultResponse(String requestId,
    PublicKeyCredential<AuthenticatorAssertionResponse, ClientAssertionExtensionOutputs> pkc) {
  ObjectNode rootNode = mapper.createObjectNode();

  rootNode.put("requestId", requestId);
  rootNode.set("assertionResult", buildAssertionResultObject(pkc));

  return mapper.convertValue(rootNode, AssertionResultRequest.class);
}

/**
 * Builds the inner layer of our AttestationResultRequest (makeCredentialResult
 * object)
 * Jackson has some issues encoding this as our makeCredentialResult object does
 * not make use of all the fields provided by the softauthn credential creation
 *
 * @param pkc
 * @return
 */
private ObjectNode buildAssertionResultObject(
    PublicKeyCredential<AuthenticatorAssertionResponse, ClientAssertionExtensionOutputs> pkc) {
  ObjectNode rootNode = mapper.createObjectNode();
  rootNode.put("id", pkc.getId().getBase64Url());
  rootNode.put("type", pkc.getType().getId());

  /**
   * Our project isn't taking advantage of the clientExtensionResults
   * We will default this to an empty object until we add it to our API schema for
   * use
   */
  ObjectNode emptyClientExtensionNode = mapper.createObjectNode();
  rootNode.set("clientExtensionResults", emptyClientExtensionNode);

  ObjectNode response = mapper.createObjectNode();
  response.put("authenticatorData", pkc.getResponse().getAuthenticatorData().getBase64Url());
  response.put("clientDataJSON", pkc.getResponse().getClientDataJSON().getBase64Url());
  response.put("signature", pkc.getResponse().getSignature().getBase64Url());
  response.put("userHandle", pkc.getResponse().getUserHandle().get().getBase64Url());
  rootNode.set("response", response);

  return rootNode;
}
```

Other test cases that you can run for this scenario are:

- Leveraging the same flow for non-discoverable credentials
- Ensure that a user cannot authenticate using a credential that has been deleted from their account

## Final considerations

Note that the test cases provided on this page aren't all encompassing. There are many facets and permutations that can occur in your passkey application. Use this page as an initial starting point, and ensure that you are testing aspects in your implementation that are specific to your security policies and business requirements.
