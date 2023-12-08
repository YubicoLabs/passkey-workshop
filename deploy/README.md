# Passkey workshop Docker Deployments

This directory is an alternative to the ../scripts directory and can be used to deploy the passkey workshop environment.

Differences with ../scripts (power)shell scripts:

- a single docker compose file is used to deploy the whole environment
- devtunnel can optionally be used to expose local docker containers to mobile clients

This document describes how to manually deploy the complete workshop environment, but this can also be done automatically with the deploy scripts:

- localhost.sh - for a deployment running everything on localhost
- devtunnel.sh - for a deployment running over a devtunnel (for access over the Internet using for instance a mobile phone)

## TL;DR

Detailed deployment instructions are listed below but for a quickstart, here's the TL;DR:

To deploy locally (only accessible on localhost):

1. install Docker
2. clone this repository
3. cd into /deploy
4. run the script in ./localhost.sh
5. Point your browser to http://localhost:3000/

To deploy using a tunnel (accessible over the Internet):

1. install Docker and devtunnel
2. logon to devtunnel first (`devtunnel user login`) with your Microsoft or Github account via your browser, then return back to the console
3. clone this repository
4. cd to /deploy
5. run the script in ./devtunnel.sh
6. if the script is complaining about your Apple Developer Team ID, edit the file `.env` and fill in the `DEVELOPMENT_TEAM` variable
7. run the script in ./devtunnel.sh
8. Point your browser to the devtunnel endpoint, as instructed by the script output

You can also deploy manually, as per the instructions below.

# Deploy on localhost

Deploying everything on localhost is simple, as this uses defaults for everything:

1. From a terminal window, change into de deploy directory

2. Copy the example environment file

        cp default.env .env

3. Copy all component source files

	cp -r ../examples/clients/web/react/passkey-client/ react-app/source/
	cp -r  ../examples/relyingParties/java-spring/ java-app/source/
	cp -r ../examples/IdentityProviders/KeyCloak/passkey_authenticator/ keycloak/source/

3. Build and run your docker containers:

        docker compose up -d

4. Point your browser to

	http://localhost:3000/

# Deploy using a tunnel

For mobile we need to expose the docker containers to the Internet in order to use them from a mobile phone.
This deployment is similar the localhost deployment, except that services are exposed on an HTTPS URL instead of localhost.

This is what the tunneled workshop network looks like:

![Workshop network](passkey-workshop.png)

To deploy the mobile client:

1. From a terminal window, change into de deploy directory

2. If applicable, stop and remove any running containers:

        docker compose down

3. Copy the example environment file

        cp default.env .env

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

8. Create a tunnel.

Install [devtunnel](https://learn.microsoft.com/en-gb/azure/developer/dev-tunnels/get-started), and sign in using your github or Microsoft account:

	devtunnel user login

Create the tunnel:

	devtunnel create --allow-anonymous --tags passkey-workshop --host-header unchanged --origin-header unchanged

Note the tunnel ID that is assigned, for instance `abcd1234.euw`.

9. Create the following ports:

	devtunnel port create <your tunnel id>  -p 3000 --description 'app'
	devtunnel port create <your tunnel id>  -p 8080 --description 'api'
	devtunnel port create <your tunnel id>  -p 8081 --description 'idp'

10. Edit your .env file and set the values of `RP_ID`, `RP_ALLOWED_ORIGINS`, and `RP_ALLOWED_CROSS_ORIGINS` to your assigned tunnel endpoint.

Your tunnel endpoint is be derived from your tunnel id: `<your tunnel id>.devtunnels.ms`.

```
RP_ID=your-tunnel-endpoint
RP_ALLOWED_ORIGINS=your-tunnel-endpoint
RP_ALLOWED_CROSS_ORIGINS=your-tunnel-endpoint
```

Also edit the URL for your RP backend API, your IdP, and your redirect URI so it includes your tunnel hostname:

```
REACT_APP_RP_API=https://your-tunnel-endpoint:8080/v1
REACT_APP_OIDC=https://your-tunnel-endpoint:8081/realms/passkeyDemo/protocol/openid-connect
REACT_APP_REDIRECT_URI=https://your-tunnel-endpoint:3000/oidc/callback
```

11. To run the complete environment,

run your docker containers:

        docker compose up -d

and host your tunnel:

	devtunnel host <your tunnel id>

12. Point your browser to

	https://your-tunnel-endpoint:3000/

13. Verify that everything works before proceeding with the iOS client code in XCode.

14. Start XCode with the iOS sample code in directory `../examples/clients/mobile/iOS/PawsKey`

15. In the Pawskey target, on the "Signing and Capabilities" tab, under "Signing", select your development team from the dropdown.

16. In Project Navigator, select "Configuration/Constants" and update the hostname in the `API_BASE_URI` and `RP_ID` constants to match your tunnel endpoint. For instance:

        API_BASE_URI = your-tunnel.com:8080/v1
        RP_ID = your-tunnel-endpoint.com

15. Build and run the Pawskey application on your iOS device.

# Cleaning up

When done, stop and remove all containers:

        docker compose down

Note that your assigned tunnel hostname will expire in 30 days, after which you need to create a new tunnel.

## Deleting passkeys

When done, you may want to delete all passkeys generated for your RP ID.

In Chrome on MacOS, you can delete passkeys generated locally using chrome://settings/passkeys.
Note that on MacOS, these passkeys aren't synced to your Google Account.

If you used the hybrid flow to use your Android device as an authenticator however, passkeys *are* synced to your Google Account.
To delete these passkeys using your Android device, navigate to _Settings_, _Passwords & accounts_, _Passwords_.
You will find any synced passkeys under the name of your RP ID.

Lastly, you can delete passkeys generated on your security key using chrome://settings/securityKeys (select "Sign-in data").
