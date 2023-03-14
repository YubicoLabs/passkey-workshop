const GLOBAL_OIDC_URI = "http://localhost:8081/realms/normal/protocol/openid-connect";
const GLOBAL_CLIENT_ID = "my_custom_client2";
const GLOBAL_REDIRECT_URI = "http://localhost:3000/oidc/callback";
const GLOBAL_LOGOUT_REDIRECT_URI = "http://localhost:3000/logout";

const OIDC_AUTH_Configs = {
  client_id: GLOBAL_CLIENT_ID,
  redirect_uri: GLOBAL_REDIRECT_URI,
  scope: "openid",
  response_type: "code",
};

const OIDC_LOGOUT_Configs = {
  client_id: GLOBAL_CLIENT_ID,
  post_logout_redirect_uri: GLOBAL_REDIRECT_URI,
};

const OIDC_TOKEN_Configs = {
  client_id: GLOBAL_CLIENT_ID,
  redirect_uri: GLOBAL_REDIRECT_URI,
  grant_type: "authorization_code",
};

const OIDC_TOKEN_REFRESH_Configs = {
  client_id: GLOBAL_CLIENT_ID,
  grant_type: "refresh_token",
  redirect_uri: "http://localhost:3000"
};

async function retrieveAccessToken(code) {
  try {
    var formBody = [];
    for (var property in OIDC_TOKEN_Configs) {
      var encodedKey = encodeURIComponent(property);
      var encodedValue = encodeURIComponent(OIDC_TOKEN_Configs[property]);
      formBody.push(encodedKey + "=" + encodedValue);
    }
    formBody.push("code=" + encodeURIComponent(code));
    formBody = formBody.join("&");

    const response = await fetch(
      "http://localhost:8081/realms/normal/protocol/openid-connect/token",
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
    for (var property in OIDC_TOKEN_Configs) {
      var encodedKey = encodeURIComponent(property);
      var encodedValue = encodeURIComponent(OIDC_TOKEN_REFRESH_Configs[property]);
      formBody.push(encodedKey + "=" + encodedValue);
    }
    formBody.push("refresh_token=" + encodeURIComponent(code));
    formBody = formBody.join("&");

    const response = await fetch(
      "http://localhost:8081/realms/normal/protocol/openid-connect/token",
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
      "http://localhost:8081/realms/normal/protocol/openid-connect/userinfo",
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
  GLOBAL_OIDC_URI,
  OIDC_LOGOUT_Configs,
  GLOBAL_CLIENT_ID,
  GLOBAL_REDIRECT_URI,
  GLOBAL_LOGOUT_REDIRECT_URI
};

export default OIDCServices;
