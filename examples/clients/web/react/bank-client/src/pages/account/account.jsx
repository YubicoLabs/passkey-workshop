import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";
import PasskeyContainer from "../../components/passkey_container";
import AdvancedProtectionContainer from "../../components/advanced_protection_container";
import PageSideNav from "../../components/page-side-nav";
import LogoutContainer from "../../components/logout_container";
import { useEffect, useState } from "react";

import PasskeyServices from "../../services/PasskeyServices";
import AuthServices from "../../services/AuthServices";

export default function Account() {
  const pageSideNavProps = [{ name: "SECURITY", link: "/account" }];
  const [passkeyList, setPasskeyList] = useState([]);
  const [isAdvancedProtectionEligible, setIsAdvancedProtectionEligible] =
    useState(false);

  /**
   * Call this method to refresh the current credential list
   * Based on the user's username
   */
  const refreshCredentialList = async () => {
    try {
      const credList = await PasskeyServices.getCredentials(
        AuthServices.getLocalUsername()
      );
      setPasskeyList(credList.credentials);
    } catch (e) {
      console.error("Could not receive credential list");
      setPasskeyList([]);
    }
  };

  const checkAdvancedProtectionEligibility = async () => {
    try {
      const passkeyList = await PasskeyServices.getCredentials(
        AuthServices.getLocalUsername()
      );

      let highAssuranceCount = 0;
      passkeyList.credentials.forEach((element) => {
        if (element.isHighAssurance) {
          highAssuranceCount++;
        }
      });

      if (highAssuranceCount >= 2) {
        setIsAdvancedProtectionEligible(true);
      } else {
        setIsAdvancedProtectionEligible(false);
      }
    } catch (e) {
      setIsAdvancedProtectionEligible(false);
    }
  };

  useEffect(() => {
    const refreshCredentialList_useEffect = refreshCredentialList;

    refreshCredentialList_useEffect();
  }, []);

  useEffect(() => {
    checkAdvancedProtectionEligibility();
  }, [passkeyList]);

  return (
    <Row>
      <Col md={12} lg={2} style={{ paddingBottom: "2em" }}>
        <PageSideNav title="Settings" linkObjects={pageSideNavProps} />
      </Col>
      <Col md={12} lg={9}>
        <div className="account-balance-parent">
          <h2>Account Settings</h2>
          <PasskeyContainer
            passkeyList={passkeyList}
            username={AuthServices.getLocalUsername()}
            refreshCredentialListCallback={refreshCredentialList}
          />
          <AdvancedProtectionContainer
            isAdvancedProtectionEligible={isAdvancedProtectionEligible}
            refreshCredentialListCallback={refreshCredentialList}
          />
          <LogoutContainer />
        </div>
      </Col>
    </Row>
  );
}
