---
sidebar_position: 0
---

# Getting Started Guide

This section will get you started with a running the Android Pawskey example, as soon as possible. It requires you to
have

1. a running docker installation (including a running docker virtual machine),
2. a devtunnel setup and being logged in there and
3. the ability to build Android apps through `gradle` (i.e. the `ANDROID_HOME` set to your SDK installation home.)

If you are unsure about either installation or you want to understand the setup steps needed in detail, we recommend
reading the [getting started section](getting-started.md) first.

## Set Configuration to Android

Assuming everything is setup, please checkout this passkeys repository from `github.com/yubicolabs/passkey-workshop` on
your machine and browse to the root of that repository in your `terminal` of choice.

Once arrived there, we need to change into the deploy folder like so:

```shell
cd deploy
```

Since we would like to build the Android example apps, we need to update the default configuration. Please copy the
default environment configuration file `default.env` to a new file called `.env`:

```shell
cp default.env .env
```

To update the configuration you need to open the newly created `.env` file and add `android` to the `DEPLOYMENT_CLIENTS`
and make sure that we deploy the web components to the `devtunnel` environment. Please assigning `devtunnel` to the
`DEPLOYMENT_ENVIRONMENT` configuration.

Once done, the `.env` file should contain these changes, when compared to the `default.env`:

```diff
-DEPLOYMENT_ENVIRONMENT=localhost
+DEPLOYMENT_ENVIRONMENT=devtunnel
 
-DEPLOYMENT_CLIENTS=bank,demo
+DEPLOYMENT_CLIENTS=bank,demo,android
```

## Deploy *Everything*

With the configuration changes done, a call to the main deploy script, like shown below, should do the trick. Please
connect a phone in order to automatically deploy the simple Android example App to that phone. Additionally, the script
will automatically connect your the docker services on your local machine via a tunnel through to a public, tls secured
domain we can access from the Android example app.

```shell
./deploy.sh
```

## Troubleshooting

We build the deploy script in such a way that it reports errors and missing setup on your machine in an easy to correct
manner. If you still feel stuck somewhere, or want a deeper understanding, please feel free to follow the more indepth
steps starting with the [next section](app-to-rp.md) and the sections following.
