import { useEffect, useState } from "react";
import { create } from "@github/webauthn-json";

import PasskeyContainerBox from "./passkey_container_box";
import PasskeyServices from "../services/PasskeyServices";

export default function PasskeyContainer() {
  const [credentialList, setCredentialList] = useState([]);
  const [username, setUsername] = useState("");

  /**
   * On page load, get the user's credentials
   */
  useEffect(() => {
    const initCredentials = async () => {
      try {
        if (username !== "") {
          await refreshCredentialList();
        }
      } catch (e) {
        console.error("Something went wrong");
        setCredentialList([]);
      }
    };
    initCredentials();
  }, [username]);

  useEffect(() => {
    const lsString = window.localStorage.getItem("USER_INFO");
    const response = JSON.parse(lsString);
    const username = response.preferred_username;

    setUsername(username);
  }, []);

  /**
   * Call this method to refresh the current credential list
   * Based on the user's username
   */
  const refreshCredentialList = async () => {
    try {
      const credList = await PasskeyServices.getCredentials(username);
      console.log(credList);
      setCredentialList(credList.credentials);
    } catch (e) {
      console.error("Could not receive credential list");
      setCredentialList([]);
    }
  };

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
        await refreshCredentialList();
      } else {
        throw new Error("Error with creating result");
      }
    } catch (e) {}
  };

  return (
    <div className="passkey-box-parent">
      <h3>Passkeys</h3>
      <div>
        {credentialList.map((value, i) => (
          <div key={i}>
            <PasskeyContainerBox
              credential={value}
              refreshCallback={refreshCredentialList}
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
