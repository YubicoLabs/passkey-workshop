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
const AUTH_URL = `${BASE_URL}/auth?client_id=${AUTH_SERVICES_CONSTANTS.client_id}&redirect_uri=${AUTH_SERVICES_CONSTANTS.redirect_uri}&scope=${AUTH_SERVICES_CONSTANTS.scope}&response_type=${AUTH_SERVICES_CONSTANTS.response_type}`;

//const LOGOUT_URL = `${BASE_URL}/logout?client_id=${AUTH_SERVICES_CONSTANTS.client_id}&post_logout_redirect_uri=${window.location.origin}&id_token_hint=${getLocalAccessTokens() ? getLocalAccessTokens().id_token : ""}`;

const random = new Uint8Array(32);

// convert from binary string to ArrayBuffer:
const encoder = new TextEncoder(); // always utf-8

function base64url_encode(buffer) {
  return btoa(Array.from(new Uint8Array(buffer), b => String.fromCharCode(b)).join(''))
      .replace(/\+/g, '-')
      .replace(/\//g, '_')
      .replace(/=+$/, '');
}

const getAccessToken = async(type, code) => {
  try {
    var formBody = [];
    if(type === "AUTH") {
      if( code === null) throw new Error("OAuth2 code flow error - missing authz code");
      const code_verifier = window.localStorage.getItem("CODE_VERIFIER");
      if( code_verifier === null ) throw new Error("PKCE error retrieving code verifier")
      window.localStorage.removeItem("CODE_VERIFIER");
      formBody.push("grant_type=" + encodeURIComponent(AUTH_SERVICES_CONSTANTS.grant_type_auth));
      formBody.push("code=" + encodeURIComponent(code));
      formBody.push("code_verifier=" + encodeURIComponent(code_verifier));
    } else if(type === "REFRESH") {
      if( code === null) throw new Error("OAuth2 refresh flow error - missing refresh token"); // note that code contains a refresh token in this case
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

    const responseJSON = await response.json();
    if( responseJSON.error ) {
      console.log(`token endpoint returned an error: ${responseJSON.error} (${responseJSON.error_description})`);
      return false;
    }

    window.localStorage.setItem(
      "APP_ACCESS_TOKENS",
      JSON.stringify(responseJSON)
    );

    return true;
  } catch(e) {
    console.error("Could not retrieve access token");
    console.error(e);
    window.location = await getAuthUri(); // go ask the user to authorize new tokens
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
  return response.sub;
}

function getLocalUsername() {
  const lsString = window.localStorage.getItem("USER_INFO");
  const response = JSON.parse(lsString);
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
      if(!refreshSuccess) {
        throw new Error("Refresh token could not be used");
      }
    }
    return true;
  } catch(e) {
    console.error("The user is no longer authenticated");
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

const getAuthUri = async (state = 'standard') => {
  crypto.getRandomValues(random);
  const code_verifier = base64url_encode(random);
  window.localStorage.setItem("CODE_VERIFIER", code_verifier);
  const digest = await crypto.subtle.digest("SHA-256", encoder.encode(code_verifier));
  const code_challenge = base64url_encode(digest);
  return `${AUTH_URL}&state=${state}&code_challenge=${code_challenge}&code_challenge_method=S256`;
}

const getLogoutUri = () => {
  const id_token_hint = getLocalAccessTokens().id_token;

  const LOGOUT_URL = `${BASE_URL}/logout?client_id=${AUTH_SERVICES_CONSTANTS.client_id}&post_logout_redirect_uri=${window.location.origin}&id_token_hint=${id_token_hint}`;
  return LOGOUT_URL

}

const AuthServices = {
  AUTH_SERVICES_CONSTANTS,
  getAccessToken,
  stillAuthenticated,
  getAuthUri,
  getLogoutUri,
  getLocalAccessTokens,
  getLocalUserHandle,
  getLocalUsername
}

export default AuthServices;
