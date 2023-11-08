<#import "template.ftl" as layout>
<@layout.registrationLayout; section>
<#if section = "title">
        title
    <#elseif section = "form">
      <form id="register" action="${url.loginAction}" method="post">
        <div>
          <input type="hidden" id="attestationResult_String" name="attestationResult_String" />
          <input type="hidden" id="username" name="username" />
          <input type="hidden" id="userHandle" name="userHandle" />
          <input type="hidden" id="action_type" name="action_type" />
        </div>
      </form>

      <div id="modal_success" class="modal">
        <!-- Modal content -->
        <div class="modal-content">
          <div class="row">
            <svg xmlns="http://www.w3.org/2000/svg" width="73" height="72" viewBox="0 0 73 72" fill="none">
              <g clip-path="url(#clip0_518_3955)">
                <path d="M36.5 6C19.94 6 6.5 19.44 6.5 36C6.5 52.56 19.94 66 36.5 66C53.06 66 66.5 52.56 66.5 36C66.5 19.44 53.06 6 36.5 6ZM36.5 60C23.27 60 12.5 49.23 12.5 36C12.5 22.77 23.27 12 36.5 12C49.73 12 60.5 22.77 60.5 36C60.5 49.23 49.73 60 36.5 60ZM50.27 22.74L30.5 42.51L22.73 34.77L18.5 39L30.5 51L54.5 27L50.27 22.74Z" fill="#6EC500"/>
              </g>
              <defs>
                <clipPath id="clip0_518_3955">
                  <rect width="72" height="72" fill="white" transform="translate(0.5)"/>
                </clipPath>
              </defs>
            </svg>
          </div>
          <div class="row">
            <div style="width: 100%">
              <h3 style="margin-bottom: auto;">Success!</h3>
            </div>
            <div style="width: 100%; text-align: center;">
              <span class="body_1_default">Passkey created successfully!</span>
            </div>
          </div>
          <div class="row">
            <button onclick="submitForm()" class="button_basic">Continue</button>
          </div>
        </div>

      </div>

      <div id="modal_error" class="modal">
        <!-- Modal content -->
        <div class="modal-content">
          <div class="row">
            <svg xmlns="http://www.w3.org/2000/svg" width="60" height="60" viewBox="0 0 60 60" fill="none">
              <path fill-rule="evenodd" clip-rule="evenodd" d="M0 30C0 13.44 13.44 0 30 0C46.56 0 60 13.44 60 30C60 46.56 46.56 60 30 60C13.44 60 0 46.56 0 30ZM6 30C6 43.23 16.77 54 30 54C43.23 54 54 43.23 54 30C54 16.77 43.23 6 30 6C16.77 6 6 16.77 6 30ZM38.0572 18L42 21.9427L33.9427 30L42 38.0572L38.0573 42L30 33.9427L21.9427 42L18 38.0572L26.0573 30L18 21.9427L21.9427 18L30 26.0572L38.0572 18Z" fill="#FF4445"/>
            </svg>
          </div>
          <div class="row">
            <div style="width: 100%">
              <h3 style="margin-bottom: auto;">Failed</h3>
            </div>
            <div style="width: 100%; text-align: center;">
              <span class="body_1_default">Passkey creation failed. Please try again</span>
            </div>
          </div>
          <div class="row">
            <button onclick="modalClose('modal_error')" class="button_basic">Close</button>
          </div>
        </div>

      </div>

      <div class="action" style="width: 90%;">
        <div class="header_login">
          <h2>Add a new passkey</h2>
          <div class="collapsible collapse_div" onclick="triggerCollapse()">
            <span class="body_2_default collapse_text">What is a passkey?</span> 
            <svg id="collapse_arrow" xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none">
              <path d="M16.59 8.58984L12 13.1698L7.41 8.58984L6 9.99984L12 15.9998L18 9.99984L16.59 8.58984Z" fill="#F2F0FF"/>
            </svg>
          </div>
          <div id="passkey_info" class="collapsible_content">
            <div class="collapsible_content_inner">
              <div class="collapsible_content_inner_item">
                <span class="body_1_bold">Why should I use passkeys?</span>
                <span class="body_1_default">With passkeys, you don't need to remember complex passwords.</span>
              </div>
              <div class="collapsible_content_inner_item">
                <span class="body_1_bold">What are passkeys?</span>
                <span class="body_1_default" >Passkeys are encrypted digital keys you create using your fingerprint, face, or screen lock.</span>            
              </div>
              <div class="collapsible_content_inner_item">
                <span class="body_1_bold">Where are passkeys saved?</span>
                <span class="body_1_default">Passkeys are saved to your password manager, so you can sign in on other devices</span>              
              </div>
            </div>
          </div> 
        </div>
          <div class="row" style="row-gap: 48px; width: 100%;">
            <div class="col-sm-12 col-md-5">
              <div class="register_option">
                <div class="register_option_inner">
                  <div class="register_option_inner_heading">
                    <svg xmlns="http://www.w3.org/2000/svg" width="40" height="40" viewBox="0 0 40 40" fill="none">
                      <path d="M29.6832 7.45001C29.5499 7.45001 29.4166 7.41668 29.2999 7.35001C26.0999 5.70001 23.3332 5.00001 20.0166 5.00001C16.7166 5.00001 13.5832 5.78334 10.7332 7.35001C10.3332 7.56668 9.83323 7.41668 9.59989 7.01668C9.38323 6.61668 9.53323 6.10001 9.93323 5.88334C13.0332 4.20001 16.4332 3.33334 20.0166 3.33334C23.5666 3.33334 26.6666 4.11668 30.0666 5.86668C30.4832 6.08334 30.6332 6.58334 30.4166 6.98334C30.2666 7.28334 29.9832 7.45001 29.6832 7.45001ZM5.83323 16.2C5.66656 16.2 5.49989 16.15 5.34989 16.05C4.96656 15.7833 4.88323 15.2667 5.14989 14.8833C6.79989 12.55 8.89989 10.7167 11.3999 9.43334C16.6332 6.73334 23.3332 6.71668 28.5832 9.41668C31.0832 10.7 33.1832 12.5167 34.8332 14.8333C35.0999 15.2 35.0166 15.7333 34.6332 16C34.2499 16.2667 33.7332 16.1833 33.4666 15.8C31.9666 13.7 30.0666 12.05 27.8166 10.9C23.0332 8.45001 16.9166 8.45001 12.1499 10.9167C9.88323 12.0833 7.98323 13.75 6.48323 15.85C6.34989 16.0833 6.09989 16.2 5.83323 16.2ZM16.2499 36.3167C16.0332 36.3167 15.8166 36.2333 15.6666 36.0667C14.2166 34.6167 13.4332 33.6833 12.3166 31.6667C11.1666 29.6167 10.5666 27.1167 10.5666 24.4333C10.5666 19.4833 14.7999 15.45 19.9999 15.45C25.1999 15.45 29.4332 19.4833 29.4332 24.4333C29.4332 24.9 29.0666 25.2667 28.5999 25.2667C28.1332 25.2667 27.7666 24.9 27.7666 24.4333C27.7666 20.4 24.2832 17.1167 19.9999 17.1167C15.7166 17.1167 12.2332 20.4 12.2332 24.4333C12.2332 26.8333 12.7666 29.05 13.7832 30.85C14.8499 32.7667 15.5832 33.5833 16.8666 34.8833C17.1832 35.2167 17.1832 35.7333 16.8666 36.0667C16.6832 36.2333 16.4666 36.3167 16.2499 36.3167ZM28.1999 33.2333C26.2166 33.2333 24.4666 32.7333 23.0332 31.75C20.5499 30.0667 19.0666 27.3333 19.0666 24.4333C19.0666 23.9667 19.4332 23.6 19.8999 23.6C20.3666 23.6 20.7332 23.9667 20.7332 24.4333C20.7332 26.7833 21.9332 29 23.9666 30.3667C25.1499 31.1667 26.5332 31.55 28.1999 31.55C28.5999 31.55 29.2666 31.5 29.9332 31.3833C30.3832 31.3 30.8166 31.6 30.8999 32.0667C30.9832 32.5167 30.6832 32.95 30.2166 33.0333C29.2666 33.2167 28.4332 33.2333 28.1999 33.2333ZM24.8499 36.6667C24.7832 36.6667 24.6999 36.65 24.6332 36.6333C21.9832 35.9 20.2499 34.9167 18.4332 33.1333C16.0999 30.8167 14.8166 27.7333 14.8166 24.4333C14.8166 21.7333 17.1166 19.5333 19.9499 19.5333C22.7832 19.5333 25.0832 21.7333 25.0832 24.4333C25.0832 26.2167 26.6332 27.6667 28.5499 27.6667C30.4666 27.6667 32.0166 26.2167 32.0166 24.4333C32.0166 18.15 26.5999 13.05 19.9332 13.05C15.1999 13.05 10.8666 15.6833 8.91656 19.7667C8.26656 21.1167 7.93323 22.7 7.93323 24.4333C7.93323 25.7333 8.04989 27.7833 9.04989 30.45C9.21656 30.8833 8.99989 31.3667 8.56656 31.5167C8.13323 31.6833 7.64989 31.45 7.49989 31.0333C6.68323 28.85 6.28323 26.6833 6.28323 24.4333C6.28323 22.4333 6.66656 20.6167 7.41656 19.0333C9.63323 14.3833 14.5499 11.3667 19.9332 11.3667C27.5166 11.3667 33.6832 17.2167 33.6832 24.4167C33.6832 27.1167 31.3832 29.3167 28.5499 29.3167C25.7166 29.3167 23.4166 27.1167 23.4166 24.4167C23.4166 22.6333 21.8666 21.1833 19.9499 21.1833C18.0332 21.1833 16.4832 22.6333 16.4832 24.4167C16.4832 27.2667 17.5832 29.9333 19.5999 31.9333C21.1832 33.5 22.6999 34.3667 25.0499 35.0167C25.4999 35.1333 25.7499 35.6 25.6332 36.0333C25.5499 36.4167 25.1999 36.6667 24.8499 36.6667Z" fill="white"/>
                    </svg>
                    <h3>Register using device biometrics</h3>
                  </div>
                  <div>
                    <span class="body_2_default">Blurb here about what security keys are. <br/> What to expect from the registration process.</span>
                  </div>
                  <button class="button_secondary" onclick="registerPasskey('platform')">REGISTER WITH BIOMETRICS</button>
                </div>
              </div>  
            </div>
            <div class="col-sm-12 col-md-2">
              <div>
                <h2>Or</h2>
              </div>
            </div>
            <div class="col-sm-12 col-md-5">
              <div class="register_option">
                <div class="register_option_inner">
                  <div class="register_option_inner_heading">
                    <svg xmlns="http://www.w3.org/2000/svg" width="40" height="40" viewBox="0 0 40 40" fill="none">
                      <path d="M34.9998 16.6667H21.0832C19.7165 12.7833 16.0165 10 11.6665 10C6.14984 10 1.6665 14.4833 1.6665 20C1.6665 25.5167 6.14984 30 11.6665 30C16.0165 30 19.7165 27.2167 21.0832 23.3333H21.6665L24.9998 26.6667L28.3332 23.3333L31.6665 26.6667L38.3332 19.9333L34.9998 16.6667ZM11.6665 25C8.9165 25 6.6665 22.75 6.6665 20C6.6665 17.25 8.9165 15 11.6665 15C14.4165 15 16.6665 17.25 16.6665 20C16.6665 22.75 14.4165 25 11.6665 25Z" fill="white"/>
                    </svg>
                    <h3>Register using a security key</h3>
                  </div>
                  <div>
                    <span class="body_2_default">Blurb here about what security keys are. <br/> What to expect from the registration process.</span>
                  </div>
                  <button class="button_secondary" onclick="registerPasskey('cross-platform')">REGISTER WITH SECURITY KEY</button>
                </div>
              </div>
            </div>
          </div> 
      </div>

      <script type="text/javascript" src="${url.resourcesCommonPath}/node_modules/jquery/dist/jquery.min.js"></script>
      <script type="text/javascript">

        function submitForm() {
            $("#register").submit();
        };

        function sleep(ms) {
          return new Promise(resolve => setTimeout(resolve, ms));
        }

        function modalOpen(modal_type) {
          var element = document.getElementById(modal_type);
          element.style.display = "block";
        }

        function modalClose(modal_type) {
          var element = document.getElementById(modal_type);
          element.style.display = "none";
        }

        async function triggerCollapse() {
          var element = document.getElementById("passkey_info");
          var element_svg = document.getElementById("collapse_arrow");

          element.classList.toggle("active");
          if(element.style.maxHeight) {
            element.style.maxHeight = null;
            await sleep(150);
            element.style.display = "none";
            element_svg.innerHTML = '<path d="M16.59 8.58984L12 13.1698L7.41 8.58984L6 9.99984L12 15.9998L18 9.99984L16.59 8.58984Z" fill="#F2F0FF"/>'

          } else {
            element.style.display = "block";
            element.style.maxHeight = element.scrollHeight + "px";
            element_svg.innerHTML = '<path d="M12 8L6 14L7.41 15.41L12 10.83L16.59 15.41L18 14L12 8Z" fill="#F2F0FF"/>';
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


        async function registerPasskey(authenticatorAttachment) {
          try {

            const request = {
              "method": "POST",
              "headers": {
                "Content-Type": "application/json",
                "Accept": "application/json"
              },
              "body": JSON.stringify({
                userName: "${username}",
                displayName: "${username}",
                authenticatorSelection: {
                  residentKey: "preferred",
                  authenticatorAttachment: authenticatorAttachment,
                  userVerification: "required"
                },
                attestation: "direct"
              })
            }

            const url = "${webauthnAPI}" + "/attestation/options";

            const response = await fetch(url, request);
            const attestationOptions = await response.json();
          

          console.log(attestationOptions)

          const userHandle = attestationOptions.publicKey.user.id;
          
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

            $("#username").val("${username}");
            $("#userHandle").val(userHandle);
            $("#action_type").val("${action_type}");

            modalOpen("modal_success");

          } catch(e) {
            console.error(e);
            modalOpen("modal_error");
            $("error").val(e);
            //$("register").submit();
          }

        }

      </script>

</#if>
</@layout.registrationLayout>
