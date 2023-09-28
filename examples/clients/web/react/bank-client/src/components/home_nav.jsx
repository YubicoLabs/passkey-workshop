import Row from "react-bootstrap/Row";
import Nav from "react-bootstrap/Nav";

import { Link } from "react-router-dom";

export default function HomeNav() {
  return (
    <div>
      <Row className="nav-home">
        <span className="text-1">Quick actions</span>
        <Nav className="flex-column">
          <Link className="nav-link-home text-1" to="/transaction">
            Make a deposit
          </Link>
          <Link className="nav-link-home text-1" to="/transaction">
            Make a transfer
          </Link>
          <Link className="nav-link-home text-1" to="/account">
            Account settings
          </Link>
        </Nav>
      </Row>
    </div>
  );
}
