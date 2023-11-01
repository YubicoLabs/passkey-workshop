import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";
import { Link } from "react-router-dom";
import { useEffect } from "react";
import { useState } from "react";
import BankServices from "../services/BankServices";

import Spinner from "react-bootstrap/Spinner";
import Utils from "../services/Utils";

export default function AccountBalance() {
  const [account, setAccount] = useState(null);
  const [transactions, setTransactions] = useState([]);

  const getUserAccount = async () => {
    try {
      const accountsList = await BankServices.getAccounts();
      /**
       * For our example, the user only has one account
       * Always get the first account found in the list
       */
      const currentAccount = accountsList.accounts[0];
      console.log(currentAccount);
      if (currentAccount !== null && currentAccount !== undefined) {
        setAccount(currentAccount);
      } else {
        throw new Error("There was an issue getting the user's account");
      }
    } catch (e) {
      /**
       * Need to do something if the account cannot be found
       */
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
      console.log(transactionsList.transactions);
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
          {/*          <div>
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
          */}
        </div>
      ) : (
        <Spinner animation="border" role="status">
          <span className="visually-hidden">Loading...</span>
        </Spinner>
      )}
    </div>
  );
}
