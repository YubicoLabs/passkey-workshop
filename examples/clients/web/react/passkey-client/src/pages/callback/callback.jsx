import { useEffect, useState } from "react";

import Loading from "../../components/loading";
import OIDCServices from "../../services/OIDCServices";
import Container from "react-bootstrap/esm/Container";
import Card from "react-bootstrap/Card";
import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";

import { useSearchParams, useNavigate } from "react-router-dom";
import Utils from "../../services/Utils";

export default function Callback({ callbackInfo }) {
  const [searchParams, setSearchParams] = useSearchParams();
  const code = searchParams.get("code");

  const navigate = useNavigate();

  const [loading, setLoading] = useState(false);

  const retrieveToken = async () => {
    try {
      await Utils.timeoutUtil(1500);

      const tokensFound = await OIDCServices.retrieveAccessToken(code);

      if (tokensFound) {
        const checkingAuth = OIDCServices.stillAuthenticated();

        if (checkingAuth) {
          navigate("/");
        } else {
          navigate("/sign_in");
        }
      } else {
        throw new Error("There was an issue getting your access token");
      }
    } catch (e) {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (code !== "") {
      retrieveToken();
    }
  });

  return (
    <div style={{ marginTop: "5em" }}>
      <Container>
        <Row>
          <Col lg={{ span: 8, offset: 2 }} md={{ span: 10, offset: 1 }}>
            <Card>
              <Card.Body>
                <Loading loadingInfo={{ message: callbackInfo.message }} />
              </Card.Body>
            </Card>
          </Col>
        </Row>
      </Container>
    </div>
  );
}
