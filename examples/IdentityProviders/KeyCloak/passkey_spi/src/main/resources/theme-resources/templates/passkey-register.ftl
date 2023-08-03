<#import "template.ftl" as layout>
<@layout.registrationLayout; section>
<#if section = "title">
        title
    <#elseif section = "header">
        <span class="${properties.kcWebAuthnKeyIcon}"></span>
        ${kcSanitize(msg("webauthn-registration-title"))?no_esc}
    <#elseif section = "form">

      <form id="register" action="${url.loginAction}" method="post">
        <div>
          <input type="hidden" id="attestationResult_String" name="attestationResult_String" />
        </div>
      </form>
      <button onclick="registerPasskey()">Register a new passkey</button>

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


        async function registerPasskey() {

          console.log("In register passkey");

          try {
          
          const requestBody = "${ATTESTATION_OPTIONS}";
          const requestBody_formatted = requestBody.replaceAll("&quot;", "\"");
          const attestationOptions = JSON.parse(requestBody_formatted);

          console.log(attestationOptions)
          
          attestationOptions.publicKey.challenge = base64urlToBuffer(
              attestationOptions.publicKey.challenge
            );

            attestationOptions.publicKey.user.id = base64urlToBuffer(
              attestationOptions.publicKey.user.id
            );

            const makeCredential = await navigator.credentials.create(
              attestationOptions
            );

            console.log(makeCredential);

            const makeCredentialResult_base64url = {
              id: makeCredential.id,
              response: {
                attestationObject: bufferToBase64url(
                  makeCredential.response.attestationObject
                ),
                clientDataJSON: bufferToBase64url(
                  makeCredential.response.clientDataJSON
                ),
              },
              type: makeCredential.type,
              clientExtensionResults: {},
            };

            console.log(makeCredentialResult_base64url);

            $("#attestationResult_String").val(JSON.stringify({
                requestId: attestationOptions.requestId,
                makeCredentialResult: makeCredentialResult_base64url
              })
            );

            $("#register").submit();

          } catch(e) {
            $("error").val(e);
            //$("register").submit();
          }

        }

      </script>

</#if>
</@layout.registrationLayout>