import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";
import Stack from "react-bootstrap/Stack";
import PasskeyContainer from "../../components/passkey_container";
import AdvancedProtectionContainer from "../../components/advanced_protection_container";
import AccountSettingsMenu from "../../components/account_settings_menu";

export default function Account() {
  return (
    <Row>
      <Col md={12} lg={2}>
        <AccountSettingsMenu />
      </Col>
      <Col md={12} lg={9}>
        <div className="account-balance-parent">
          <h2>Account Settings</h2>
          <PasskeyContainer />

          <AdvancedProtectionContainer />
        </div>
      </Col>
    </Row>
  );
}
