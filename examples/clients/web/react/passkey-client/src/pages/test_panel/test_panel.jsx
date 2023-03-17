import { useState } from "react";

import { create, get } from "@github/webauthn-json";

import Stack from "react-bootstrap/Stack";
import Form from "react-bootstrap/Form";
import Button from "react-bootstrap/Button";
import Alert from "react-bootstrap/Alert";
import PasskeyServices from "../../services/PasskeyServices";
import CredentialCard from "../../components/credential_card";

export default function TestPanel() {
  const [username, setUsername] = useState("TestAccount");
  const [credentialList, setCredentialList] = useState([]);
  const [regBanner, setRegBanner] = useState({
    message: "Nothing to report :)",
    variant: "primary",
  });
  const [authBanner, setAuthBanner] = useState({
    message: "Nothing to report :)",
    variant: "primary",
  });

  const onUsernameChange = (e) => {
    setUsername(e.target.value);
  };

  const onRegisterClick = async () => {
    try {
      /**
       * Get registration options from the RP
       */
      console.info(`Getting options for user: ${username}`);
      const attestationOptions = await PasskeyServices.getAttestationOptions(
        username,
        "preferred",
        "cross-platform",
        "preferred",
        "direct"
      );

      /**
       * Create the credential using the reg options
       */
      const makeCredential = await create(attestationOptions);

      /**
       * Send the new credential to the user
       */
      const registrationResult = await PasskeyServices.sendAttestationResult(
        attestationOptions.requestId,
        makeCredential
      );

      /**
       * Validate that the key was created
       * Otherwise display a message
       */
      if (registrationResult.status === "created") {
        console.info("Registration successful");
        setRegBanner({
          message: "Credential created",
          variant: "success",
        });
      } else {
        console.info("Registration failed");
        setRegBanner({
          message: "Registration failed",
          variant: "danger",
        });
      }
    } catch (e) {
      //console.log(e);
      console.info("Registration failed");
      setRegBanner({
        message: "Registration failed",
        variant: "danger",
      });
    }
  };

  const onAuthenticateNDCClick = async () => {
    /**
     * Trigger the auth method with the current username
     * Used to invoke a non-discoverable credential flow
     */
    authenticateUser(username);
  };

  const onAuthenticateDCClick = async () => {
    /**
     * Trigger the auth method with an empty username
     * An empty username is used to invoke a flow using a
     * discoverable credential
     */
    authenticateUser("");
  };

  const authenticateUser = async (authUser) => {
    try {
      /**
       * Get authentication options from the RP
       */
      console.info(`Getting options for user: ${authUser}`);
      const assertionOptions = await PasskeyServices.getAssertionOptions(
        authUser
      );

      /**
       * Attempt to get the assertion using the auth options
       */
      const assertionResult = await get(assertionOptions);

      /**
       * Send the assertion to the RP
       */
      const authenticationResult = await PasskeyServices.sendAssertionResult(
        assertionOptions.requestId,
        assertionResult
      );

      /**
       * Validate that the key was created
       * Otherwise display a message
       */
      if (authenticationResult.status === "ok") {
        console.info("Authentication successful");
        setAuthBanner({
          message: "Auth success",
          variant: "success",
        });
      } else {
        console.info("Auth failed");
        setAuthBanner({
          message: "Auth failed",
          variant: "danger",
        });
      }
    } catch (e) {
      console.info("Auth failed1");
      setAuthBanner({
        message: "Auth failed",
        variant: "danger",
      });
    }
  };

  const getCredentials = async () => {
    const credList = await PasskeyServices.getCredentials(username);

    setCredentialList(credList.credentials);
  };

  return (
    <div>
      <Stack gap={5}>
        <div>
          <Form>
            <Form.Group className="mb-3" controlId="Username">
              <Form.Label>Username</Form.Label>
              <Form.Control
                value={username}
                placeholder={username}
                onChange={onUsernameChange}
              />
            </Form.Group>
          </Form>
        </div>
        <div>
          <h3>Credentials:</h3>
          <Stack gap={2}>
            <div>
              <Button size="lg" onClick={getCredentials}>
                Click here to load credentials
              </Button>
            </div>
            <div>
              <h5>Credential list</h5>
            </div>
            <div>
              {credentialList.map((item) => (
                <CredentialCard credential={item} />
              ))}
            </div>
          </Stack>
        </div>
        <div>
          <h3>Registration tests</h3>
          <Alert variant={regBanner.variant}>{regBanner.message}</Alert>
          <Button size="lg" onClick={onRegisterClick}>
            Click here to register
          </Button>
        </div>
        <div>
          <h3>Authentication tests</h3>
          <Alert variant={authBanner.variant}>{authBanner.message}</Alert>
          <Stack gap={2}>
            <div>
              <Button size="lg" onClick={onAuthenticateNDCClick}>
                Click here to authenticate (Non-discoverable)
              </Button>
            </div>
            <div>
              <Button size="lg" onClick={onAuthenticateDCClick}>
                Click here to authenticate (Discoverable)
              </Button>
            </div>
          </Stack>
        </div>
      </Stack>
    </div>
  );
}
