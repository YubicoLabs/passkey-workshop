---
sidebar_position: 2
---

import ReactPlayer from "react-player";

# Client Hints

[Client hints](https://www.w3.org/TR/webauthn-3/#enum-hints) are in addition to the Level 3 version of the WebAuthn specification that aims to improve UX flows to aid in the adoption of passkeys. Hints provide a relying party a mechanism to suggest to a client application the type of authenticator that should be used to complete a WebAuthn ceremony.

The ability to suggest a specific type of authenticator can be useful for a variety of situations that can include:

- An enterprise relying party suggesting to the client that it's expecting for a security key to be used
- Prompts in a consumer application that provides different user flows for platform authenticators and security keys
- The ability to hide the QR code presented in a cross-platform defined requests when only a security key is expected

## Client hints vs authenticator attachment

Earlier in this guide, we introduced the concept of [authenticator attachment](https://yubicolabs.github.io/passkey-workshop/docs/relying-party/reg-flow#:~:text=discouraged-,authenticatorAttachment,-defines%20the%20AuthenticatorAttachment). Authenticator attachment has historically been the mechanism to enforce the use of a specific type of authenticator against two choices:

- **Platform**, which indicates the built in authenticator on a device such as Windows Hello, and Touch ID
- **Cross-platform**, which could be any roaming device such as a security key, or a phone acting as a roaming authenticator through the hybrid (QR code) flow

The primary difference between hints and authenticator attachment is in the enforcement of the selection on the property. Hints will immediately show the WebAuthn prompt defined by the hint, but will allow the user to make another selection of their choice. Authenticator attachment always enforces the use of the authenticator that was defined by the relying party (so if platform was selected, then security keys are no longer an option for the user).

Use the outline below to understand how the two properties may interact as they continue to coexist in the WebAuthn specification.

- Hints may contradict what's defined in the authenticator attachment, when this occurs the hint setting should be given precedence.
- Authenticator attachment should not be deprecated from a relying party, as it should be used as a compliment to hints for clients (browsers, OS, and platforms) that have not adopted the use of hints

## Example

Below is a demonstration of how hints work in a client application. The video denotes different (but not all) permutations for hints, and how the client reacts to the input. An in-depth explanation on the different hints enumerations is included in the next section.

<ReactPlayer
controls
width="100%"
height="100%"
url="/passkey-workshop/videos/hints-video.mp4"
style={{ marginBottom: "2em" }}
/>

## Types of hints

The WebAuthn specification notes a few different options that can be expressed to the client from the relying party. Hints are expressed in the attestation and assertion requests as the hints property, which is an array list of the enumerations listed below.

The enumerations are ordered in the following decreasing preference: `security-key, client-device, hybrid`. This means that if two hints are contradictory, then the option with the higher preference will be presented over the other.

Meaning that if a hints property is set with a value of `["hybrid", "security-key"]`, then the user will be presented with a modal to register or authenticate with a security key.

### None (null)

The option of no hint can be denoted by not including the hints property, setting the property to `null`, or by passing a hints property with an empty array. This indicates to the client application that your relying party has no preference on the authenticator used, and will show a standard modal presenting all of the standard WebAuthn options of the client.

### security-key

`security-key` denotes to the client application that it's expecting the user to leverage a security key for registration or authentication. This is helpful in enterprise, and other high assurance scenarios as it helps to guide the use of device-bound passkeys found on security keys.

When using this option you should set the `authenticatorAttachment` property to `cross-platform`, This will help ensure compatibility with clients that have not adopted the use of hints in their WebAuthn implementation.

### client-device

`client-device` denotes to the client application that it's expecting the user to leverage a platform authenticator. This will likely result in the creation of copyable passkeys.

When using this option you should set the `authenticatorAttachment` property to `platform`, This will help ensure compatibility with clients that have not adopted the use of hints in their WebAuthn implementation.

### hybrid

`hybrid` denotes to the client application that it's expecting the use of a roaming, platform authenticator that will leverage the hybrid (QR code) flow. The user will be presented with a QR code to scan with their smartphone to leverage a passkey accessible by the device.

When using this option you should set the `authenticatorAttachment` property to `cross-platform`, This will help ensure compatibility with clients that have not adopted the use of hints in their WebAuthn implementation.

## Platform support

Before attempting to implement hints from your relying party, ensure that your chosen platform (browser, OS, client) has support for Client Hints. You can use the [device support matrix on passkeys.dev](https://passkeys.dev/device-support/) to see if your platform is supported.

## Implementation guidance

### Relying party

The goal of implementing Client Hints for a Relying Party is ensuring that you add the property to the `PublicKeyCredentialCreationOptions` for `create()` calls, and `PublicKeyCredentialRequestOptions` for `get()` calls.An example of both can be found below.

Attestation options for passkey creation:

```json
"publicKey": {
  "rp": ...,
  "user": ...,
  "challenge": "uhUjPNlZfvn7onwuhNdsLPkkE5Fv-lUN",
  "pubKeyCredParams": ...,
  "authenticatorSelection": ...,
  "attestation": "direct",
  // highlight-start
  "hints": ["security-key", "client-device", "hybrid"]
  // highlight-end
}
```

Assertion options for passkey authentication:

```json
"publicKey": {
  "challenge": "NGc3jpB4Q-VnOmbhFBnDAczlYPT4soKA7xviGeJmDhc",
  "timeout": 180000,
  "rpId": "localhost",
  "userVerification": "preferred",
  // highlight-start
  "hints": ["security-key", "client-device", "hybrid"]
  // highlight-end
}
```

In-depth implementation guidance can be found on the Relying Party pages for both [registration](/docs/relying-party/reg-flow) and [authentication](/docs/relying-party/auth-flow).

### Clients

There's no additional code that's needed to leverage hints from a client application, assuming that the client has support for hints (as outlined in the previous section on platform support).
