import AuthServices from "../services/AuthServices";

export default function LogoutContainer() {
  const logoutButton = () => {
    const logout_uri = AuthServices.getLogoutUri();
    localStorage.removeItem("APP_ACCESS_TOKENS");
    localStorage.removeItem("USER_INFO");
    window.location.replace(logout_uri);
  };

  return (
    <div className="ap-box-parent">
      <h3>Logout</h3>
      <div>
        <button onClick={logoutButton} className="button-outlined">
          Logout
        </button>
      </div>
    </div>
  );
}
