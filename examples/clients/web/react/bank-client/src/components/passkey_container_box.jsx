import { useState } from "react";

import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";

import Utils from "../services/Utils";
import PasskeyServices from "../services/PasskeyServices";

export default function PasskeyContainerBox({ credential, refreshCallback }) {
  const [updateNickname, setUpdateNickname] = useState(credential.nickName);

  const [isUpdate, setIsUpdate] = useState(false);

  const updateButton = async (e) => {
    /**
     * Not updating, initiate update
     */
    if (!isUpdate) {
      setIsUpdate(!isUpdate);
    } else {
      /**
       * Exit update, there is nothing to update
       */
      if (updateNickname === credential.nickName) {
        setIsUpdate(false);
      } else {
        /**
         * Call to the RP to make an update to the nickname
         */
        await PasskeyServices.updateCredential(credential.id, updateNickname);
        setIsUpdate(false);
        await refreshCallback();
      }
    }
  };

  const usernameOnChange = (e) => {
    setUpdateNickname(e.target.value);
  };

  const onEnterKeyUp = async (e) => {
    if (e.key === "Enter") {
      await updateButton();
    }
  };

  const deleteButton = async (e) => {
    try {
      e.preventDefault();

      const deleteResult = await PasskeyServices.deleteCredential(
        credential.id
      );

      if (deleteResult.result === "deleted") {
        /**
         * Need to add some logic to refresh the credential list (hook message)
         */
        await refreshCallback();
      } else {
        throw new Error("Error with deleting credential");
      }
    } catch (e) {}
  };

  return (
    <div className="passkey-box">
      <Row>
        <Col xs={12} lg={1}>
          <div id="svg-container">
            <img id="svg-1" src="/img/icon-passkey.svg" alt="Passkey icon" />
          </div>
        </Col>
        <Col xs={12} lg={4} className="body-2 passkey-meta-info">
          <div>
            {isUpdate ? (
              <input
                className="standard-input"
                type="text"
                placeholder="To"
                value={updateNickname}
                onChange={usernameOnChange}
                onKeyUp={onEnterKeyUp}
              />
            ) : (
              credential.nickName
            )}
          </div>
          <div>
            Registration time: {Utils.convertDate(credential.registrationTime)}
          </div>
          <div>Last used: {Utils.convertDate(credential.lastUsedTime)}</div>
        </Col>
        <Col xs={12} lg={3} className="d-none d-lg-block"></Col>
        <Col xs={12} lg={3} className="passkey-button-box">
          <button className="button-outlined" onClick={updateButton}>
            {isUpdate
              ? updateNickname === credential.nickName
                ? "Exit"
                : "Submit"
              : "Update"}
          </button>
          <button onClick={deleteButton} className="button-text">
            Delete
          </button>
        </Col>
      </Row>
    </div>
  );
}
