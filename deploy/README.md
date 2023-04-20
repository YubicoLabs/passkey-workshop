# Passkey workshop Docker Deployments

NOTE: WORK IN PROGRESS

This directory is an alternative to the ../scripts directory and can be used to deploy the passkey workshop environment.

Differences with ../scripts (power)shell scripts:

- a single docker compose file is used to deploy the whole environment
- cloudflared is used to expose local docker containers to mobile clients
- a proxy is used so that all traffic between client and RP can be routed over a single tunnel

Please note that:

- Some code changes are required as the current code often hardcodes endpoint URLs to `localhost`.
- Deployment is now partially a manual process but can be automated later.

# Deploy for web

Note that this deployment currently does not use the proxy.

To deploy the web client:

- Copy the default environment file

	cp default.env .env

- Copy the frontend code

	cp -r ../examples/clients/web/react/passkey-client/ react-app/source

- Copy the backend code

	cp -r  ../examples/relyingParties/java-spring/ java-app/source/

- Copy the passkey authenticator for keycloack

	cp ../examples/IdentityProviders/KeyCloak/pre-build/passkey_authenticator.jar keycloak/

- Build and run all containers, including the Keycloak IdP:

	docker compose --profile web up -d

- Point your browser to

	http://localhost:3000

- When done, stop and remove all containers:

	docker compose --profile web down

# Deploy for mobile

For mobile we need to expose the docker containers to the Internet in order to use them from a mobile phone.
This deployment is similar the the web-deployment, except that services are exposed on an HTTPS URL instead of localhost.

This is what the tunneled workshop network looks like:

![Workshop network](passkey-workshop.png)

Note that this also deploys the web front-end in order to demonstrate copyable passkeys.

Also note that this deployment currently does not use keycloak, so only the TestPanel can be used in the web application.

To deploy the mobile client:

- If applicable, stop and remove any running containers:

	docker compose --profile mobile --profile web down

- Copy the environment file for mobile

	cp tunnel.env .env

- Copy the frontend code

Only if not already done above.

- Edit front-end code (temporary patch, until [this PR](https://github.com/YubicoLabs/passkey-workshop/pull/19) is merged).

In the file `react-app/source/src/services/PasskeyServices.js`

Change

	const baseURl = "http://localhost:8080/v1"

to

	const baseURl = process.env.REACT_APP_API || "http://localhost:8080/v1";

- Edit the file `react-app/source/public/.well-known/apple-app-site-association` with your AppID. For instance so it reads:

```
$ cat react-app/source/public/.well-known/apple-app-site-association 
{
  "webcredentials": {
    "apps": [
      "UVWXYZ1234.com.mydomain.pawskey"
    ]
  }
}
```

where `UVWXYZ1234` is your Team ID and com.mydomain is unique for your organization.

- As the `passkey-client` source code has changed, rebuild the previously built image:

	docker compose build passkey-client

- Copy the backend code

Only if not already done above.
No changes are required.

- Start your tunnel:

	docker compose --profile tunnel up -d

- Lookup the tunnel URL in cloudflared's output, either in Docker Desktop or using:

	docker compose --profile tunnel logs

For instance, the logfile shows:

```
INF +--------------------------------------------------------------------------------------------+
INF |  Your quick Tunnel has been created! Visit it at (it may take some time to be reachable):  |
INF |  https://your-proxied-tunnel-endpoint.trycloudflare.com                                     |
INF +--------------------------------------------------------------------------------------------+
```

when you are assigned the tunnel hostname `your-proxied-tunnel-endpoint.trycloudflare.com`.

- Edit your .env file and set the values of `RP_ID`, `RP_ALLOWED_ORIGINS`, and `RP_ALLOWED_CROSS_ORIGINS` to your assigned hostname (`your-proxied-tunnel-endpoint.trycloudflare.com` in the example):

```
RP_ID=replace-with-your-hostname.trycloudflare.com
RP_ALLOWED_ORIGINS=replace-with-your-hostname.trycloudflare.com
RP_ALLOWED_CROSS_ORIGINS=replace-with-your-hostname.trycloudflare.com
```

- Also edit the URL for your RP backend API so it includes your tunnel hostname:

```
REACT_APP_API=https://replace-with-your-hostname.trycloudflare.com/v1
```

- Run:

	docker compose --profile mobile up -d

- Point your browser to

	https://your-proxied-tunnel-endpoint.trycloudflare.com/

- Verify that the testPanel works before proceeding with the iOS client code in XCode.

- Start XCode with the iOS sample code in directory `../examples/clients/mobile/iOS/PawsKey`

- Change the Bundle Identifier from `fyi.passkey.pawskey` to an identifier of your own

- In the Associated Domains Capability settings, change `webcredentials:passkey.fyi` to `webcredentials:your-proxied-tunnel-endpoint.trycloudflare.com` (use your tunnel hostname).

- In the file `examples/clients/mobile/iOS/PawsKey/Shared/AccountManager.swift`, update the domain variable accordingly. For instance:

	let domain = "your-proxied-tunnel-endpoint.trycloudflare.com"

- In the file `examples/clients/mobile/iOS/PawsKey/Shared/RelyingParty.swift`, update the `API_ENDPOINT` accordingly. For instance:

	static let API_ENDPOINT = "https://your-proxied-tunnel-endpoint.trycloudflare.com/v1"

- Build and run the Pawskey application on your iOS device.

# Cleaning up

When done, stop and remove all containers:

	docker compose --profile mobile stop
	docker compose --profile mobile rm

To also take the tunnel down, use:

	docker compose --profile tunnel down

Note that your assigned tunnel hostname will change when restarting the tunnel, so you will need to update your `.env` file and Paswkey code accordingly!
