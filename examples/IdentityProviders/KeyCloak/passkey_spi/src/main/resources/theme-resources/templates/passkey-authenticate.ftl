<#import "template.ftl" as layout>
<@layout.registrationLayout; section>
<#if section = "title">
        title
    <#elseif section = "form">
      <form id="authenticate" action="${url.loginAction}" method="post">
        <div>
          <input type="hidden" id="assertionResult_String" name="assertionResult_String" />
          <input type="hidden" id="userHandle" name="userHandle" />
          <input type="hidden" id="action_type" name="action_type" />
        </div>
      </form>
      <div class="row" style="margin-left: 0; margin-right:0">
        <div class="col-md-5 action_parent" style="padding-right: 0; padding-left: 0">
          <div class="action splash">
            <div class="header_login">
              <h2>Welcome to PKBS</h2>
              <span class="body_2_default">Sign in or create an account to continue.</span>     
            </div>
            <#if alert_message != "">
              <div class="error-alert" id="alert_message_display">
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  width="24"
                  height="24"
                  viewBox="0 0 24 24"
                  fill="none">
                  <g clip-path="url(#clip0_11_895)">
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
                <span id="alert_message_display_content">${alert_message}</span>
              </div>
            </#if>
            <div class="username">
              <input autocomplete="username webauthn" class="username_input body_2_default" type="text" id="username_input" name="username_input" placeholder="Username" />
              <button class="button_basic" onclick="authenticateClick()">Press to authenticate</button>
              <button type="button" class="button_outlined" onclick="location.href='${url.registrationUrl}'">CREATE AN ACCOUNT</button>
            </div> 
          </div>
        </div>
        <div class="col-md-7 hero action_parent" style="padding-right: 0; padding-left: 0; background-image: linear-gradient(45deg, #0803FF 0%, #7a64ff99 100%), url('${url.resourcesPath}/img/backsplash.jpeg');">
          <div class="action splash" style="width: auto">
              <div class="header_login">
                <img class="logo_image" src="${url.resourcesPath}/img/logo.png"/>
                <h2 class="splash_logo">Passkey Banking Solution</h2>
              </div>
            </div>
        </div>
      </div>      

      <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>
      <script type="text/javascript">

        let authAbortController = new AbortController();
        
        async function mediationAvailable() {
          const pubKeyCred = window.PublicKeyCredential;
            if (
              typeof pubKeyCred.isConditionalMediationAvailable === "function" &&
              (await pubKeyCred.isConditionalMediationAvailable())
            ) {
              return true;
            } else {
              return false;
            }
        }

        async function getAssertionOptions(username) {
          try {
            const request = {
              "method": "POST",
              "headers": {
                "Content-Type": "application/json",
                "Accept": "application/json"
              },
              "body": JSON.stringify({
                userName: username
              })
            }

            const url = "${webauthnAPI}" + "/assertion/options";

            const response = await fetch(url, request);
            const assertionOptions = await response.json();

            assertionOptions.publicKey.challenge = base64urlToBuffer(
              assertionOptions.publicKey.challenge
            );

            if(assertionOptions.publicKey.allowCredentials !== undefined) {
              for(let i = 0; i < assertionOptions.publicKey.allowCredentials.length; i++) {
                assertionOptions.publicKey.allowCredentials[i] = {
                  type: assertionOptions.publicKey.allowCredentials[i].type,
                  id: base64urlToBuffer(assertionOptions.publicKey.allowCredentials[i].id)
                }
              }
            }

            return assertionOptions;
          } catch(e) {
            console.error(e);
            throw new Error("There was a problem communicating with our server");
          }
        }

        async function autofillSignIn() {
          try {
            const assertionOptions = await getAssertionOptions("");

            assertionOptions.mediation = "conditional";
            assertionOptions.signal = authAbortController.signal;

            console.log(assertionOptions);

            const assertionResult = await navigator.credentials.get(
              assertionOptions
            );

            console.log(assertionResult);

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
            authAbortController = new AbortController();
          }
        }

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


        async function authenticateClick() {
          try {

            authAbortController.abort();

            const username = document.getElementById('username_input').value;

            const assertionOptions = await getAssertionOptions(username);

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
            $("#action_type").val("${action_type}");
            $("#authenticate").submit();

          } catch(e) {
            console.error(e);
            $("error").val(e);
            autofillSignIn();
            //$("register").submit();
          }

        }
        
        autofillSignIn();
      </script>
</#if>
</@layout.registrationLayout>
