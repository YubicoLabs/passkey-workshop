
const GLOBAL_OIDC_CONFIGS = {
  baseUri: process.env.REACT_APP_OIDC || "http://localhost:8081/realms/passkeyDemo/protocol/openid-connect",


  client: "passkeyClient",
  redirect_uri: `${window.location.origin}/oidc/callback`,
  logout_redirect_uri: `${window.location.origin}/logout`
}

const OIDC_AUTH_CONFIGS = {
  client_id: GLOBAL_OIDC_CONFIGS.client,
  redirect_uri: GLOBAL_OIDC_CONFIGS.redirect_uri,
  scope: "openid",
  response_type: "code"
}

const OIDC_TOKEN_CONFIG = {
  client_id: GLOBAL_OIDC_CONFIGS.client,
  redirect_uri: GLOBAL_OIDC_CONFIGS.redirect_uri,
  grant_type: "authorization_code",
};

const OIDC_TOKEN_REFRESH_CONFIG = {
  client_id: GLOBAL_OIDC_CONFIGS.client,
  grant_type: "refresh_token",
  redirect_uri: `${window.location.origin}`
};

async function retrieveAccessToken(code) {
  try {
    var formBody = [];
    for (var property in OIDC_TOKEN_CONFIG) {
      var encodedKey = encodeURIComponent(property);
      var encodedValue = encodeURIComponent(OIDC_TOKEN_CONFIG[property]);
      formBody.push(encodedKey + "=" + encodedValue);
    }
    formBody.push("code=" + encodeURIComponent(code));
    formBody = formBody.join("&");

    const response = await fetch( `${GLOBAL_OIDC_CONFIGS.baseUri}/token`,
      {
        method: "POST",
        headers: {
          "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8",
        },
        body: formBody,
      }
    );

    const responseJSON = await response.json();

    window.localStorage.setItem(
      "APP_ACCESS_TOKENS",
      JSON.stringify(responseJSON)
    );

    return true;
  } catch (e) {
    return false;
  }
}

function getLocalAccessTokens() {
  const lsString = window.localStorage.getItem("APP_ACCESS_TOKENS");
  const response = JSON.parse(lsString);
  return response;
}

function getLocalUserInfo() {
  const lsString = window.localStorage.getItem("USER_INFO");
  const response = JSON.parse(lsString);
  return response;
}

async function refreshToken(code) {
  try {
    var formBody = [];
    for (var property in OIDC_TOKEN_CONFIG) {
      var encodedKey = encodeURIComponent(property);
      var encodedValue = encodeURIComponent(OIDC_TOKEN_REFRESH_CONFIG[property]);
      formBody.push(encodedKey + "=" + encodedValue);
    }
    formBody.push("refresh_token=" + encodeURIComponent(code));
    formBody = formBody.join("&");

    const response = await fetch(
      `${GLOBAL_OIDC_CONFIGS.baseUri}/token`,
      {
        method: "POST",
        headers: {
          "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8",
        },
        body: formBody,
      }
    );

    if(response.status === 200) {
      const responseJSON = await response.json();

      window.localStorage.setItem(
        "APP_ACCESS_TOKENS",
        JSON.stringify(responseJSON)
      );
  
      return true;
    } else {
      throw new Error("Token not found");
    }

  } catch (e) {
    return false;
  }
}

/**
 * Method that will determine if the user needs to be reauthenticated
 * The first step is to test the current access token
 * If that fails, check the refresh token
 * If the refresh token fails, then return false
 * returns true if the user still has access, false otherwise
 */
async function stillAuthenticated() {
  try {
  // Check if the access token works
  const accessToken = getLocalAccessTokens();

  // Access token does not exist
  if(accessToken === undefined) {
    return false
  } 
  
  const accessCodeStillValid = await testAccessToken(accessToken.access_token);

  if(accessCodeStillValid) {
    return true
  } else {
    // Attempt to utilize the refresh token
    const refreshSuccess = await refreshToken(accessToken.refresh_token);
    if(!refreshSuccess) {
      return false;
    } 

    const accessTokenWithRefrsh = getLocalAccessTokens();

    const newAccessCodeValid = await testAccessToken(accessTokenWithRefrsh.access_token);

    if(newAccessCodeValid) {
      return true;
    } else {
      return false
    }
  }
  } catch (e) {
    return false;
  }

}

async function testAccessToken(code) {
  try {
    const response = await fetch(
      `${GLOBAL_OIDC_CONFIGS.baseUri}/userinfo`,
      {
        method: "GET",
        headers: {
          Authorization: `Bearer ${code}`,
        },
      }
    );

    if(response.status !== 200) {
      window.localStorage.removeItem("USER_INFO");
      return false
    }
    const responseJSON = await response.json();

    window.localStorage.setItem(
      "USER_INFO",
      JSON.stringify(responseJSON)
    );
    return true;
  } catch(e) {
    return false
  }
}

const OIDCServices = {
  retrieveAccessToken,
  getLocalAccessTokens,
  stillAuthenticated,
  getLocalUserInfo,
  GLOBAL_OIDC_CONFIGS,
  OIDC_AUTH_CONFIGS,
};

export default OIDCServices;
