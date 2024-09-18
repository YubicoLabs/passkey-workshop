---
sidebar_position: 2
---

# Connecting to the Relying Party

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
Now that we know how to [deploy and run our backend](relying-party.md), let us connect our
[Android app](https://github.com/YubicoLabs/passkey-workshop/tree/main/examples/clients/mobile/android) to it.

## Deploy script

The deploy script sets up the backend relying party to be deployed on docker and then exposed through `devtunnel` from
your local machine to the broad internet. Using TLS it is able to allow a connection from your phone securely to your
laptop.

Once this connection is established, the script finds the Android examples and modifies the configuration
in [gradle.properties](../../../../examples/clients/mobile/android/PawsKey/gradle.properties) to reflect the newly
created tunnel. This way if the tunnel changes, a call to the deploy script will also update that configuration. You
might want to change the configuration manually to contain the endpoint your relying party is running on.

Additionally the deploy script also updates the relying party id, as in the id used to identify which credentials to
select. You will also need to update that configuration, if you update the endpoint.

Once the configuration is updated, the script builds the android examples using `gradle`. With the source, which we will
go through in detail soon, we try to reflect current best practices and will not emphasize common ways of working.
Please follow linked literature if you want to get up to speed in everything from MVVM[1], Jetpack Compose[2] and DI
using Hilt[3].

## Dependencies

Additionally, the mentioned libraries and architectures the android examples depend on retrofit and kotlinx
serialization: Those are used in order to communicate with the relying
parties: [RelyingPartyService.kt](../../../../examples/clients/mobile/android/PawsKey/app/src/main/java/io/yubicolabs/pawskey/RelyingPartyService.kt)
implements the Retrofit Service to communicate with the relying
party. [PassKeyService.kt](../../../../examples/clients/mobile/android/PawsKey/app/src/main/java/io/yubicolabs/pawskey/PassKeyService.kt)
is used for communicating with the passkeys, may it be from the platform perspective or the Yubico SDK point of view.

All of this is tied together by the [MainViewModel.kt][5], that abstracts from the service implementations to expose the
view relevant details. Finally, the [MainActivity.kt][4] displays relevant details to the user. And gets called when she
wants to sign in / log in or interact otherwise with the app.

## Hello Relying Party Server

Let us follow the flow of the example app to communicate with the relying party with the example of requesting a status
from it's `/v1/status` endpoint. Once the user starts the app, the [MainActivity][4] initializes and creates the
[MainViewModel][5]. Once the [MainViewModel][5] is created, it asks its [Repository for RelyingParty][6] to fetch it's
status. Fetching the status has two effects: A) we are making sure the network is working. And B) that we are connecting
to a well known backend, before we are starting more complex interactions.


[1]: https://developer.android.com/topic/architecture

[2]: https://developer.android.com/compose

[3]: https://developer.android.com/training/dependency-injection/hilt-android

[4]: ../../../../examples/clients/mobile/android/PawsKey/app/src/main/java/io/yubicolabs/pawskey/MainActivity.kt

[5]: ../../../../examples/clients/mobile/android/PawsKey/app/src/main/java/io/yubicolabs/pawskey/MainViewModel.kt

[6]: ../../../../examples/clients/mobile/android/PawsKey/app/src/main/java/io/yubicolabs/pawskey/RelyingPartyService.kt