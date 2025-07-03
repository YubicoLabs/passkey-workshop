import { useState } from "react";

import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";
import Modal from "react-bootstrap/Modal";

import Utils from "../services/Utils";
import PasskeyServices from "../services/PasskeyServices";

export default function PasskeyContainerBox({ credential, refreshCallback }) {
  const [updateNickname, setUpdateNickname] = useState(credential.nickName);

  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [showEditModal, setShowEditModal] = useState(false);

  const updateButton = async (e) => {
    e.preventDefault();

    if (updateNickname !== credential.nickName) {
      /**
       * Call to the RP to make an update to the nickname
       */
      try {
        await PasskeyServices.updateCredential(credential.id, updateNickname);

        setShowEditModal(false);

        await refreshCallback();
      } catch (e) {
        console.error(
          "There was an issue attempting to edit the credential nickname"
        );
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

      setShowDeleteModal(false);

      if (deleteResult.result === "deleted") {
        await refreshCallback();
      } else {
        throw new Error("Error with deleting credential");
      }
    } catch (e) {}
  };

  const modalOpenDelete = (e) => {
    e.preventDefault();

    setShowDeleteModal(true);
  };

  const modalCloseDelete = (e) => {
    e.preventDefault();

    setShowDeleteModal(false);
  };

  const modalOpenEdit = (e) => {
    e.preventDefault();

    setShowEditModal(true);
  };

  const modalCloseEdit = (e) => {
    e.preventDefault();

    setShowEditModal(false);
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
          <div>{credential.nickName}</div>
          <div>
            Registration time: {Utils.convertDate(credential.registrationTime)}
          </div>
          <div>Last used: {Utils.convertDate(credential.lastUsedTime)}</div>
        </Col>
        <Col xs={12} lg={3} className="d-none d-lg-block"></Col>
        <Col xs={12} lg={3} className="passkey-button-box">
          <button className="button-outlined" onClick={modalOpenEdit}>
            Update
          </button>
          <button onClick={modalOpenDelete} className="button-text">
            Delete
          </button>
        </Col>
      </Row>
      {showDeleteModal && (
        <Modal className="modal-primary" show={true}>
          <Modal.Header>Remove this passkey?</Modal.Header>
          <Modal.Body>
            <div>
              <p>
                You will no longer be able to use this passkey to sign in. You
                can create a new passkey for this device at any time.
              </p>
            </div>
          </Modal.Body>
          <Modal.Footer style={{ justifyContent: "right" }}>
            <button className="button-text" onClick={modalCloseDelete}>
              CANCEL
            </button>
            <button className="button-danger" onClick={deleteButton}>
              Remove
            </button>
          </Modal.Footer>
        </Modal>
      )}

      {showEditModal && (
        <Modal className="modal-primary" show={true}>
          <Modal.Header>Passkey name</Modal.Header>
          <Modal.Body>
            <div>
              <p>Change your passkey name to something you'll remember.</p>
              <input
                className="standard-input"
                type="text"
                placeholder="To"
                value={updateNickname}
                onChange={usernameOnChange}
                onKeyUp={onEnterKeyUp}
              />
            </div>
          </Modal.Body>
          <Modal.Footer style={{ justifyContent: "right" }}>
            <button className="button-text" onClick={modalCloseEdit}>
              Cancel
            </button>
            <button className="button-primary" onClick={updateButton}>
              Save
            </button>
          </Modal.Footer>
        </Modal>
      )}
    </div>
  );
}
