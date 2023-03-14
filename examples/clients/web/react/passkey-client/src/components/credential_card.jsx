import { useState } from "react";
import { KeyFill } from "react-bootstrap-icons";
import Card from "react-bootstrap/Card";
import Col from "react-bootstrap/Col";
import Button from "react-bootstrap/esm/Button";
import Row from "react-bootstrap/Row";
import Stack from "react-bootstrap/Stack";
import Utils from "../services/Utils";
import PasskeyServices from "../services/PasskeyServices";
export default function CredentialCard({ credential, refreshCallback }) {
  const [loading, setLoading] = useState(false);

  const deleteButton = async (e) => {
    try {
      e.preventDefault();

      setLoading(true);
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
    } catch (e) {
      setLoading(false);
    }
  };

  return (
    <Card>
      <Card.Body>
        <Row
          className="justify-content-md-center"
          style={{ padding: "1em 0 0 0" }}>
          <Col sm={2} xs={12}>
            <div className="svg-container">
              <KeyFill className="svg-1" size={48} />
            </div>
          </Col>
          <Col sm={7} xs={12}>
            <div className="credential-info">
              <Card.Title>{credential.nickName}</Card.Title>
              <Stack gap={1}>
                <span>
                  Reg time: {Utils.convertDate(credential.registrationTime)}
                </span>
                <span>
                  Last used: {Utils.convertDate(credential.lastUsedTime)}
                </span>
              </Stack>
            </div>
          </Col>
          <Col sm={3} xs={12}>
            <Stack gap={1}>
              {/**
               * TODO - Add edit button
               */}
              <div className="delete-credential">
                {/**
                 * TODO - Consider adding modal
                 * Consider adding wording about passkey lifecycle
                 * "You still need to delete the passkey from your device"
                 */}
                <Button variant="danger" onClick={deleteButton}>
                  Delete
                </Button>
              </div>
            </Stack>
          </Col>
        </Row>
      </Card.Body>
    </Card>
  );
}
