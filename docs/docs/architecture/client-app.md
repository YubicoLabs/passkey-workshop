---
sidebar_position: 2
---

# Client application

The client is the user interface aspect of your application, where your users will interact with your service. In the case of a passkey application, there are two aspects of the client that should be considered: the user interface that you develop, and the ecosystem that the user is on.

## User ecosystem

The ecosystem (browser | operating system) is a critical component that your application team should be aware of. While the WebAuthn specification provides the recommended guidelines for passkeys, there still remains nuances in how they are implemented. Each of the major browsers and operating systems are developed by individual entities who have different implementation approaches. It should be noted that for the most part the passkey experience will remain consistent across ecosystems, notable differences between ecosystems could include:

- Support for new functionality (Autofill)
- Wording and displays for modals and prompts
- Default behaviors for `PublicKeyCredentialCreationOptions` and `PublicKeyCredentialRequestOptions` configurations

Before you develop an application you should consider what ecosystem your users are leveraging. For consumer applications the permutations of ecosystems is boundless, but it’s wise to support the major browsers (Google Chrome, Apple Safari, Microsoft Edge, Mozilla Firefox) and operating systems (Windows, iOS, macOS, and Android).

Enterprise applications may have fewer permutations in the ecosystems allowed, and may offer more control in how you guide and prompt users.

Please refer to these resources to see passkey support across a variety of different ecosystem:

- [Yubico WebAuthn browser support matrix](https://developers.yubico.com/WebAuthn/WebAuthn_Browser_Support/)
- [Passkeys.dev device support matrix](https://passkeys.dev/device-support/)

## Web vs native applications

In line with the topic mentioned above, you may need multiple implementations of your client depending on the ecosystems used by your users. For the most part, a single web based application will be supported across mainstream ecosystems (but please consult the browser support matrices mentioned above). Native applications will require targeted implementations for each ecosystem that you wish to support. For instance, your passkey implementation for a native Android application will not work for iOS.

::::tip Not every component requires a targeted implementation
The client portion of our application is the only component that requires targeted implementations based on the ecosystem. The relying party should be usable by all of your clients through some form of API.
::::

This workshop currently provides two examples of client applications:

- A web based example (leveraging React) //@TODO - Add links when the sections are ready
- A native [iOS example](../category/mobile-client/).
