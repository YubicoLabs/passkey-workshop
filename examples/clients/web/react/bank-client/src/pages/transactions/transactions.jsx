import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";
import PageSideNav from "../../components/page-side-nav";
import { useState } from "react";

import { useLocation, useNavigate } from "react-router-dom";
import { useEffect } from "react";
import BankServices from "../../services/BankServices";

import Spinner from "react-bootstrap/Spinner";
import Modal from "react-bootstrap/Modal";
import AuthServices from "../../services/AuthServices";
import Utils from "../../services/Utils";

export default function Transactions() {
  /**
   * Need a way for this component to see the account
   */

  const location = useLocation();
  const navigate = useNavigate();
  const [account, setAccount] = useState(null);
  const [transactionType, setTransactionType] = useState("deposit");
  const [amount, setAmount] = useState(undefined);
  const [to, setTo] = useState("");

  const [showModal, setShowModal] = useState(false);
  const [successModal, setSuccessModal] = useState(false);
  const [errorModal, setErrorModal] = useState(false);

  const [stepUpAuthState, setStepUpAuthState] = useState({
    stepUpAuthAttempted: false,
  });
  const [authInProgress, setAuthInProgress] = useState(false);

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
      } else if (amount === undefined) {
        throw new Error("No amount was provided for the transaction");
      }
      let transaction = to;
      if (transaction === "") {
        transaction = "New transaction";
      }

      const createTransactionResult = await BankServices.createTransactions(
        account.accountId,
        transactionType,
        amount,
        transaction
      );

      if (createTransactionResult.status === "complete") {
        /**
         * Open modal to indicate successes
         */
        setSuccessModal(true);
      } else {
        throw new Error("Transaction was not successfully created");
      }
    } catch (e) {
      /**
       * Trigger a modal noting that the transaction failed
       */

      if (
        e.status_code !== undefined &&
        e.status_code !== null &&
        e.status_code === 401
      ) {
        stepupTransaction();
      } else {
        setErrorModal(true);
        console.error("The transaction could not be processed successfully ");
        console.error(e);
      }
    }
  };

  const stepupTransaction = async () => {
    setShowModal(true);
  };

  const openStepUpWindow = () => {
    setAuthInProgress(true);
    const authWindow = window.open(
      AuthServices.STEPUP_AUTH_URL +
        `&username=${AuthServices.getLocalUsername()}`,
      "targetWindow",
      `toolbar=no,
     location=no,
     status=no,
     menubar=no,
     scrollbars=yes,
     resizable=yes,
     width=500px,
     height=500px`
    );

    const timeLoop = setInterval(async function () {
      if (authWindow.closed) {
        clearInterval(timeLoop);
        setShowModal(false);
        setAuthInProgress(false);
        await Utils.timeoutUtil(200);
        setStepUpAuthState({
          stepUpAuthAttempted: true,
        });
        submitTransaction();
      }
    }, 1000);
  };

  const returnToAccount = () => {
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

  const stepUpFailed = () => {
    setShowModal(false);
    setErrorModal(true);
  };

  const handleCloseError = () => {
    setErrorModal(false);
  };

  return (
    <div>
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
                      <path
                        d="M14 8H8V14H6V8H0V6H6V0H8V6H14V8Z"
                        fill="#F2F0FF"
                      />
                    </svg>
                  </button>
                  <button
                    className="transaction-type-button"
                    onClick={() => switchTransactionType("withdraw")}
                    disabled={isTransactionType("withdraw")}
                    style={{
                      borderWidth: isTransactionType("withdraw")
                        ? "4px"
                        : "1px",
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
                <button className="button-text" onClick={stepUpFailed}>
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

      {/**
       * Modal used for step-up auth
       */}
      <Modal className="modal-primary" show={showModal}>
        <Modal.Header>Step-up authentication required</Modal.Header>
        <Modal.Body>
          {stepUpAuthState.stepUpAuthAttempted && (
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
                Error: Step-up authentication failed, please try again
              </span>
            </div>
          )}
          {authInProgress ? (
            <span>
              Step-up authentication in progress. Please follow the instructions
              on the pop-up window
            </span>
          ) : (
            <span>Blurb about stepup authentication</span>
          )}
        </Modal.Body>
        <Modal.Footer>
          {authInProgress ? (
            <button
              className="button-primary"
              onClick={openStepUpWindow}
              disabled={authInProgress}>
              <Spinner></Spinner>
            </button>
          ) : (
            <button
              className="button-primary"
              onClick={openStepUpWindow}
              disabled={authInProgress}>
              UNLOCK WITH SECURITY KEY
            </button>
          )}
          <button className="button-text" onClick={stepUpFailed}>
            CANCEL
          </button>
        </Modal.Footer>
      </Modal>
      {/**
       * Modal used for successful transaction
       */}
      <Modal className="modal-status" show={successModal}>
        <Modal.Header>
          <svg
            xmlns="http://www.w3.org/2000/svg"
            width="73"
            height="72"
            viewBox="0 0 73 72"
            fill="none">
            <g clipPath="url(#clip0_518_3955)">
              <path
                d="M36.5 6C19.94 6 6.5 19.44 6.5 36C6.5 52.56 19.94 66 36.5 66C53.06 66 66.5 52.56 66.5 36C66.5 19.44 53.06 6 36.5 6ZM36.5 60C23.27 60 12.5 49.23 12.5 36C12.5 22.77 23.27 12 36.5 12C49.73 12 60.5 22.77 60.5 36C60.5 49.23 49.73 60 36.5 60ZM50.27 22.74L30.5 42.51L22.73 34.77L18.5 39L30.5 51L54.5 27L50.27 22.74Z"
                fill="#6EC500"
              />
            </g>
            <defs>
              <clipPath id="clip0_518_3955">
                <rect
                  width="72"
                  height="72"
                  fill="white"
                  transform="translate(0.5)"
                />
              </clipPath>
            </defs>
          </svg>
        </Modal.Header>
        <Modal.Body>
          <span className="text-1">Success!</span>
          <span className="text-2">
            Your transaction posted successfully. Click the button below to
            return to your account screen.
          </span>
        </Modal.Body>
        <Modal.Footer>
          <button className="button-primary" onClick={returnToAccount}>
            Return to account
          </button>
        </Modal.Footer>
      </Modal>

      <Modal className="modal-status" show={errorModal}>
        <Modal.Header>
          <svg
            xmlns="http://www.w3.org/2000/svg"
            width="60"
            height="60"
            viewBox="0 0 60 60"
            fill="none">
            <path
              fillRule="evenodd"
              clipRule="evenodd"
              d="M0 30C0 13.44 13.44 0 30 0C46.56 0 60 13.44 60 30C60 46.56 46.56 60 30 60C13.44 60 0 46.56 0 30ZM6 30C6 43.23 16.77 54 30 54C43.23 54 54 43.23 54 30C54 16.77 43.23 6 30 6C16.77 6 6 16.77 6 30ZM38.0572 18L42 21.9427L33.9427 30L42 38.0572L38.0573 42L30 33.9427L21.9427 42L18 38.0572L26.0573 30L18 21.9427L21.9427 18L30 26.0572L38.0572 18Z"
              fill="#FF4445"
            />
          </svg>
        </Modal.Header>
        <Modal.Body>
          <span className="text-1">Error!</span>
          <span className="text-2">
            Your transaction failed for an unknown reason, please try again!
          </span>
        </Modal.Body>
        <Modal.Footer>
          <button className="button-primary" onClick={handleCloseError}>
            Try again!
          </button>
        </Modal.Footer>
      </Modal>
    </div>
  );
}
