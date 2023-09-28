import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";
import PasskeyContainer from "../../components/passkey_container";
import AdvancedProtectionContainer from "../../components/advanced_protection_container";
import PageSideNav from "../../components/page-side-nav";

export default function Transactions() {
  const pageSideNavProps = [
    { name: "DEPOSIT", link: "/transactions/deposit" },
    { name: "TRANSFER", link: "/transactions/transfer" },
  ];

  return (
    <Row>
      <Col md={12} lg={2}>
        <PageSideNav title="Payments" linkObjects={pageSideNavProps} />
      </Col>
      <Col md={12} lg={9}>
        <div className="transaction-parent">
          <div>
            <h3>Account name (6666)</h3>
          </div>
          <div>
            <h2>Account balance</h2>
            <span className="title">$6,666</span>
          </div>
          <div style={{ maringTop: "80px" }}>
            <div>
              <input
                className="standard-input"
                type="text"
                placeholder="Amount"
              />
            </div>
            <div style={{ marginTop: "16px" }}>
              <input className="standard-input" type="text" placeholder="To" />
            </div>
          </div>
          <div style={{ marginTop: "16px" }}>
            <button className="button-primary">TRANSFER</button>
            <button className="button-text">CANCEL</button>
          </div>
        </div>
      </Col>
    </Row>
  );
}
