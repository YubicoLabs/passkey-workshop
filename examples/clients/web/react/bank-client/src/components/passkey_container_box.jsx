import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";

export default function PasskeyContainerBox() {
  return (
    <div className="passkey-box">
      <Row>
        <Col xs={12} lg={1}>
          <div id="svg-container">
            <img id="svg-1" src="/img/icon-passkey.svg" alt="Passkey icon" />
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
  );
}
