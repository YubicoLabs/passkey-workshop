import { useEffect, useState } from "react";

import "./App.css";

import Header from "./components/header";
import Footer from "./components/footer";
import {
  Stack,
  Form,
  Container,
  Button,
  Row,
  Col,
  Alert,
} from "react-bootstrap";
import CredentialCard from "./components/credential_card";
import PasskeyServices from "./services/PasskeyServices";
import { create, get } from "@github/webauthn-json";

function App() {
  const [username, setUsername] = useState("TestAccount");
  const [credentialList, setCredentialList] = useState([]);
  const [regBanner, setRegBanner] = useState({
    message: "Nothing to report :)",
    variant: "null",
  });
  const [authBanner, setAuthBanner] = useState({
    message: "Nothing to report :)",
    variant: "null",
  });

  //Used to abort conditional mediation (autofill)
  const [authAbortController, setAuthAbortController] = useState(
    new AbortController()
  );

  const onUsernameChange = (e) => {
    setUsername(e.target.value);
  };

  const onDiscoverableAuth = (e) => {
    e.preventDefault();
    authenticate(username, false);
  };

  const onGetCredentials = (e) => {
    e.preventDefault();
    getCredentials();
  };

  const register = async () => {
    //Stop any conditional mediation should it be running
    authAbortController.abort();
    try {
      /**
       * Get registration options from the RP
       */
      console.info(`Getting assertion options for user: ${username}`);
      const attestationOptions = await PasskeyServices.getAttestationOptions(
        username
      );

      /**
       * Create the credential using .create() and the attestation options
       */
      const makeCredential = await create(attestationOptions);

      /**
       * Send the new credential to the RP
       */
      await PasskeyServices.sendAttestationResult(
        attestationOptions.requestId,
        makeCredential
      );

      console.info("Registration successful");
      setRegBanner({
        message: "Credential created",
        variant: "success",
      });
    } catch (e) {
      console.error("Credential creation failed with the following error");
      console.error(e);
      setRegBanner({
        message:
          "Credential creation failed with the following message: " + e.message,
        variant: "danger",
      });
    }
    //Refire conditional mediation
    setAuthAbortController(new AbortController());
    authenticate("", true);
  };

  const authenticate = async (authUser, isAutofill) => {
    try {
      /**
       * Get assertion options from the RP
       */
      console.info(`Getting options for user: ${authUser}`);
      const assertionOptions = await PasskeyServices.getAssertionOptions(
        authUser
      );

      //Add additional parameters to assertion options if we're using an autofill flow
      if (isAutofill) {
        assertionOptions.mediation = "conditional";
        assertionOptions.signal = authAbortController.signal;
      } else {
        //Stop any conditional mediation should it be running
        authAbortController.abort();
      }

      /**
       * Attempt to get assertion with .get()
       */
      const assertionResult = await get(assertionOptions);

      /**
       * Send the assertion to the RP
       */
      const authenticationResult = await PasskeyServices.sendAssertionResult(
        assertionOptions.requestId,
        assertionResult
      );

      if (!authenticationResult.status === "ok") {
        throw new Error("Attestation not valid in RP");
      }

      console.info("Authentication successful");
      setAuthBanner({
        message: "Auth success",
        variant: "success",
      });
    } catch (e) {
      //Error not due to cancellation of conditional mediation
      if (e.name !== "AbortError") {
        console.error("Authentication failed with the following error");
        console.error(e);
        setAuthBanner({
          message:
            "Authentication failed with the following message: " + e.message,
          variant: "danger",
        });
      }
    }
  };

  const getCredentials = async () => {
    try {
        const credList = await PasskeyServices.getCredentials(username);
        setCredentialList(credList.credentials);
    } catch(e) {
      setCredentialList([])
    }
  };

  useEffect(() => {
    if (mediationAvailable) {
      authenticate("", true);
    }
  }, []);

  const mediationAvailable = async () => {
    const pubKeyCred = window.PublicKeyCredential;
    if (
      typeof pubKeyCred.isConditionalMediationAvailable === "function" &&
      (await pubKeyCred.isConditionalMediationAvailable())
    ) {
      return true;
    } else {
      return false;
    }
  };

  return (
    <div className="d-flex flex-column min-vh-100">
      <Header />
      <Container className="content">
        <Stack gap={5}>
          {/** Enter username */}
          <Form className="test-section">
            <h3>Enter username</h3>
            <Form.Control
              value={username}
              placeholder={username}
              onChange={onUsernameChange}
              autoComplete="username webauthn"
            />
          </Form>

          {/** Register OR Authenticate */}
          <Row className="test-section">
            <Col className="reg-item">
              <h5>Registration</h5>
              <p>Click the button below to register a new passkey</p>
              <Button onClick={register}>Register passkey</Button>
              {regBanner.variant !== "null" && (
                <Alert variant={regBanner.variant}>{regBanner.message}</Alert>
              )}
            </Col>
            <Col className="auth-item">
              <h5>Authenticate</h5>
              <p>Click the button below to authenticate with a passkey</p>
              <Button onClick={onDiscoverableAuth}>
                Authenticate with passkey
              </Button>
              {authBanner.variant !== "null" && (
                <Alert variant={authBanner.variant}>{authBanner.message}</Alert>
              )}
            </Col>
          </Row>

          {/** Credential list */}
          <div className="test-section">
            <h3>See all credentials for the user account</h3>
            <Stack gap={3}>
              {credentialList.length > 0 && (
                <div>
                  <h5>Credential list</h5>
                  <div className="card-list">
                    {credentialList.map((item) => (
                      <CredentialCard credential={item} />
                    ))}
                  </div>
                </div>
              )}
              <div>
                <Button onClick={onGetCredentials}>
                  Click here to load credentials
                </Button>
              </div>
            </Stack>
          </div>
        </Stack>
      </Container>
      <Footer />
    </div>
  );
}

export default App;
