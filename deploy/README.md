# Passkey workshop Docker Deployments

This directory is an alternative to the ../scripts directory and can be used to deploy the passkey workshop environment.

Differences with ../scripts (power)shell scripts:

- a single docker compose file is used to deploy the whole environment
- cloudflared is used to expose local docker containers to mobile clients
- a proxy is used so that all traffic between client and RP can be routed over a single tunnel

This document describes how to manually deploy the web and mobile environments, but this can be done automatically with the deploy script:

- cloudflared.sh - for a proxied web and mobile deployment running over a cloudflare tunnel

# Deploy for mobile

For mobile we need to expose the docker containers to the Internet in order to use them from a mobile phone.
This deployment is similar the the web-deployment, except that services are exposed on an HTTPS URL instead of localhost.

This is what the tunneled workshop network looks like:

![Workshop network](passkey-workshop.png)

Note that this also deploys the web front-end in order to demonstrate copyable passkeys.

Also note that this deployment currently does not use keycloak, so only the TestPanel can be used in the web application.

To deploy the mobile client:

1. From a terminal window, change into de deploy directory

2. If applicable, stop and remove any running containers:

        docker compose --profile mobile --profile web down

3. Copy the example environment file

        cp cloudflared.env.example .env

4. Copy the frontend code

        cp -r ../examples/clients/web/react/passkey-client/ react-app/source

5. Edit the file `react-app/source/public/.well-known/apple-app-site-association` with your AppID. For instance so it reads:

```bash
$ cat react-app/source/public/.well-known/apple-app-site-association 
{
  "webcredentials": {
    "apps": [
      "UVWXYZ1234.fyi.passkey.pawskeyUVWXYZ1234"
    ]
  }
}
```

where `UVWXYZ1234` is your Team ID.
Read [here](https://developer.apple.com/help/account/manage-your-team/locate-your-team-id/) how to locate your Team ID.

6. As the `passkey-client` source code has changed, rebuild the previously built image:

        docker compose build passkey-client

7. Copy the backend code

        cp -r  ../examples/relyingParties/java-spring/ java-app/source/

No changes are required.

8. Start your tunnel:

        docker compose --profile tunnel up -d

9. Lookup the tunnel URL in cloudflared's output, either in Docker Desktop or using:

        docker compose --profile tunnel logs

For instance, the logfile shows:

```
INF +--------------------------------------------------------------------------------------------+
INF |  Your quick Tunnel has been created! Visit it at (it may take some time to be reachable):  |
INF |  https://your-proxied-tunnel-endpoint.trycloudflare.com                                     |
INF +--------------------------------------------------------------------------------------------+
```

when you are assigned the tunnel hostname `your-proxied-tunnel-endpoint.trycloudflare.com`.

10. Edit your .env file and set the values of `RP_ID`, `RP_ALLOWED_ORIGINS`, and `RP_ALLOWED_CROSS_ORIGINS` to your assigned hostname (`your-proxied-tunnel-endpoint.trycloudflare.com` in the example):

```
RP_ID=replace-with-your-hostname.trycloudflare.com
RP_ALLOWED_ORIGINS=replace-with-your-hostname.trycloudflare.com
RP_ALLOWED_CROSS_ORIGINS=replace-with-your-hostname.trycloudflare.com
```

Also edit the URL for your RP backend API, your IdP, and your redirect URI so it includes your tunnel hostname:

```
REACT_APP_API=https://replace-with-your-hostname.trycloudflare.com/v1
REACT_APP_OIDC=https://replace-with-your-hostname.trycloudflare.com/realms/passkeyDemo/protocol/openid-connect
REACT_APP_REDIRECT_URI=https://replace-with-your-hostname.trycloudflare.com/oidc/callback
```

11. Run:

        docker compose --profile mobile up -d

12. Point your browser to

	https://your-proxied-tunnel-endpoint.trycloudflare.com/

13. Verify that the testPanel works before proceeding with the iOS client code in XCode.

14. Start XCode with the iOS sample code in directory `../examples/clients/mobile/iOS/PawsKey`

15. In the Pawskey target, on the "Signing and Capabilities" tab, under "Signing", select your development team from the dropdown.

16. In Project Navigator, select "Configuration/Constants" and update the hostname in the `API_BASE_URI` and `RP_ID` constants to match your cloudflared tunnel endpoint. For instance:

        API_BASE_URI = your-proxied-tunnel-endpoint.trycloudflare.com/v1
        RP_ID = your-proxied-tunnel-endpoint.trycloudflare.com

15. Build and run the Pawskey application on your iOS device.

# Cleaning up

When done, stop and remove all containers:

        docker compose --profile mobile stop
        docker compose --profile mobile rm

To also take the tunnel down, use:

        docker compose --profile tunnel down

Note that your assigned tunnel hostname will change when restarting the tunnel, so you will need to update your `.env` file and Paswkey code accordingly!

## Deleting passkeys

As you are unlikely to be assigned the same tunnel hostname twice, you may want to delete all passkeys generated using that hostname as the RP ID.

In Chrome on MacOS, you can delete passkeys generated locally using chrome://settings/passkeys.
Note that on MacOS, these passkeys aren't synced to your Google Account.

If you used the hybrid flow to use your Android device as an authenticator however, passkeys *are* synced to your Google Account.
To delete these passkeys using your Android device, navigate to _Settings_, _Passwords & accounts_, _Passwords_.
You will find any synced passkeys under trycloudflare.com.

Lastly, you can delete passkeys generated on your security key using chrome://settings/securityKeys (select "Sign-in data").
