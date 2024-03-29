import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";

import HomeNav from "../../components/home_nav";
import AccountBalance from "../../components/account_balance";
import Utils from "../../services/Utils";
import AuthServices from "../../services/AuthServices";

export default function Home() {
  const cardName = Utils.cardName(AuthServices.getLocalUserHandle());
  return (
    <Row>
      <Col md={12} lg={5}>
        <Row className="card-center">
          <div className="hero action_parent">
            <div className="action">
              <Row>
                <div className="text-1">
                  <img
                    alt=""
                    src="/img/logo.png"
                    width="24"
                    height="24"
                    className="d-inline-block align-top"
                  />{" "}
                  Passkey Banking Solution
                </div>
              </Row>
              <Row className="card-gap-1">
                <Col xs={6}>
                  <span className="caption-default">Name</span>
                  <span className="card-text-name">{cardName}</span>
                </Col>
                <Col xs={6}>
                  <span className="caption-default">Number</span>
                  <span className="card-text-name">XXXX XXXX XXXX 7817</span>
                </Col>
              </Row>
              <Row className="card-gap-2">
                <Col className="card-logo">
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    width="61"
                    height="38"
                    viewBox="0 0 61 38"
                    fill="none">
                    <circle
                      opacity="0.6"
                      cx="19"
                      cy="19"
                      r="19"
                      fill="#D80001"
                    />
                    <circle
                      opacity="0.6"
                      cx="42"
                      cy="19"
                      r="19"
                      fill="#FFB800"
                    />
                  </svg>
                </Col>
              </Row>
            </div>
          </div>
        </Row>
        <HomeNav />
      </Col>
      <Col md={12} lg={7}>
        <AccountBalance />
      </Col>
    </Row>
  );
}
