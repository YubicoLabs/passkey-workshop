<#import "template.ftl" as layout>
<@layout.registrationLayout; section>
<#if section = "title">
        title
    <#elseif section = "header">
        <span class="${properties.kcWebAuthnKeyIcon}"></span>
        ${kcSanitize(msg("webauthn-registration-title"))?no_esc}
    <#elseif section = "form">

      <form id="authenticate" action="${url.loginAction}" method="post">
        <div>
          <input type="hidden" id="assertionResult_String" name="assertionResult_String" />
          <input type="hidden" id="userHandle" name="userHandle" />
        </div>
      </form>
      <button onclick="authenticatePasskey()">Press to authenticate</button>

      <script type="text/javascript" src="${url.resourcesCommonPath}/node_modules/jquery/dist/jquery.min.js"></script>
      <script type="text/javascript">

        function base64urlToBuffer(baseurl64String) {
          // Base64url to Base64
          const padding = "==".slice(0, (4 - (baseurl64String.length % 4)) % 4);
          const base64String =
            baseurl64String.replace(/-/g, "+").replace(/_/g, "/") + padding;

          // Base64 to binary string
          const str = atob(base64String);

          // Binary string to buffer
          const buffer = new ArrayBuffer(str.length);
          const byteView = new Uint8Array(buffer);
          for (let i = 0; i < str.length; i++) {
            byteView[i] = str.charCodeAt(i);
          }
          return buffer;
        }

        function bufferToBase64url(buffer) {
          // Buffer to binary string
          const byteView = new Uint8Array(buffer);
          let str = "";
          for (const charCode of byteView) {
            str += String.fromCharCode(charCode);
          }

          // Binary string to base64
          const base64String = btoa(str);

          // Base64 to base64url
          // We assume that the base64url string is well-formed.
          const base64urlString = base64String
            .replace(/\+/g, "-")
            .replace(/\//g, "_")
            .replace(/=/g, "");
          return base64urlString;
        }


        async function authenticatePasskey() {
          try {

            const request = {
              "method": "POST",
              "headers": {
                "Content-Type": "application/json",
                "Accept": "application/json"
              },
              "body": JSON.stringify({
                userName: ""
              })
            }

            const url = "http://localhost:8080/v1/assertion/options"

            const response = await fetch(url, request);
            const assertionOptions = await response.json();

            assertionOptions.publicKey.challenge = base64urlToBuffer(
              assertionOptions.publicKey.challenge
            );

            console.log(assertionOptions);

            const assertionResult = await navigator.credentials.get(
              assertionOptions
            );

            console.log(assertionResult);
            console.log(assertionResult.response.authenticatorData);

            const assertionResult_base64url = {
              id: assertionResult.id,
              response: {
                authenticatorData: bufferToBase64url(assertionResult.response.authenticatorData),
                signature: bufferToBase64url(assertionResult.response.signature),
                userHandle: bufferToBase64url(assertionResult.response.userHandle),
                clientDataJSON: bufferToBase64url(assertionResult.response.clientDataJSON)
              },
              type: assertionResult.type,
              clientExtensionResults: {},
            };

            console.log(assertionResult_base64url);

            $("#assertionResult_String").val(JSON.stringify({
                requestId: assertionOptions.requestId,
                assertionResult: assertionResult_base64url
              })
            );

            $("#userHandle").val(assertionResult_base64url.response.userHandle);

            $("#authenticate").submit();

          } catch(e) {
            console.error(e);
            $("error").val(e);
            //$("register").submit();
          }

        }

      </script>
      <div id="kc-registration">
          <span>${msg("noAccount")} <a tabindex="6" href="${url.registrationUrl}">${msg("doRegister")}</a></span>
      </div>
</#if>
</@layout.registrationLayout>