import { useEffect, useState } from "react";

import Loading from "../../components/loading";
import OIDCServices from "../../services/OIDCServices";

import { useSearchParams, useNavigate } from "react-router-dom";

export default function SignInCallback() {
  const [searchParams, setSearchParams] = useSearchParams();
  const code = searchParams.get("code");

  const navigate = useNavigate();

  const [loading, setLoading] = useState(false);

  const retrieveToken = async () => {
    try {
      console.warn(
        "Checking auth: " + (await OIDCServices.stillAuthenticated())
      );
      const tokensFound = await OIDCServices.retrieveAccessToken(code);

      if (tokensFound) {
        const checkingAuth = OIDCServices.stillAuthenticated();

        if (checkingAuth) {
          navigate("/");
        }
      } else {
        throw new Error("There was an issue getting your access token");
      }
    } catch (e) {
      console.log(e);
      setLoading(false);
    }
  };

  useEffect(() => {
    if (code !== "") {
      retrieveToken();
    }
  });

  return (
    <div style={{ marginTop: "5em" }}>
      <Loading loadingInfo={{ message: "Loading your profile" }} />
    </div>
  );
}
