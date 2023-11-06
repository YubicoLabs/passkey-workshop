<#import "template.ftl" as layout>
<@layout.registrationLayout; section>
<#if section = "title">
        title
    <#elseif section = "form">
      <form id="register" action="${url.loginAction}" method="post">
        <div>
          <input type="hidden" id="username" name="username" />
          <input type="hidden" id="action_type" name="action_type" />
        </div>
      </form>

      <div class="row" style="margin-left: 0; margin-right:0">
        <div class="col-md-5 action_parent" style="padding-right: 0; padding-left: 0">
          <div class="action splash">
            <div class="header_login">
              <h2>Create your account</h2>
              <span class="body_2_default">Create a free account in order to access MSBS</span>     
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
                    <input class="username_input body_2_default" type="text" id="username_input" name="username_input" placeholder="Username" />
                    <button class="button_outlined" onclick="usernameSubmit()">CREATE A PASSKEY</button>
                    <span class="body_2_default">By Creating an Account up you accept our terms of use and privacy policy.</span>
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

        async function usernameSubmit() {
          try {
            const action_type = "${action_type}";
            const username = document.getElementById("username_input").value;

            console.log(action_type + "    " + username);

            $("#action_type").val(action_type);
            $("#username").val(username);

            $("#register").submit();

          } catch(e) {
            console.error(e);
            $("error").val(e);
            //$("register").submit();
          }

        }
      </script>

</#if>
</@layout.registrationLayout>