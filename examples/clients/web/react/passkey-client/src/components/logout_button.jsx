import Button from "react-bootstrap/esm/Button";
import OIDCServices from "../services/OIDCServices";

export default function LogoutButton() {
  const submitLogout = async (e) => {
    const id_token_hint = await OIDCServices.getLocalAccessTokens().id_token;

    const logoutUrl = `${OIDCServices.GLOBAL_OIDC_CONFIGS.baseUri}/logout?client_id=${OIDCServices.GLOBAL_OIDC_CONFIGS.client}&post_logout_redirect_uri=${OIDCServices.GLOBAL_OIDC_CONFIGS.logout_redirect_uri}&id_token_hint=${id_token_hint}`;

    localStorage.removeItem("APP_ACCESS_TOKENS");

    window.location.replace(logoutUrl);
  };

  return (
    <Button
      style={{ width: "100%" }}
      onClick={submitLogout}
      variant="secondary">
      Click to sign out of your account
    </Button>
  );
}
