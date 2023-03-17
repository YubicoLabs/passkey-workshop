import { useCallback, useEffect, useState } from "react";

import Container from "react-bootstrap/esm/Container";
import Form from "react-bootstrap/Form";
import Card from "react-bootstrap/Card";
import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";
import Button from "react-bootstrap/esm/Button";
import Stack from "react-bootstrap/esm/Stack";
import OverlayTrigger from "react-bootstrap/OverlayTrigger";
import Popover from "react-bootstrap/Popover";
import { QuestionCircle } from "react-bootstrap-icons";
import Alert from "react-bootstrap/Alert";

import { Link, useSearchParams } from "react-router-dom";

import { get } from "@github/webauthn-json";

import PasskeyServices from "../../services/PasskeyServices";
import Loading from "../../components/loading";
import Utils from "../../services/Utils";

export default function SignIn() {
  const [assertionResult, setAssertionResult] = useState("");
  const [userHandle, setUserHandle] = useState("");
  const [searchParams, setSearchParams] = useSearchParams();
  const [errorMessage, setErrorMessage] = useState("");
  const [loading, setLoading] = useState(false);
  const [username, setUsername] = useState("");

  const oidcFormValues = {
    client_id: "passkeyClient",
    redirect_uri: "http://localhost:3000/oidc/callback",
    scope: "openid",
    response_type: "code",
  };

  const submitForm = async (e) => {
    try {
      e.preventDefault();
      setLoading(true);
      const assertionOptions = await PasskeyServices.getAssertionOptions("");

      const assertionResult = await get(assertionOptions);

      const reqData = {
        requestId: assertionOptions.requestId,
        assertionResult: assertionResult,
      };

      const assertionJSON = JSON.stringify(reqData);

      await Utils.timeoutUtil(1500);
      setUserHandle(assertionResult.response.userHandle);
      setAssertionResult(assertionJSON);
    } catch (e) {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (assertionResult !== "" && userHandle !== "") {
      document.getElementById("passkeyForm").requestSubmit();
    }
  }, [assertionResult]);

  useEffect(() => {
    const invokeAutofill = async () => {
      if (mediationAvailablle) {
        await passkeySignIn();
      }
    };

    const errorFound = searchParams.get("status");

    if (errorFound !== null) {
      const errorMessage = searchParams.get("error_message");
      setErrorMessage(errorMessage);
    }

    invokeAutofill();
  }, []);

  const usernameOnChange = (e) => {
    setUsername(e.target.value);
  };

  const onEnterKeyUp = async (e) => {
    if (e.key === "Enter") {
      await submitForm(e);
    }
  };

  const mediationAvailablle = () => {
    const pubKeyCred = window.PublicKeyCredential;
    if (
      typeof pubKeyCred.isConditionalMediationAvailable === "function" &&
      pubKeyCred.isConditionalMediationAvailable()
    ) {
      return true;
    } else {
      return false;
    }
  };

  const passkeySignIn = useCallback(async () => {
    try {
      const authAbortController = new AbortController();
      const assertionOptions = await PasskeyServices.getAssertionOptions("");

      console.log(assertionOptions);

      const assertionResult = await get({
        publicKey: assertionOptions.publicKey,
        mediation: "conditional",
        signal: authAbortController.signal,
      });

      await Utils.timeoutUtil(1500);
      const reqData = {
        requestId: assertionOptions.requestId,
        assertionResult: assertionResult,
      };

      const assertionJSON = JSON.stringify(reqData);

      await Utils.timeoutUtil(1500);
      setUserHandle(assertionResult.response.userHandle);
      setAssertionResult(assertionJSON);
    } catch (e) {
      console.error(e);
    }
  }, []);

  return (
    <div style={{ marginTop: "5em" }}>
      <Container>
        <Row>
          <Col lg={{ span: 8, offset: 2 }} md={{ span: 10, offset: 1 }}>
            {errorMessage !== "" && (
              <Alert variant="danger">
                <Alert.Heading>
                  Oh no! There was an error logging in
                </Alert.Heading>
                <p>{errorMessage}</p>
              </Alert>
            )}
          </Col>
        </Row>
        <Row>
          <Col lg={{ span: 8, offset: 2 }} md={{ span: 10, offset: 1 }}>
            <Card>
              <Card.Body>
                <Stack gap={3} hidden={loading}>
                  <div>
                    <Card.Title style={{ textAlign: "center" }}>
                      Sign In
                    </Card.Title>
                  </div>
                  <div>
                    <Form
                      action={
                        "http://localhost:8081/realms/passkeyDemo/protocol/openid-connect/auth"
                      }
                      method="post"
                      id="passkeyForm">
                      <Form.Group>
                        <Form.Label>Username</Form.Label>
                        <Form.Control
                          type="text"
                          name="username"
                          placeholder="Enter username"
                          value={username}
                          onChange={usernameOnChange}
                          onKeyUp={onEnterKeyUp}
                          autoComplete="username webauthn"
                        />
                      </Form.Group>
                      <Form.Group hidden>
                        <Form.Control
                          type="text"
                          name="client_id"
                          value={oidcFormValues.client_id}
                          readOnly
                        />
                      </Form.Group>
                      <Form.Group hidden>
                        <Form.Control
                          type="text"
                          name="redirect_uri"
                          value={oidcFormValues.redirect_uri}
                          readOnly
                        />
                      </Form.Group>
                      <Form.Group hidden>
                        <Form.Control
                          type="text"
                          name="scope"
                          value={oidcFormValues.scope}
                          readOnly
                        />
                      </Form.Group>
                      <Form.Group hidden>
                        <Form.Control
                          type="text"
                          name="response_type"
                          value={oidcFormValues.response_type}
                          readOnly
                        />
                      </Form.Group>
                      <Form.Group hidden>
                        <Form.Control
                          type="text"
                          name="assertionResult"
                          value={assertionResult}
                          readOnly
                        />
                      </Form.Group>
                      <Form.Group hidden>
                        <Form.Control
                          type="text"
                          name="userHandle"
                          value={userHandle}
                          readOnly
                        />
                      </Form.Group>
                    </Form>
                  </div>
                  <div style={{ textAlign: "center" }}>
                    <Button onClick={submitForm}>Sign in with passkey</Button>
                    <OverlayTrigger
                      trigger="click"
                      placement="right"
                      overlay={
                        <Popover>
                          <Popover.Header as="h3">
                            Login with passkeys
                          </Popover.Header>
                          <Popover.Body>
                            <span>
                              Passkeys are a safer and more secure replacement
                              to passwords.
                            </span>
                            <br />
                            <br />
                            <span>
                              Your device will prompt you on how to login with
                              your passkey. Follow along to securely login to
                              your account
                            </span>
                          </Popover.Body>
                        </Popover>
                      }>
                      <QuestionCircle style={{ marginLeft: "1em" }} />
                    </OverlayTrigger>
                  </div>
                  <div style={{ paddingTop: "1em" }}>
                    <div>
                      <h6 className="middle-text">
                        <span>Need an account?</span>
                      </h6>
                    </div>
                    <div style={{ textAlign: "center", paddingTop: "0.5em" }}>
                      <Link to="/sign_up">
                        Click here to register for a new account
                      </Link>
                    </div>
                  </div>
                </Stack>
                <div hidden={!loading}>
                  <Loading loadingInfo={{ message: "Finding your profile" }} />
                </div>
              </Card.Body>
            </Card>
          </Col>
        </Row>
      </Container>
    </div>
  );
}
