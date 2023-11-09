import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";
import PasskeyContainer from "../../components/passkey_container";
import AdvancedProtectionContainer from "../../components/advanced_protection_container";
import PageSideNav from "../../components/page-side-nav";
import LogoutContainer from "../../components/logout_container";

export default function Account() {
  const pageSideNavProps = [{ name: "SECURITY", link: "/account" }];

  return (
    <Row>
      <Col md={12} lg={2} style={{ paddingBottom: "2em" }}>
        <PageSideNav title="Settings" linkObjects={pageSideNavProps} />
      </Col>
      <Col md={12} lg={9}>
        <div className="account-balance-parent">
          <h2>Account Settings</h2>
          <PasskeyContainer />
          <AdvancedProtectionContainer />
          <LogoutContainer />
        </div>
      </Col>
    </Row>
  );
}
