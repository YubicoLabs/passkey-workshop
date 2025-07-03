import { create } from "@github/webauthn-json";

import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";

import PasskeyContainerBox from "./passkey_container_box";
import PasskeyServices from "../services/PasskeyServices";
import { useState } from "react";
import { useEffect } from "react";

export default function PasskeyContainer({
  username,
  passkeyList,
  refreshCredentialListCallback,
}) {
  const [showBanner, setShowBanner] = useState(false);
  const [bannerMessage, setBannerMessage] = useState("");

  const addNewPasskey = async (e) => {
    try {
      e.preventDefault();

      const attestationOptions = await PasskeyServices.getAttestationOptions(
        username
      );

      const makeCredentialResult = await create(attestationOptions);

      const credentialCreateResult =
        await PasskeyServices.sendAttestationResult(
          attestationOptions.requestId,
          makeCredentialResult
        );

      if (credentialCreateResult.status === "created") {
        await refreshCredentialListCallback();
      } else {
        throw new Error("Error with creating result");
      }
    } catch (e) {}
  };

  const determineBannerMessage = () => {
    /**
     * Conditions:
     *
     * 1. No high assurance passkey
     * 2. Only one passkey
     */
    console.log("in method len: " + passkeyList.length);
    if (passkeyList.length < 2) {
      setShowBanner(true);
      setBannerMessage(
        "Great, your account is secured with a passkey. If you lose your authenticator, you may be unable to access your account. To ensure recovery, please add a secondary passkey."
      );
      return;
    }

    for (var i = 0; i < passkeyList.length; i++) {
      if (passkeyList[i].isHighAssurance) {
        setShowBanner(false);
        setBannerMessage();
        return;
      }
    }

    setShowBanner(true);
    setBannerMessage(
      "Great, your passkey is backed up! You can recover it if lost. For higher security, consider using a device-bound passkey."
    );
  };

  useEffect(() => {
    console.log("Length: " + passkeyList.length);
    determineBannerMessage();
  }, [passkeyList]);

  return (
    <div className="passkey-box-parent">
      <h3>Passkeys</h3>
      {showBanner && (
        <div>
          <div className="passkey-info">
            <Row>
              <Col xs={12} lg={1}>
                <div id="svg-container">
                  <img id="info-box" src="/img/info-icon.svg" alt="Info icon" />
                </div>
              </Col>
              <Col xs={12} lg={9} className="body-2 passkey-meta-info">
                {bannerMessage}
              </Col>
            </Row>
          </div>
        </div>
      )}
      <div>
        <button
          onClick={addNewPasskey}
          className="button-primary"
          style={{
            marginRight: "8px",
            paddingTop: "1em",
            paddingBottom: "1em",
          }}>
          <img id="svg-1" src="/img/icon-passkey.svg" alt="Passkey icon" />
          Create a passkey
        </button>
      </div>
      <div>
        {passkeyList.map((value, i) => (
          <div key={i}>
            <PasskeyContainerBox
              credential={value}
              refreshCallback={refreshCredentialListCallback}
            />
          </div>
        ))}
      </div>
    </div>
  );
}
