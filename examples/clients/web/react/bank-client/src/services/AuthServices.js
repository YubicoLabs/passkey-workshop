const baseUrl_env = process.env.REACT_APP_KEYCLOAK_URL || "http://localhost:8081";
const client_id_env = process.env.REACT_APP_CLIENT_ID || "BankApp";

const AUTH_SERVICES_CONSTANTS = {
  baseUrl: baseUrl_env,
  client_id: client_id_env,
  scope: "openid",
  response_type: "code",
  grant_type_auth: "authorization_code",
  grant_type_refresh: "refresh_token",
  redirect_uri: `${window.location.origin}/callback/auth`,
  logout_redirect_uri: `${window.location.origin}/logout`
}

const BASE_URL = `${AUTH_SERVICES_CONSTANTS.baseUrl}/realms/${AUTH_SERVICES_CONSTANTS.client_id}/protocol/openid-connect`
const AUTH_URL = `${BASE_URL}/auth?client_id=${AUTH_SERVICES_CONSTANTS.client_id}&redirect_uri=${AUTH_SERVICES_CONSTANTS.redirect_uri}&scope=${AUTH_SERVICES_CONSTANTS.scope}&response_type=${AUTH_SERVICES_CONSTANTS.response_type}&state=standard`;
const STEPUP_AUTH_URL = `${BASE_URL}/auth?client_id=${AUTH_SERVICES_CONSTANTS.client_id}&redirect_uri=${AUTH_SERVICES_CONSTANTS.redirect_uri}&scope=${AUTH_SERVICES_CONSTANTS.scope}&response_type=${AUTH_SERVICES_CONSTANTS.response_type}&state=stepup`;

//const LOGOUT_URL = `${BASE_URL}/logout?client_id=${AUTH_SERVICES_CONSTANTS.client_id}&post_logout_redirect_uri=${window.location.origin}&id_token_hint=${getLocalAccessTokens() ? getLocalAccessTokens().id_token : ""}`;


const getAccessToken = async(type, code) => {
  try {
    var formBody = [];
    if(type === "AUTH") {
      formBody.push("grant_type=" + encodeURIComponent(AUTH_SERVICES_CONSTANTS.grant_type_auth));
      formBody.push("code=" + encodeURIComponent(code));


    } else if(type === "REFRESH") {
      formBody.push("grant_type=" + encodeURIComponent(AUTH_SERVICES_CONSTANTS.grant_type_refresh));
      formBody.push("refresh_token=" + encodeURIComponent(code));
    } else {
      throw new Error("Invalid grant type");
    }
    formBody.push("client_id=" + encodeURIComponent(`${AUTH_SERVICES_CONSTANTS.client_id}`));
    formBody.push("redirect_uri=" + encodeURIComponent(`${AUTH_SERVICES_CONSTANTS.redirect_uri}`));
    formBody = formBody.join("&");

    const response = await fetch( `${BASE_URL}/token`,
      {
        method: "POST",
        headers: {
          "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8",
        },
        body: formBody,
      }
    );

    if(response.status !== 200) {
      window.localStorage.removeItem("USER_INFO");
      throw new Error("Access token not found");
    }

    const responseJSON = await response.json();

    window.localStorage.setItem(
      "APP_ACCESS_TOKENS",
      JSON.stringify(responseJSON)
    );

    return true;
  } catch(e) {
    console.error("Could not retrieve access token");
    console.error(e);
    window.location = AuthServices.AUTH_URL;
  }
}

function getLocalAccessTokens() {
  const lsString = window.localStorage.getItem("APP_ACCESS_TOKENS");
  const response = JSON.parse(lsString);
  return response;
}

function getLocalUserHandle() {
  const lsString = window.localStorage.getItem("USER_INFO");
  const response = JSON.parse(lsString);
  //console.log(response);
  return response.sub;
}

function getLocalUsername() {
  const lsString = window.localStorage.getItem("USER_INFO");
  const response = JSON.parse(lsString);
  //console.log(response);
  return response.preferred_username;
}

const stillAuthenticated = async () => {
  try {
    const accessToken = getLocalAccessTokens();

    // Access token does not exist
    if(accessToken === undefined) {
      return false
    } 
    
    const accessCodeStillValid = await testAccessToken(accessToken.access_token);
  
    if(!accessCodeStillValid) {
      const refreshSuccess = await getAccessToken("REFRESH", accessToken.refresh_token);
      await testAccessToken(refreshSuccess.access_token)
      console.log("Refresh success: " + refreshSuccess);
      if(!refreshSuccess) {
        throw new Error("Refresh token could not be used");
      }
    }
    return true;
  } catch(e) {
    console.error("The user is no longer authentication");
    return false;
  }
}

const testAccessToken = async (code) => {
  try {
    const response = await fetch(
      `${BASE_URL}/userinfo`,
      {
        method: "GET",
        headers: {
          Authorization: `Bearer ${code}`
        }
      }
    )
    if(response.status !== 200) {
      window.localStorage.removeItem("USER_INFO");
      throw new Error("This access token is no longer valid");
    }
    const responseJSON = await response.json();
    
    window.localStorage.setItem(
      "USER_INFO",
      JSON.stringify(responseJSON)
    );
    return true;
  } catch(e) {
    console.error("This access token is no longer usable");
    return false;
  }
}

const getLogoutUri = () => {
  const id_token_hint = getLocalAccessTokens().id_token;

  const LOGOUT_URL = `${BASE_URL}/logout?client_id=${AUTH_SERVICES_CONSTANTS.client_id}&post_logout_redirect_uri=${window.location.origin}&id_token_hint=${id_token_hint}`;
  return LOGOUT_URL

}

const AuthServices = {
  AUTH_SERVICES_CONSTANTS,
  AUTH_URL,
  STEPUP_AUTH_URL,
  getAccessToken,
  stillAuthenticated,
  getLogoutUri,
  getLocalAccessTokens,
  getLocalUserHandle,
  getLocalUsername
}

export default AuthServices;
