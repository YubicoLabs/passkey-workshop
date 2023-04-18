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

- run:

	docker compose up -d

- point your browser to

    http://localhost:3000

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

- Edit the file `react-app/source/public/.well-known` with your AppID. For instance so it reads:

```
$ cat react-app/source/public/.well-known/apple-app-site-association 
{
  "webcredentials": {
    "apps": [
      "VFQBXPXJH6.nl.joostd.pawskey"
    ]
  }
}
```

- copy the backend code

Already done above. No changes required.

- Start your tunnel:

    docker run -it --rm --network workshop --name cloudflare cloudflare/cloudflared:latest tunnel --no-autoupdate --url http://proxy

- Lookup the tunnel URL in cloudflared's output, for instance `your-proxied-tunnel-endpoint.trycloudflare.com`:

```
INF +--------------------------------------------------------------------------------------------+
INF |  Your quick Tunnel has been created! Visit it at (it may take some time to be reachable):  |
INF |  http://your-proxied-tunnel-endpoint.trycloudflare.com                                     |
INF +--------------------------------------------------------------------------------------------+
```

- Edit your .env file and set the values of `RP_ID`, `RP_ALLOWED_ORIGINS`, and `RP_ALLOWED_CROSS_ORIGINS` to your-proxied-tunnel-endpoint.trycloudflare.com:

```
RP_ID=replace-with-your-hostname.trycloudflare.com
RP_ALLOWED_ORIGINS=replace-with-your-hostname.trycloudflare.com
RP_ALLOWED_CROSS_ORIGINS=replace-with-your-hostname.trycloudflare.com
```

- run:

	docker compose up -d

- point your browser to

    https://your-proxied-tunnel-endpoint.trycloudflare.com/

- verify that everything works before proceeding with the iOS client code in XCode.

- Change the Bundle Identifier from `fyi.passkey.pawskey` to an identifier of your own

- In the Associated Domains, change `webcredentials:passkey.fyi` to `webcredentials:your-proxied-tunnel-endpoint.trycloudflare.com` (use your tunnel hostname).

- In the file `examples/clients/mobile/iOS/PawsKey/Shared/AccountManager.swift`, update the domain variable accordingly. For instance:

    let domain = "your-proxied-tunnel-endpoint.trycloudflare.com"

- In the file `examples/clients/mobile/iOS/PawsKey/Shared/RelyingParty.swift`, update the `API_ENDPOINT` accordingly. For instance:

    static let API_ENDPOINT = "https://your-proxied-tunnel-endpoint.trycloudflare.com/v1"

# Cleaning up

When done, stop and remove all containers and dispose of the images:

	docker compose down

Remember that if you change anything in the source code, you need to rebuild the corresponding image before running the containers.
