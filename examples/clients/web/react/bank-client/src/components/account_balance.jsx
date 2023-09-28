import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";
import { Link } from "react-router-dom";

export default function AccountBalance() {
  return (
    <div>
      <div className="account-balance-parent">
        <h3>Account name (1111)</h3>
        <div>
          <h2>Account balance</h2>
          <h1 className="title">$1,111</h1>
        </div>
        <div className="recent-transactions">
          <h2>Recent transactions</h2>
          <div className="recent-transactions-list">
            <Row className="text-1">
              <Col xs={4}>Payment</Col>
              <Col xs={4}>Date</Col>
              <Col xs={4}>Amount</Col>
            </Row>
            <Row className="body-1 recent-transactions-list-item">
              <Col xs={4}>Beelzebub</Col>
              <Col xs={4}>July 21, 8:03 AM EST</Col>
              <Col xs={4}>$1111</Col>
            </Row>
            <Row className="body-1 recent-transactions-list-item">
              <Col xs={4}>Behemoth</Col>
              <Col xs={4}>June 13, 9:25 AM EST</Col>
              <Col xs={4}>$2222</Col>
            </Row>
            <Row className="body-1 recent-transactions-list-item">
              <Col xs={4}>Lucy</Col>
              <Col xs={4}>April 19, 1:22 PM EST</Col>
              <Col xs={4}>$3333</Col>
            </Row>
          </div>
        </div>
        <div>
          <Link className="account-balance-link text-1" to="/payments">
            View all{""}
            <svg
              xmlns="http://www.w3.org/2000/svg"
              width="24"
              height="24"
              viewBox="0 0 24 24"
              fill="none">
              <path
                d="M9.99984 6L8.58984 7.41L13.1698 12L8.58984 16.59L9.99984 18L15.9998 12L9.99984 6Z"
                fill="white"
              />
            </svg>
          </Link>
        </div>
      </div>
    </div>
  );
}
