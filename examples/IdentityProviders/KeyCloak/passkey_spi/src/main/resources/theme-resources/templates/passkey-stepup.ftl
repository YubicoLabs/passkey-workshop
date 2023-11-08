<#import "template.ftl" as layout>
<@layout.registrationLayout; section>
<#if section = "title">
        title
    <#elseif section = "form">
      <form id="authenticate" action="${url.loginAction}" method="post">
        <div>
          <input type="hidden" id="assertionResult_String" name="assertionResult_String" />
          <input type="hidden" id="userHandle" name="userHandle" />
        </div>
      </form>
      <div class="row" style="margin-left: 0; margin-right:0">
        <div class="col-md-5 action_parent" style="padding-right: 0; padding-left: 0">
          <div class="action splash">
            <div class="header_login">
              <h2>MSBS Step-up Authentication</h2>
              <span class="body_2_default">You are attempting to perform a sensative action. Please use your security key so that we can be sure it's you!</span>     
            </div>
            <div class="username">
              <button class="button_basic" onclick="authenticateClick()">UNLOCK WITH SECURITY KEY</button>
            </div> 
          </div>
        </div>
        <div class="col-md-7 hero action_parent" style="padding-right: 0; padding-left: 0; background-image: linear-gradient(45deg, #0803FF 0%, #7a64ff99 100%), url('${url.resourcesPath}/img/backsplash.jpeg');">
          <div class="action splash" style="width: auto">
              <div class="header_login">
                <img class="logo_image" src="${url.resourcesPath}/img/logo.png"/>
                <h2 class="splash_logo">Morning Star Banking Solution</h2>
              </div>
            </div>
        </div>
      </div>      

      <script type="text/javascript" src="${url.resourcesCommonPath}/node_modules/jquery/dist/jquery.min.js"></script>
      <script type="text/javascript">

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
            const username = "${username}";
            console.log("Attempting to auth: " + username);

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
            $("#authenticate").submit();

          } catch(e) {
            console.error(e);
            $("error").val(e);
            autofillSignIn();
            //$("register").submit();
          }

        }
        
      </script>
</#if>
</@layout.registrationLayout>
