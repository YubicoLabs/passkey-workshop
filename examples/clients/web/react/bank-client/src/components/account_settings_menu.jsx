import { Link } from "react-router-dom";

export default function AccountSettingsMenu() {
  return (
    <div>
      <div>
        <h3>Settings</h3>
      </div>
      <div style={{ marginTop: "24px" }}>
        <Link className="nav-link-account" to="/account">
          Security
        </Link>
      </div>
    </div>
  );
}
