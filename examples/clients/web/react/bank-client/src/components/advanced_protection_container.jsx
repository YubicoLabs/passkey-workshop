import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";
import PasskeyServices from "../services/PasskeyServices";
import { useEffect, useState } from "react";

export default function AdvancedProtectionContainer() {
  const [hasAdvancedProtection, setHasAdvancedProtection] = useState(false);
  const [advancedProtectionEligable, setAdvancedProtectionEligable] =
    useState(false);

  /**
   * Return true if the account can enroll in AP, false otherwise
   */
  useEffect(() => {
    const determineAdvancedProtectionEligable = async () => {
      // Call to RP to get the list of credentials
      //Iterate through credentials, count the # of HA credentials
      //If < 2, allow the button to be triggered, display green message
      const lsString = window.localStorage.getItem("USER_INFO");
      const response = JSON.parse(lsString);
      const username = response.preferred_username;
      const passkeyList = await PasskeyServices.getCredentials(username);

      let highAssuranceCount = 0;

      passkeyList.credentials.forEach((element) => {
        console.log(element);
        if (element.isHighAssurance) {
          highAssuranceCount++;
        }
      });

      if (highAssuranceCount >= 2) {
        setAdvancedProtectionEligable(true);
      }
    };

    const checkHasAdvancedProtection = async () => {
      const lsString = window.localStorage.getItem("USER_INFO");
      const response = JSON.parse(lsString);
      const userHandle = response.sub;
      const status = await PasskeyServices.getAdvancedProtectionStatus(
        userHandle
      );

      setHasAdvancedProtection(status.enabled);
    };

    checkHasAdvancedProtection();
    determineAdvancedProtectionEligable();
  }, []);

  const changeAdvancedProtectionStatus = async () => {
    const lsString = window.localStorage.getItem("USER_INFO");
    const response = JSON.parse(lsString);
    const userHandle = response.sub;
    const result = await PasskeyServices.setAdvancedProtectionStatus(
      userHandle,
      !hasAdvancedProtection
    );

    setHasAdvancedProtection(result.enabled);
  };

  return (
    <div className="ap-box-parent">
      <h3>Advanced Protection</h3>
      <div>
        <div className="ap-box">
          <Row>
            <Col lg={10} xs={12}>
              <div className="ap-meta-info">
                <h3>Enable advanced protection</h3>
                {advancedProtectionEligable ? (
                  <div className="body-2 text-success">
                    Your account is eligible for advanced protection.
                  </div>
                ) : (
                  <div className="body-2 text-error">
                    Your account is not eligible for advanced protection.
                  </div>
                )}
                <div className="body-2" style={{ paddingTop: "12px" }}>
                  <p>
                    Advanced protection will ensure that your account uses the
                    highest degree of security. This means that you will only be
                    able to access your account using security keys.
                  </p>
                  <p>
                    Your account will only qualify for advanced protection once
                    two security keys have been registered.
                  </p>
                  <p>
                    Once advanced protection is enabled, your biometric based
                    passkeys will be disabled, and you will NOT be able to use
                    them to authenticate.
                  </p>
                  <p>
                    Advanced protection can be disabled at any time. Once
                    disabled, your biometric based passkeys will be reenabled.
                  </p>
                </div>
              </div>
            </Col>
            <Col lg={2} xs={12}>
              <label className="switch">
                <input
                  disabled={!advancedProtectionEligable}
                  type="checkbox"
                  checked={hasAdvancedProtection}
                  onChange={changeAdvancedProtectionStatus}
                />
                <span className="slider round"></span>
              </label>
            </Col>
          </Row>
        </div>
      </div>
    </div>
  );
}
