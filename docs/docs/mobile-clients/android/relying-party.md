---
sidebar_position: 1
---

# Relying Party

We will cover the backend part, called relying party, only briefly and for a more indepth discussion, please follow
the [Relying Party](/passkey-workshop/docs/category/relying-party) section.

We'll use the Relying Party as an API to register a new user using their Yubikey. The idea is that we build an Android
App that communicates via REST to the Relying Party and the Yubikey to register a new user.

In the later section [banking app example](advanced/getting-started.md) we will use that basis for a more complex
banking example, complete with several activities, user flows and more.

In order to build the connection, we need to understand how to build the Relying Party backend service, make it run, and
connect the Android App to it.

<img
src={require('/img/mobile/android/relying-party-architecture.jpg').default}
alt="Relying Party Architectural Diagram."
style={{ width: "100%" }}
/>

## Deploying the Relying Party

Summarizing from [Relying Party](/passkey-workshop/docs/category/relying-party) what we need to start the backend, is to
execute the `./deploy.sh` (or `./deploy.ps1` on windows) script from the `deploy` folder.

To call the deploy script, you need `docker` and a `virtual machine`. Either can be fulfilled by either
installing [docker desktop](https://docs.docker.com/desktop/)
or [docker with compose](https://docs.docker.com/compose/install/) and [podman](https://podman.io/docs/installation).
Please see the appropriate documentation, since this will not be covered here.

:::tip Running Virtual Machines
Please remember to run a virtual machine for docker, i.e. `podman machine start` if you receive errors like
```Cannot connect to the Docker daemon at unix:///var/run/docker.sock.```
:::

Running the deploy script will start the server on the localhost using http. Feel free to explore the web frontend
running on your machine here [http://localhost:3000](http://localhost:3000). This will be the part we are reimplementing
in Android, so feel free to click around, register and get familiar.

## Android and Secure HTTP Traffic

Since [Android 9 (API Level 27)](https://developer.android.com/privacy-and-security/security-config#CleartextTrafficPermitted)
all internet traffic needs to be secured from eavesdropping using secure transport. This means we cannot use our local
server running on `localhost` to connect to our Android App. So we need to secure our traffic.

The solution is to use a service like [devtunnel](https://learn.microsoft.com/en-gb/azure/developer/dev-tunnels/) to
expose a local web service to the internet.

### Installation of devtunnel

Please install devtunnel like so:

* `brew install devtunnel` (mac) or
* `curl -sL https://aka.ms/DevTunnelCliInstall | bash` (linux) or
* `winget install Microsoft.devtunnel` (windows)

Once installed, we need to tell the deploy script to use devtunnel. Please copy the [default.enf](/deploy/default.env)
default configuration file to [.env](/deploy/.env) file in the deploy folder. Now that we have copied the default
configuration file, please update this line

```
DEPLOYMENT_ENVIRONMENT=localhost
```

to set the deployment environment to be the devtunnel:

```
DEPLOYMENT_ENVIRONMENT=devtunnel
```

:::tip
Please remember to login to devtunnel: `devtunnel user login`. (Using 'other' for the microsoft login, you can use
GitHub and will not need to create a new microsoft account.)
:::

To start the deployment to the devtunnel, please execute the `./deploy.sh` (mac or linux) or `./deploy.ps1`(windows).

## Validating deployment

If everything went smoothly, you should see an output similar to

```shell
[...]
your demo application will be deployed here:

 https://XXXXXXXXXXXXXX-3000.euw.devtunnels.ms/test_panel 

your bank application will be deployed here:

 https://XXXXXXXXXXXXXX-3002.euw.devtunnels.ms/ 

### starting devtunnel. Type ^C to stop the tunnel and take down all containers
```

To verify, let us browse to the status page of the just deployed webservice. Therefore, you need to take the url the
devtunnel is running on and update it. From the example above, please take the
``` https://XXXXXXXXXXXXXX-3000.euw.devtunnels.ms/test_panel``` url and replace the `3000` with the port the service is
running on, `8080` in our case.

You should be able to visit the relying party running on  ```https://XXXXXXXXXXXXXX-8080.euw.devtunnels.ms/``` (please
replace the X with your endpoint) and be greeted with a similar image then this screenshot:

<img
src={require('/img/mobile/android/relying-party-status-hint.png').default}
alt="Android Studio: Menu new project."
style={{ width: "50%" }}
/>

The security check is to make sure that we know what we are doing, and since the url is under our control it is fine to
accept the connection and hit `continue`.

If everything is running as planned, you will see a message like this

```json
{
  "status": "ok"
}
```

This means our local server is available through the devtunnel, and works as expected. You can now also browse through
the other components, securely transmitting data from your local machine through the tunnel to the local browser.

## Next Steps

That was fun, wasn't it. Now that we have a local webserver accessible through the devtunnel, we can start implementing
an android app that connects to it. Therefore, in the [next section](authentication.md) we will combine this sections
backend with our [previously built](getting-started.md) Android app.
