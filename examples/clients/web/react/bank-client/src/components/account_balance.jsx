import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";
import { useEffect } from "react";
import { useState } from "react";
import BankServices from "../services/BankServices";

import Spinner from "react-bootstrap/Spinner";
import Utils from "../services/Utils";

export default function AccountBalance() {
  const [account, setAccount] = useState(null);
  const [transactions, setTransactions] = useState([]);
  const [error, setError] = useState(false);

  const getUserAccount = async () => {
    try {
      const accountsList = await BankServices.getAccounts();
      /**
       * For our example, the user only has one account
       * Always get the first account found in the list
       */
      const currentAccount = accountsList.accounts[0];
      if (currentAccount !== null && currentAccount !== undefined) {
        setAccount(currentAccount);
      } else {
        throw new Error("There was an issue getting the user's account");
      }
    } catch (e) {
      /**
       * Need to do something if the account cannot be found
       */
      setError(true);
      console.error(e.message);
    }
  };

  const getUserAccountTransactions = async () => {
    const transactionsList = await BankServices.getTransactions(
      account.accountId
    );
    /**
     * For our example, the user only has one account
     * Always get the first account found in the list
     */
    if (transactionsList.transactions !== null) {
      setTransactions(transactionsList.transactions);
    } else {
      setTransactions([]);
      throw new Error("There was an issue getting the user's transactions");
    }
  };

  const transactionSymbol = (transactionType) => {
    if (transactionType === "withdraw") {
      return "-";
    } else if (transactionType === "deposit") {
      return "+";
    } else {
      return "?";
    }
  };

  useEffect(() => {
    getUserAccount();
  }, []);

  useEffect(() => {
    if (account !== null && account.accountId !== null)
      getUserAccountTransactions();
  }, [account]);

  return (
    <div>
      {account !== null ? (
        <div className="account-balance-parent">
          <h3>Account (XXXXXXXX-{account.accountId})</h3>
          <div>
            <h2>Account balance</h2>
            <h1 className="title">
              ${account.balance.toLocaleString("en-US")}
            </h1>
          </div>
          <div className="recent-transactions">
            <h2>Recent transactions</h2>
            <div className="recent-transactions-list">
              <Row className="text-1">
                <Col xs={4}>Payment</Col>
                <Col xs={4}>Date</Col>
                <Col xs={4}>Amount</Col>
              </Row>
              {transactions.map((value, i) => (
                <Row className="body-1 recent-transactions-list-item" key={i}>
                  <Col xs={4}>{value.description}</Col>
                  <Col xs={4}>{Utils.convertDate(value.transactionDate)}</Col>
                  <Col xs={4}>
                    {transactionSymbol(value.type)} ${value.amount}
                  </Col>
                </Row>
              ))}
            </div>
          </div>
        </div>
      ) : error ? (
        <div>
          <div className="error-alert">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              width="24"
              height="24"
              viewBox="0 0 24 24"
              fill="none">
              <g clipPath="url(#clip0_11_895)">
                <path
                  d="M12 5.99L19.53 19H4.47L12 5.99ZM12 2L1 21H23L12 2ZM13 16H11V18H13V16ZM13 10H11V14H13V10Z"
                  fill="#F2F0FF"
                />
              </g>
              <defs>
                <clipPath id="clip0_11_895">
                  <rect width="24" height="24" fill="white" />
                </clipPath>
              </defs>
            </svg>
            <span>
              There was an issue finding your account. Please refresh the page,
              or attempt to logout and sign back in to your account. Please
              contact your administrator if the problem persists.
            </span>
          </div>
        </div>
      ) : (
        <Spinner animation="border" role="status">
          <span className="visually-hidden">Loading...</span>
        </Spinner>
      )}
    </div>
  );
}
