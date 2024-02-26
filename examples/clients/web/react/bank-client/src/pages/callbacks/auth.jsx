import { useEffect } from "react";

import { useSearchParams, useNavigate } from "react-router-dom";
import Utils from "../../services/Utils";

import Spinner from "react-bootstrap/Spinner";
import AuthServices from "../../services/AuthServices";

export default function AuthCallback({ callbackInfo }) {
  const [searchParams, ] = useSearchParams();
  const code = searchParams.get("code");
  const state = searchParams.get("state");

  const navigate = useNavigate();

  const retrieveToken = async () => {
    try {
      await Utils.timeoutUtil(1500);

      await AuthServices.getAccessToken("AUTH", code);

      const checkingAuth = await AuthServices.stillAuthenticated();
      if (checkingAuth) {
        if (state === "standard") {
          navigate("/dashboard");
        } else if (state === "stepup") {
          window.close();
        } else {
          navigate("/dashboard");
        }
      } else {
        throw new Error("There was an issue using your access token");
      }
    } catch (e) {
      navigate("/error");
    }
  };

  useEffect(() => {
    if (code !== "") {
      retrieveToken();
    }
  });

  return (
    <div style={{ textAlign: "center", marginTop: "24px" }}>
      <Spinner
        animation="border"
        role="status"
        size="lg"
        className="spinner-custom"></Spinner>
      <div style={{ marginTop: "24px" }}>
        <h2 className="">Loading...</h2>
      </div>
    </div>
  );
}
