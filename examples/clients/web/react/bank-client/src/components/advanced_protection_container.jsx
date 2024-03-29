import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";
import PasskeyServices from "../services/PasskeyServices";
import { useEffect, useState } from "react";
import AuthServices from "../services/AuthServices";

export default function AdvancedProtectionContainer({
  isAdvancedProtectionEligible,
  refreshCredentialListCallback,
}) {
  const [hasAdvancedProtection, setHasAdvancedProtection] = useState(false);

  /**
   * Return true if the account can enroll in AP, false otherwise
   */
  useEffect(() => {
    const checkHasAdvancedProtection = async () => {
      try {
        const status = await PasskeyServices.getAdvancedProtectionStatus(
          AuthServices.getLocalUserHandle()
        );

        setHasAdvancedProtection(status.enabled);
      } catch (e) {
        setHasAdvancedProtection(false);
      }
    };

    checkHasAdvancedProtection();
  }, []);

  const changeAdvancedProtectionStatus = async () => {
    const result = await PasskeyServices.setAdvancedProtectionStatus(
      AuthServices.getLocalUserHandle(),
      !hasAdvancedProtection
    );

    setHasAdvancedProtection(result.enabled);
    await refreshCredentialListCallback();
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
                {isAdvancedProtectionEligible ? (
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
                  disabled={!isAdvancedProtectionEligible}
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
