---
sidebar_position: 1
---

# Getting Started

Instructions for making the local environment available for testing from your mobile client

## Prerequisites

1. Ensure you have completed the [deploy project](../deploy) section in the passkey workshop by navigating to the relying party API documentation running locally in your environment: [http://localhost:8080](http://localhost:8080)
2. An Apple Developer account with permission to create Associated Domains Entitlements
3. Install Xcode 14.3 or newer
4. Have a physical iPhone running iOS 16.4 or newer
5. YubiKey Security Key

## PawsKey iOS app

Once you have [cloned](../deploy#clone-the-repository) the passkey workshop, navigate into the Pawskey iOS project folder

```javascript
cd passkey-workshop/clients/mobile/iOS/Pawskey
```

Launch the project into Xcode:

```javascript
xed .
```

## Associated Domains

Associated domains establish a secure association between domains (secure origin-based backend endpoints like the relying party server) and your mobile app. The association between the app and your backend satisfies origin-based requirements of the WebAuthn spec that is implied when using passkeys.

Supporting Associated Domains ([docs](https://developer.apple.com/documentation/xcode/supporting-associated-domains))
Associated Domains are added through app Entitlements in your Xcode project.

To set up the entitlement in your app, open the target’s Signing & Capabilities tab in Xcode and add the Associated Domains capability. If they’re not already present, this step adds the Associated Domains Entitlement to your app and the associated domains feature to your app ID.

During Development:

a. Enable associated domain alternate mode: If your web server is unreachable from the public internet (running locally), you can use the _alternate mode_ feature to bypass the Apple CDN and connect directly to your private domain.
You enable an alternate mode by adding a query string to your associated domain’s entitlement as follows:

```bash
webcredentials:<fully qualified domain>?mode=<alternate mode>
```

![Associated Domain - Developer Mode](/img/associated-domain.png)

Tip: Create two ENTITLEMENTS for your app. One DEBUG and one RELEASE. Add the alternate mode only to the DEBUG entitlement and leave the release entitlement without any mode appended.

On your development iPhone:

b. Enable Associated Domains Development under the UNIVERSAL LINKS in the Developer settings of your iPhone/iPad. `Settings > Developer > Associated Domains Development`

![Associated Domains - Developer Mode iPhone](/img/associated-domains-devmode.png)

That’s it for setting up Associated Domains for your app.

## Backend Tunneling (Optional - During Development)

Following the getting started guide above, you should have a local deployment of the passkey workshop relying party server running on your machine and accessible through `http://localhost:3000`. To make this backend endpoint reachable from the Pawskey iOS app running on an iPhone, you can either set the local IP address of your Mac workstation in the app OR you can install and deploy a tunnel service like NGROK or Cloudflared to expose your local environment to be reachable dynamic DNS endpoint. You would then put that endpoint URL into the Pawskey app.

Here's an example for setting up `Cloudflared` service to expose your relying party localhost environment:

MacOS: Download and install cloudflared via Homebrew

```bash
$ brew install cloudflared
```

Start a tunnel using the following command

```bash
$ cloudflared tunnel --url http://localhost:3000
```

Result: You should get a reachable DNS endpoint that looks something like: `https://<some-text-here>.trycloudflare.com` that redirects to the relying party running in your localhost environment. Take note of that endpoint as we’ll be using it in our Pawskey iOS app.
