import { useEffect } from "react";

import { useSearchParams, useNavigate } from "react-router-dom";
import Utils from "../../services/Utils";

import Spinner from "react-bootstrap/Spinner";
import AuthServices from "../../services/AuthServices";

export default function AuthCallback({ callbackInfo }) {
  const [searchParams, setSearchParams] = useSearchParams();
  const code = searchParams.get("code");

  const navigate = useNavigate();

  const retrieveToken = async () => {
    try {
      await Utils.timeoutUtil(1500);

      await AuthServices.getAccessToken("AUTH", code);

      const checkingAuth = await AuthServices.stillAuthenticated();
      if (checkingAuth) {
        navigate("/dashboard");
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
    <Spinner animation="border" role="status">
      <span className="visually-hidden">Loading...</span>
    </Spinner>
  );
}
