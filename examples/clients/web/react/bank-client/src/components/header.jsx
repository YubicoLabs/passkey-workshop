import { useEffect, useState } from "react";
import Container from "react-bootstrap/Container";
import Nav from "react-bootstrap/Nav";
import Navbar from "react-bootstrap/Navbar";
import { Link } from "react-router-dom";

export default function Header() {
  const [email, setEmail] = useState("");

  useEffect(() => {
    const lsString = window.localStorage.getItem("USER_INFO");
    const response = JSON.parse(lsString);
    setEmail(
      response !== null && response.preferred_username !== null
        ? response.preferred_username
        : ""
    );
  }, []);

  return (
    <Navbar expand="md">
      <Container>
        <Navbar.Brand href="/dashboard">
          <img
            alt=""
            src="/img/logo.png"
            width="30"
            height="30"
            className="d-inline-block align-top"
          />{" "}
          MSBS
        </Navbar.Brand>
        <Navbar.Toggle aria-controls="basic-navbar-nav" />
        <Navbar.Collapse id="basic-navbar-nav">
          <Nav className="me-auto">
            <Link className="nav-link" to="/dashboard">
              HOME
            </Link>
            <Link className="nav-link" to="/transactions/deposit">
              PAYMENTS
            </Link>
            <Link className="nav-link" to="account">
              SETTINGS
            </Link>
          </Nav>
          <Nav className="justify-content-end">
            <div className="nav-notification d-none d-md-inline">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                width="16"
                height="20"
                viewBox="0 0 16 20"
                fill="none">
                <path
                  d="M8 20C9.1 20 10 19.1 10 18H6C6 19.1 6.9 20 8 20ZM14 14V9C14 5.93 12.37 3.36 9.5 2.68V2C9.5 1.17 8.83 0.5 8 0.5C7.17 0.5 6.5 1.17 6.5 2V2.68C3.64 3.36 2 5.92 2 9V14L0 16V17H16V16L14 14ZM12 15H4V9C4 6.52 5.51 4.5 8 4.5C10.49 4.5 12 6.52 12 9V15Z"
                  fill="#F2F0FF"
                />
              </svg>
            </div>
            {email !== "" ? (
              <div className="nav-username">
                <span className="nav-username-circle d-none d-lg-inline-block">
                  {email.charAt(0).toUpperCase()}
                </span>
                <span>{email}</span>
              </div>
            ) : (
              <></>
            )}
          </Nav>
        </Navbar.Collapse>
      </Container>
    </Navbar>
  );
}
