import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";
import PasskeyContainerBox from "./passkey_container_box";

export default function PasskeyContainer() {
  return (
    <div className="passkey-box-parent">
      <h3>Passkeys</h3>
      <div>
        <PasskeyContainerBox />
        <PasskeyContainerBox />
        <PasskeyContainerBox />
      </div>
      <div>
        <button className="button-primary" style={{ marginRight: "8px" }}>
          ADD A PASSKEY
        </button>
        <button className="button-text">DISABLE PASSKEYS</button>
      </div>
    </div>
  );
}
