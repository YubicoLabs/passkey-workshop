import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import LogoutButton from "../../components/logout_button";
import CredentialCard from "../../components/credential_card";
import Container from "react-bootstrap/Container";

import OIDCServices from "../../services/OIDCServices";
import PasskeyServices from "../../services/PasskeyServices";
import Stack from "react-bootstrap/Stack";
import Button from "react-bootstrap/esm/Button";

import { create } from "@github/webauthn-json";
import Row from "react-bootstrap/esm/Row";
import Col from "react-bootstrap/esm/Col";

export default function Home() {
  const navigation = useNavigate();
  const [credentialList, setCredentialList] = useState([]);
  const [username, setUsername] = useState("");
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const effectStillAuthenticated = async () => {
      const signedIn = await OIDCServices.stillAuthenticated();
      if (!signedIn) {
        navigation("/sign_in");
      } else {
        const currentUser = OIDCServices.getLocalUserInfo().preferred_username;
        setUsername(currentUser);
      }
    };
    effectStillAuthenticated();
  }, []);

  useEffect(() => {
    const initCredentials = async () => {
      if (username !== "") {
        await refreshCredentialList();
      }
    };
    initCredentials();
  }, [username]);

  const addNewPasskey = async (e) => {
    try {
      e.preventDefault();
      console.log(username);

      setLoading(true);
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
        setLoading(false);
      } else {
        throw new Error("Error with creating result");
      }
    } catch (e) {
      setLoading(false);
    }
  };

  const refreshCredentialList = async () => {
    console.log("This method called");
    const credList = await PasskeyServices.getCredentials(username);

    setCredentialList(credList.credentials);
  };

  return (
    <Stack gap={5}>
      <div style={{ marginTop: "3em", textAlign: "center" }}>
        <h3>Welcome {username}</h3>
      </div>
      {/**
       * Add button to create a new passkey
       */}
      <div>
        <h4>My passkeys</h4>
        <Container style={{ marginTop: "1.5em" }}>
          <Stack gap={3}>
            {credentialList.map((item, i) => (
              <CredentialCard
                key={i}
                credential={item}
                refreshCallback={refreshCredentialList}
              />
            ))}
          </Stack>
        </Container>
        <div style={{ marginTop: "1.5em", textAlign: "center" }}>
          <Button onClick={addNewPasskey}>Add a new passkey</Button>
        </div>
      </div>
      {/**
       * Add logout section
       */}
      <div>
        <h4>Logout</h4>
        <Container style={{ marginTop: "1.5em" }}>
          <Row className="justify-content-md-center">
            <Col xs="8" style={{ textAlign: "center" }}>
              <LogoutButton />
            </Col>
          </Row>
        </Container>
      </div>
    </Stack>
  );
}
