import { create } from "@github/webauthn-json";

import PasskeyContainerBox from "./passkey_container_box";
import PasskeyServices from "../services/PasskeyServices";

export default function PasskeyContainer({
  username,
  passkeyList,
  refreshCredentialListCallback,
}) {
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

      console.log(credentialCreateResult.status);

      if (credentialCreateResult.status === "created") {
        await refreshCredentialListCallback();
      } else {
        throw new Error("Error with creating result");
      }
    } catch (e) {}
  };

  return (
    <div className="passkey-box-parent">
      <h3>Passkeys</h3>
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
      <div>
        <button
          onClick={addNewPasskey}
          className="button-primary"
          style={{ marginRight: "8px" }}>
          ADD A PASSKEY
        </button>

        {/*
        Consider removing this, unsure what the thought behind this button is
        <button className="button-text">DISABLE PASSKEYS</button>*/}
      </div>
    </div>
  );
}
