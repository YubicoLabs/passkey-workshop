---
sidebar_position: 2
---

# API client

To begin our client implementation we will create an interface that will connect to our [relying party API](http://localhost:3002/docs/relying-party/api-def). This interface will allow our React application to make calls to our relying party API, without having to create duplicate code.

The methods, and their associated flows will be covered in detail in the following pages, so for now don't get caught up in the details.

## Base URL

If you are following along with this guide, the URL for the API is as follows

```javascript
const baseURl = "http://localhost:8080/v1";
```

This URL is hardcoded, so ensure that you utilize something like an environment variable for your production deployment.

## Registration methods

The following methods will be used for the registration (creation), of new passkeys. For the remainder of this section, any use of attestation will refer to registration.

### Get attestation options

This method will allow you to get the attestation options from your relying party.

More details on these properties can be found on the [relying party registration flow page](/docs/relying-party/reg-flow#request).

```javascript
async function getAttestationOptions(
  username,
  residentKeyReq,
  authAttachment,
  uvReq,
  attestation
) {
  try {
    const reqData = {
      userName: username,
      displayName: username,
      authenticatorSelection: {
        residentKey: residentKeyReq,
        authenticatorAttachment: authAttachment,
        userVerification: uvReq,
      },
      attestation: attestation,
    };

    const requestOptions = {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(reqData),
    };

    const response = await fetch(
      `${baseURl}/attestation/options`,
      requestOptions
    );
    const responseJSON = await response.json();

    console.info("Printing registration options");
    console.info(responseJSON);

    return responseJSON;
  } catch (e) {
    console.error("There was an issue getting your registration options");
    console.error(e.message);
    throw e;
  }
}
```

### Send attestation result

This method will allow you to send the newly created passkey to be registered in your relying party.

More details on these properties can be found on the [relying party registration flow page](/docs/relying-party/reg-flow#request-1).

```javascript
async function sendAttestationResult(requestID, makeCredentialResponse) {
  try {
    const reqData = {
      requestId: requestID,
      makeCredentialResult: makeCredentialResponse,
    };

    const requestOptions = {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(reqData),
    };

    const response = await fetch(
      `${baseURl}/attestation/result`,
      requestOptions
    );
    const responseJSON = await response.json();

    console.info("Printing registration result");
    console.info(responseJSON);

    return responseJSON;
  } catch (e) {
    console.error("There was an issue sending your registration response");
    console.error(e.message);
    throw e;
  }
}
```

## Authentication methods

The following methods will be used for authenticating with a passkey. For the remainder of this section, any use of assertion will refer to authentication.

### Get assertion options

This method will allow you to get the assertion options from your relying party.

More details on these properties can be found on the [relying party authentication flow page](/docs/relying-party/auth-flow#request).

```javascript
async function getAssertionOptions(username) {
  try {
    const reqData = {
      userName: username,
    };

    const requestOptions = {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(reqData),
    };

    const response = await fetch(
      `${baseURl}/assertion/options`,
      requestOptions
    );
    const responseJSON = await response.json();

    console.info("Printing authentication options");
    console.info(responseJSON);

    return responseJSON;
  } catch (e) {
    console.error("There was an issue getting your authentication options");
    console.error(e.message);
    throw e;
  }
}
```

### Send assertion result

This method will allow you to send a signed challenge to the relying party to validate if the user should be authenticated.

More details on these properties can be found on the [relying party authentication flow page](/docs/relying-party/auth-flow#request-1).

```javascript
async function sendAssertionResult(requestID, assertionResult) {
  try {
    const reqData = {
      requestId: requestID,
      assertionResult: assertionResult,
    };

    const requestOptions = {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(reqData),
    };

    const response = await fetch(`${baseURl}/assertion/result`, requestOptions);
    const responseJSON = await response.json();

    console.info("Printing authentication result");
    console.info(responseJSON);

    return responseJSON;
  } catch (e) {
    console.error("There was an issue sending your authentication response");
    console.error(e.message);
    throw e;
  }
}
```

## Credential management APIs

The following methods will be used to allow the user to manage their credentials. These methods will allow a user to view and manage passkeys through the lifecycle of their account. The methods included below will list, update, and delete passkeys from an account.

### Get all passkeys

This method will allow you to list all of the passkeys that belong to a specific user

```javascript
async function getCredentials(username) {
  try {
    const requestOptions = {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
      },
    };

    const response = await fetch(
      `${baseURl}/user/credentials/${username}`,
      requestOptions
    );
    const responseJSON = await response.json();

    console.info(`Printing credentials list for ${username}`);
    console.info(responseJSON);

    return responseJSON;
  } catch (e) {
    console.error("There was an issue getting your credentials");
    console.error(e.message);
    throw e;
  }
}
```

### Delete passkey

This method will allow you to delete a specific passkey from your account

```javascript
async function deleteCredential(credentialId) {
  try {
    const reqData = {
      id: credentialId,
    };

    const requestOptions = {
      method: "DELETE",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(reqData),
    };

    const response = await fetch(`${baseURl}/user/credentials`, requestOptions);
    const responseJSON = await response.json();

    return responseJSON;
  } catch (e) {
    console.error("There was an issue getting your credentials");
    console.error(e.message);
    throw e;
  }
}
```

### Rename passkey

This method will allow you to edit a passkeys username, that can be used to help a user more easily identify passkeys in their account.

Note that this does not edit the passkey itself, only the metadata that is associated to it.

```javascript
async function updateCredential(credentialId, newNickname) {
  try {
    const reqData = {
      id: credentialId,
      nickName: newNickname,
    };

    const requestOptions = {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(reqData),
    };

    const response = await fetch(`${baseURl}/user/credentials`, requestOptions);
    const responseJSON = await response.json();

    return responseJSON;
  } catch (e) {
    console.error("There was an issue getting your credentials");
    console.error(e.message);
    throw e;
  }
}
```
