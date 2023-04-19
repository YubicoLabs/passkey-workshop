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

- copy the default environment file

	cp default.env .env

- copy the frontend code

	cp -r ../examples/clients/web/react/passkey-client/ react-app/source

- copy the backend code

	cp -r  ../examples/relyingParties/java-spring/ java-app/source/

- copy the passkey authenticator for keycloack

	cp ../examples/IdentityProviders/KeyCloak/pre-build/passkey_authenticator.jar keycloak/

- build and run all containers, including the Keycloak IdP:

	docker compose --profile web up -d

- point your browser to

	http://localhost:3000

- when done, stop and remove all containers:

	docker compose --profile web down

# Deploy for mobile

Note that this deployment currently does not use keycloak, so only the TestPanel can be used on the proxy.

- copy the environment file for mobile

	cp tunnel.env .env

- copy the frontend code

Already done above.

- edit front-end code

In the file `react-app/source/src/services/PasskeyServices.js`

Change

	const baseURl = "http://localhost:8080/v1"

to

	const baseURl = `${window.location.origin.toString()}/v1`;

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

where `UVWXYZ1234` is your Team ID and com.mydomain is unique for your organisation.

- copy the backend code

Already done above. No changes required.

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

when your are assigned the tunnel hostname `your-proxied-tunnel-endpoint.trycloudflare.com`.

- Edit your .env file and set the values of `RP_ID`, `RP_ALLOWED_ORIGINS`, and `RP_ALLOWED_CROSS_ORIGINS` to your assigned hostname (`your-proxied-tunnel-endpoint.trycloudflare.com` in the example):

```
RP_ID=replace-with-your-hostname.trycloudflare.com
RP_ALLOWED_ORIGINS=replace-with-your-hostname.trycloudflare.com
RP_ALLOWED_CROSS_ORIGINS=replace-with-your-hostname.trycloudflare.com
```

- As the `passkey-client` source code has changed, rebuild the previously built image:

	docker compose build passkey-client

- run:

	docker compose --profile mobile up -d

- point your browser to

	https://your-proxied-tunnel-endpoint.trycloudflare.com/

- verify that everything works before proceeding with the iOS client code in XCode.

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
