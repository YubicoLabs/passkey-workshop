import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";
import PageSideNav from "../../components/page-side-nav";
import { useState } from "react";

import { useLocation, useNavigate } from "react-router-dom";
import { useEffect } from "react";
import BankServices from "../../services/BankServices";

import Spinner from "react-bootstrap/Spinner";

export default function Transactions() {
  /**
   * Need a way for this component to see the account
   */

  const location = useLocation();
  const navigate = useNavigate();
  const [account, setAccount] = useState(null);
  const [transactionType, setTransactionType] = useState("deposit");
  const [amount, setAmount] = useState(0);
  const [to, setTo] = useState("");

  useEffect(() => {
    getUserAccount();
  }, []);

  useEffect(() => {
    if (location.pathname.includes("deposit")) {
      setTransactionType("deposit");
    } else if (location.pathname.includes("withdraw")) {
      setTransactionType("withdraw");
    }
  });

  const getUserAccount = async () => {
    try {
      const accountsList = await BankServices.getAccounts();
      /**
       * For our example, the user only has one account
       * Always get the first account found in the list
       */
      const currentAccount = accountsList.accounts[0];
      if (currentAccount !== null) {
        setAccount(currentAccount);
      } else {
        throw new Error("There was an issue getting the user's account");
      }
    } catch (e) {}
  };

  const isTransactionType = (type) => {
    return type === transactionType;
  };

  const switchTransactionType = (type) => {
    navigate(`/transactions/${type}`);
  };

  const submitTransaction = async () => {
    try {
      if (account === null) {
        throw new Error("User is not signed in with a valid account");
      }
      const createTransactionResult = await BankServices.createTransactions(
        account.accountId,
        transactionType,
        amount,
        to
      );

      if (createTransactionResult.status === "complete") {
        /**
         * Open modal to indicate successes
         */
        navigate("/dashboard");
      } else {
        throw new Error("Transaction was not successfully created");
      }
    } catch (e) {
      console.error("The transaction could not be processed successfully ");
      /**
       * Trigger a modal noting that the transaction failed
       */
    }
  };

  const cancelTransaction = () => {
    navigate("/dashboard");
  };

  const pageSideNavProps = [
    { name: "DEPOSIT", link: "/transactions/deposit" },
    { name: "WITHDRAW", link: "/transactions/withdraw" },
  ];

  const handleChangeAmount = (event) => {
    setAmount(event.target.value);
  };

  const handleChangeTo = (event) => {
    setTo(event.target.value);
  };

  return (
    <Row>
      <Col md={12} lg={2}>
        <PageSideNav title="Payments" linkObjects={pageSideNavProps} />
      </Col>
      <Col md={12} lg={9}>
        {account !== null ? (
          <div className="transaction-parent">
            <div>
              <h3>Account (XXXXXXXX-{account.accountId})</h3>
            </div>
            <div>
              <h2>Account balance</h2>
              <span className="title">
                ${account.balance.toLocaleString("en-US")}
              </span>
            </div>
            <div style={{ maringTop: "80px" }}>
              <div>
                <input
                  className="standard-input"
                  type="text"
                  placeholder="Amount"
                  style={{ marginRight: "35px" }}
                  value={amount}
                  onChange={handleChangeAmount}
                />

                <button
                  className="transaction-type-button"
                  onClick={() => switchTransactionType("deposit")}
                  disabled={isTransactionType("deposit")}
                  style={{
                    marginRight: "8px",
                    borderWidth: isTransactionType("deposit") ? "4px" : "1px",
                  }}>
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    width="14"
                    height="14"
                    viewBox="0 0 14 14"
                    fill="none">
                    <path d="M14 8H8V14H6V8H0V6H6V0H8V6H14V8Z" fill="#F2F0FF" />
                  </svg>
                </button>
                <button
                  className="transaction-type-button"
                  onClick={() => switchTransactionType("withdraw")}
                  disabled={isTransactionType("withdraw")}
                  style={{
                    borderWidth: isTransactionType("withdraw") ? "4px" : "1px",
                  }}>
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    width="14"
                    height="14"
                    viewBox="0 0 24 24"
                    fill="none">
                    <path d="M20 11H4V13H20V11Z" fill="#F2F0FF" />
                  </svg>
                </button>
              </div>
              <div style={{ marginTop: "16px" }}>
                <input
                  className="standard-input"
                  type="text"
                  placeholder="To"
                  value={to}
                  onChange={handleChangeTo}
                />
              </div>
            </div>
            <div style={{ marginTop: "16px" }}>
              <button className="button-primary" onClick={submitTransaction}>
                {transactionType.toUpperCase()}
              </button>
              <button className="button-text" onClick={cancelTransaction}>
                CANCEL
              </button>
            </div>
          </div>
        ) : (
          <Spinner animation="border" role="status">
            <span className="visually-hidden">Loading...</span>
          </Spinner>
        )}
      </Col>
    </Row>
  );
}
