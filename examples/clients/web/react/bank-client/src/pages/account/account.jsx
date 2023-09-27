import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";
import Stack from "react-bootstrap/Stack";

export default function Account() {
  return (
    <div className="account-balance-parent">
      <h2>Account Settings</h2>
      <div className="passkey-box-parent">
        <h3>Passkeys</h3>
        <div>
          <div className="passkey-box">
            <Row>
              <Col xs={12} lg={1}>
                <div id="svg-container">
                  <img
                    id="svg-1"
                    src="/img/icon-passkey.svg"
                    alt="Passkey icon"
                  />
                </div>
              </Col>
              <Col xs={12} lg={4} className="body-2 passkey-meta-info">
                <div>Passkey name</div>
                <div>Reg time: 5/15/2023, 1:00:00 pm</div>
                <div>Last used: 5/15/2023, 1:00:00 pm</div>
              </Col>
              <Col xs={12} lg={3} className="d-none d-lg-block"></Col>
              <Col xs={12} lg={3} className="passkey-button-box">
                <button className="button-outlined">Update</button>
                <button className="button-text">Delete</button>
              </Col>
            </Row>
          </div>
          <div className="passkey-box">
            <Row>
              <Col xs={12} lg={1}>
                <div id="svg-container">
                  <img
                    id="svg-1"
                    src="/img/icon-passkey.svg"
                    alt="Passkey icon"
                  />
                </div>
              </Col>
              <Col xs={12} lg={4} className="body-2 passkey-meta-info">
                <div>Passkey name</div>
                <div>Reg time: 5/15/2023, 1:00:00 pm</div>
                <div>Last used: 5/15/2023, 1:00:00 pm</div>
              </Col>
              <Col xs={12} lg={3} className="d-none d-lg-block"></Col>
              <Col xs={12} lg={3} className="passkey-button-box">
                <button className="button-outlined">Update</button>
                <button className="button-text">Delete</button>
              </Col>
            </Row>
          </div>
        </div>
        <div>
          <button className="button-primary" style={{ marginRight: "8px" }}>
            ADD A PASSKEY
          </button>
          <button className="button-text">DISABLE PASSKEYS</button>
        </div>
      </div>

      <div className="ap-box-parent">
        <h3>Advanced Protection</h3>
        <div>
          <div className="ap-box">
            <Row>
              <Col lg={10} xs={12}>
                <div className="ap-meta-info">
                  <div className="body-2">Enable advanced protection</div>
                  <div className="body-2 text-error">
                    Your account is not eligible for advanced protection.
                  </div>
                  <div className="body-2" style={{ paddingTop: "12px" }}>
                    Blurb about enabling advanced protection:
                    <ul>
                      <li>What it is?</li>
                      <li>Why it's useful</li>
                      <li>Conditions</li>
                    </ul>
                  </div>
                </div>
              </Col>
              <Col lg={2} xs={12}>
                <label class="switch">
                  <input type="checkbox" />
                  <span class="slider round"></span>
                </label>
              </Col>
            </Row>
          </div>
        </div>
      </div>
    </div>
  );
}
