import Button from "react-bootstrap/esm/Button";
import OIDCServices from "../services/OIDCServices";

export default function LogoutButton() {
  const submitLogout = async (e) => {
    const id_token_hint = await OIDCServices.getLocalAccessTokens().id_token;

    const logoutUrl =
      OIDCServices.GLOBAL_OIDC_URI +
      "/logout" +
      `?client_id=${OIDCServices.GLOBAL_CLIENT_ID}&post_logout_redirect_uri=${OIDCServices.GLOBAL_LOGOUT_REDIRECT_URI}&id_token_hint=${id_token_hint}`;

    localStorage.removeItem("APP_ACCESS_TOKENS");

    window.location.replace(logoutUrl);
  };

  return <Button onClick={submitLogout}>Logout</Button>;
}
