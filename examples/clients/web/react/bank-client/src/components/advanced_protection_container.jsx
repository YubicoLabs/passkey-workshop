import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";

export default function AdvancedProtectionContainer() {
  return (
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
              <label className="switch">
                <input type="checkbox" />
                <span className="slider round"></span>
              </label>
            </Col>
          </Row>
        </div>
      </div>
    </div>
  );
}
