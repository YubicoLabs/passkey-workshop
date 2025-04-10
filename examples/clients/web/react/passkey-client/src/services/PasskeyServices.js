const PasskeyServices = {
  getAttestationOptions,
  sendAttestationResult,
  getAssertionOptions,
  sendAssertionResult,
  getCredentials,
  deleteCredential,
  updateCredential
}

const baseURL = process.env.REACT_APP_RP_API || "http://localhost:8080/v1";

async function getAttestationOptions(username) {
  try {
    const reqData = {
      userName: username,
      displayName: username,
      authenticatorSelection: {
        residentKey: "preferred",
        authenticatorAttachment: "",
        userVerification: "preferred"
      },
      attestation: "direct"
    };

    const requestOptions = {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(reqData)
    };

    const response = await fetch(`${baseURL}/attestation/options`, requestOptions);
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

    const response = await fetch(`${baseURL}/attestation/result`, requestOptions);
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

    const response = await fetch(`${baseURL}/assertion/options`, requestOptions);
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

    const response = await fetch(`${baseURL}/assertion/result`, requestOptions);
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

    const response = await fetch(`${baseURL}/user/credentials/${username}`, requestOptions);
    if(response.status !== 200) {
      throw Error("User not found")
    }
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

async function deleteCredential(credentialId) {
  try {
    const reqData = {
      id: credentialId
    };

    const requestOptions = {
      method: "DELETE",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(reqData)
    }

    console.warn(requestOptions)

    const response = await fetch(`${baseURL}/user/credentials`, requestOptions);
    const responseJSON = await response.json();

    return responseJSON;

  } catch (e) {
    console.error("There was an issue getting your credentials")
    console.error(e.message)
    throw e;
  }
}

async function updateCredential(credentialId, newNickname) {
  try {
    const reqData = {
      id: credentialId,
      nickName: newNickname
    };

    const requestOptions = {
      method: "PUT",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(reqData)
    }

    console.warn(requestOptions)

    const response = await fetch(`${baseURL}/user/credentials`, requestOptions);
    const responseJSON = await response.json();

    return responseJSON;

  } catch (e) {
    console.error("There was an issue getting your credentials")
    console.error(e.message)
    throw e;
  }
}

export default PasskeyServices;
