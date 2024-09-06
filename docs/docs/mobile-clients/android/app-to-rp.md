---
sidebar_position: 2
---

# Connecting to the Relying Party

Now that we know how to [deploy and run our backend](relying-party.md), let us connect our
[Android app](https://github.com/YubicoLabs/passkey-workshop/tree/main/examples/clients/mobile/android) to it.

## Deploy script

The easiest part is already done: Assuming you have `adb` on your `PATH` environment, or `ANDROID_HOME` set, the
[../../../../deploy/deploy.sh](../../../../deploy/deploy.sh) script will find it. Now you only need to connect an
Android phone, or start an emulator, and let the script do the rest for you. It'll build the source code of the app,
install it and run it. Magick.

## Manually

If you want to understand more what's happening, please open Android studio and add a new project pointing to the source
of the example: https://github.com/YubicoLabs/passkey-workshop/tree/main/examples/clients/mobile/android/pawskey .

Once you have it imported, you can follow along to see how the deploy script works and what you would need to adapt for
your own relying party.

## Docker and Devtunnel

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