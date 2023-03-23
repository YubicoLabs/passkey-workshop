import { useEffect, useState } from "react";

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

import { create } from "@github/webauthn-json";

import PasskeyServices from "../../services/PasskeyServices";
import Loading from "../../components/loading";
import Utils from "../../services/Utils";
import OIDCServices from "../../services/OIDCServices";

export default function SignUp() {
  const [attestationResult, setAttestationResult] = useState("");
  const [userHandle, setUserHandle] = useState("");
  const [username, setUsername] = useState("");
  const [searchParams, setSearchParams] = useSearchParams();
  const [errorMessage, setErrorMessage] = useState("");
  const [loading, setLoading] = useState(false);

  const oidcFormValues = {
    client_id: "passkeyClient",
    redirect_uri: "http://localhost:3000/oidc/callback",
    scope: "openid",
    response_type: "code",
  };

  const submitForm = async (e) => {
    try {
      e.preventDefault();
      /**
       * @TODO - Consider adding method call that checks if the username is valid prior to submission
       */
      console.log(username);

      setLoading(true);
      const attestationOptions = await PasskeyServices.getAttestationOptions(
        username
      );

      const makeCredentialResult = await create(attestationOptions);

      const reqData = {
        requestId: attestationOptions.requestId,
        makeCredentialResult: makeCredentialResult,
      };

      const attestationJSON = JSON.stringify(reqData);

      await Utils.timeoutUtil(1500);

      setUserHandle(attestationOptions.publicKey.user.id);
      setAttestationResult(attestationJSON);
    } catch (e) {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (attestationResult !== "") {
      document.getElementById("passkeyForm").requestSubmit();
    }
  }, [attestationResult]);

  useEffect(() => {
    const errorFound = searchParams.get("status");

    if (errorFound !== null) {
      const errorMessage = searchParams.get("error_message");
      setErrorMessage(errorMessage);
    }
  }, []);

  const usernameOnChange = (e) => {
    setUsername(e.target.value);
  };

  const onEnterKeyUp = async (e) => {
    if (e.key === "Enter") {
      await submitForm(e);
    }
  };

  return (
    <div style={{ marginTop: "5em" }}>
      <Container>
        <Row>
          <Col lg={{ span: 8, offset: 2 }} md={{ span: 10, offset: 1 }}>
            {errorMessage !== "" && (
              <Alert variant="danger">
                <Alert.Heading>
                  Oh no! There was an error creating your account!
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
                      Sign up
                    </Card.Title>
                  </div>
                  <div>
                    <Form
                      action={`${OIDCServices.GLOBAL_OIDC_CONFIGS.baseUri}/registrations`}
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
                      <Form.Group hidden>
                        <Form.Control
                          type="text"
                          name="client_id"
                          value={OIDCServices.OIDC_AUTH_CONFIGS.client_id}
                          readOnly
                        />
                      </Form.Group>
                      <Form.Group hidden>
                        <Form.Control
                          type="text"
                          name="redirect_uri"
                          value={OIDCServices.OIDC_AUTH_CONFIGS.redirect_uri}
                          readOnly
                        />
                      </Form.Group>
                      <Form.Group hidden>
                        <Form.Control
                          type="text"
                          name="scope"
                          value={OIDCServices.OIDC_AUTH_CONFIGS.scope}
                          readOnly
                        />
                      </Form.Group>
                      <Form.Group hidden>
                        <Form.Control
                          type="text"
                          name="response_type"
                          value={OIDCServices.OIDC_AUTH_CONFIGS.response_type}
                          readOnly
                        />
                      </Form.Group>
                      <Form.Group hidden>
                        <Form.Control
                          type="text"
                          name="attestationResult"
                          value={attestationResult}
                          readOnly
                        />
                      </Form.Group>
                    </Form>
                  </div>
                  <div style={{ textAlign: "center" }}>
                    <Button onClick={submitForm}>Create account</Button>
                    <OverlayTrigger
                      trigger="click"
                      placement="right"
                      overlay={
                        <Popover>
                          <Popover.Header as="h3">
                            Sign up with passkeys
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
                        <span>Already have an account?</span>
                      </h6>
                    </div>
                    <div style={{ textAlign: "center", paddingTop: "0.5em" }}>
                      <Link to="/sign_in">
                        Click here to login to your account
                      </Link>
                    </div>
                  </div>
                </Stack>
                <div hidden={!loading}>
                  <Loading loadingInfo={{ message: "Creating your profile" }} />
                </div>
              </Card.Body>
            </Card>
          </Col>
        </Row>
      </Container>
    </div>
  );
}
