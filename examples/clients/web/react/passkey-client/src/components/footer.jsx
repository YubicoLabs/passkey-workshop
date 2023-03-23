import Navbar from "react-bootstrap/Navbar";
import Nav from "react-bootstrap/Nav";
import { Link } from "react-router-dom";
import Container from "react-bootstrap/Container";
import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";
import Stack from "react-bootstrap/Stack";

export default function Footer() {
  //https://passkey.org/images/Created-by-Yubico.svg
  return (
    <Navbar
      fixed="bottom"
      bg="dark"
      variant="dark"
      style={{ color: "#fff", position: "inherit", marginTop: "3em" }}>
      <Container>
        <Stack>
          <div>
            <Row className="footer-item">
              <Col style={{ display: "block", margin: "auto" }}>
                <div>
                  <a
                    href="https://www.yubico.com/"
                    target="_blank"
                    rel="noreferrer">
                    <img
                      src="https://passkey.org/images/Created-by-Yubico.svg"
                      loading="lazy"
                      alt=""
                    />
                  </a>
                </div>
              </Col>
              <Col>
                <h6>Useful passkey resources</h6>
                <ul>
                  <li>
                    <a
                      href="https://developers.yubico.com/Passkeys/"
                      target="_blank"
                      rel="noreferrer">
                      Passkey developer guidance
                    </a>
                  </li>
                  <li>
                    <a
                      href="https://passkey.org/"
                      target="_blank"
                      rel="noreferrer">
                      Passkey demo
                    </a>
                  </li>
                  <li>
                    <a
                      href="https://passkeys.dev/"
                      target="_blank"
                      rel="noreferrer">
                      passkeys.dev
                    </a>
                  </li>
                </ul>
              </Col>
            </Row>
          </div>
          <div>Made with &lt;3 by Yubico's Developer Program</div>
        </Stack>
      </Container>
    </Navbar>
  );
}
