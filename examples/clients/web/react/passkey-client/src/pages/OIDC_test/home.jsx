import { useEffect, useState } from "react";
import Button from "react-bootstrap/Button";
import { useNavigate } from "react-router-dom";
import { get } from "@github/webauthn-json";

import PasskeyServices from "../../services/PasskeyServices";

export default function HomeOIDC() {
  const [assertionResult, setAssertionResult] = useState("");

  const submitForm = async (e) => {
    e.preventDefault();
    console.log(e);
    const assertionOptions = await PasskeyServices.getAssertionOptions("");

    const assertionResult = await get(assertionOptions);

    const reqData = {
      requestId: assertionOptions.requestId,
      assertionResult: assertionResult,
    };

    const assertionJSON = JSON.stringify(reqData);

    setAssertionResult(assertionJSON);
  };

  useEffect(() => {
    if (assertionResult !== "") {
      document.getElementById("passkeyForm").requestSubmit();
    }
  }, [assertionResult]);

  return (
    <div>
      <form
        action={
          "http://localhost:8081/realms/normal/protocol/openid-connect/auth"
        }
        method="post"
        id="passkeyForm">
        <label hidden>
          client_id
          <input value="my_custom_client2" name="client_id" />
        </label>
        <label hidden>
          redirect_uri
          <input
            value="http://localhost:3000/oidc/callback"
            name="redirect_uri"
          />
        </label>
        <label hidden>
          scope
          <input value="openid" name="scope" />
        </label>
        <label hidden>
          response_type
          <input value="code" name="response_type" />
        </label>
        <label>
          username
          <input value="cody1" name="username" />
        </label>
        <label hidden>
          <input name="assertionResult" value={assertionResult} />
        </label>
      </form>
      <input type="submit" value="Submit" onClick={submitForm} />
    </div>
  );
}
