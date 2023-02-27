const PasskeyServices = {
  getAttestationOptions,
  sendAttestationResult,
  getAssertionOptions,
  sendAssertionResult,
  getCredentials
}

const baseURl = "http://localhost:8080/v1"

async function getAttestationOptions(username, residentKeyReq, authAttachment, uvReq, attestation) {
  try {
    const reqData = {
      userName: username,
      displayName: username,
      authenticatorSelection: {
        residentKey: residentKeyReq,
        authenticatorAttachment: authAttachment,
        userVerification: uvReq
      },
      attestation: attestation
    };

    const requestOptions = {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(reqData)
    };

    const response = await fetch(`${baseURl}/attestation/options`, requestOptions);
    const responseJSON = await response.json();

    console.info("Printing registration options");
    console.info(responseJSON);

    return responseJSON;
  } catch (e) {
    console.error("There was an issue getting your registration options")
    console.error(e.message)
    throw e;
  }
}

async function sendAttestationResult(requestID, makeCredentialResponse) {
  try {
    const reqData = {
      requestId: requestID,
      makeCredentialResult: makeCredentialResponse
    };

    const requestOptions = {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(reqData)
    }

    const response = await fetch(`${baseURl}/attestation/result`, requestOptions);
    const responseJSON = await response.json();

    console.info("Printing registration result");
    console.info(responseJSON);

    return responseJSON;

  } catch (e) {
    console.error("There was an issue sending your registration response")
    console.error(e.message)
    throw e;
  }
}

async function getAssertionOptions(username) {
  try {
    const reqData = {
      userName: username
    };

    const requestOptions = {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(reqData)
    }

    const response = await fetch(`${baseURl}/assertion/options`, requestOptions);
    const responseJSON = await response.json();

    console.info("Printing authentication options");
    console.info(responseJSON);

    return responseJSON;

  } catch (e) {
    console.error("There was an issue getting your authentication options")
    console.error(e.message)
    throw e;
  }
}

async function sendAssertionResult(requestID, assertionResult) {
  try {
    const reqData = {
      requestId: requestID,
      assertionResult: assertionResult
    };

    const requestOptions = {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(reqData)
    }

    const response = await fetch(`${baseURl}/assertion/result`, requestOptions);
    const responseJSON = await response.json();

    console.info("Printing authentication result");
    console.info(responseJSON);

    return responseJSON;

  } catch (e) {
    console.error("There was an issue sending your authentication response")
    console.error(e.message)
    throw e;
  }
}

async function getCredentials(username) {
  try {

    const requestOptions = {
      method: "GET",
      headers: {
        "Content-Type": "application/json"
      }
    }

    const response = await fetch(`${baseURl}/user/credentials/${username}`, requestOptions);
    const responseJSON = await response.json();

    console.info(`Printing credentials list for ${username}`);
    console.info(responseJSON);

    return responseJSON;

  } catch (e) {
    console.error("There was an issue getting your credentials")
    console.error(e.message)
    throw e;
  }
}

export default PasskeyServices;