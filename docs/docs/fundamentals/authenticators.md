---
sidebar_position: 2
---

# Authenticators

Authenticators are used to generate, store, and use passkeys.

When signing up to a website, for instance, a user generates a passkey using an authenticator in a process called registration. The authenticator will generate a new private and public key pair for that specific website.
The private key is securely stored on in authenticator, and the public key is sent to the website to associate with the user's account.

Later, when the user returns to the website and needs to sign in, the authenticator is used to authenticate to the website with the passkey stored on the authenticator. A public key authentication protocol is used where the web site sends a challenge and the authenticator signs the challenge and some other data with the private key stored on the authenticator. The signature is sent back to the server and verified with the public key associated with the user's account during registration.

Authenticators need to store multiple passkeys, because every passkey are generated for a specific web site (or origin).

Authenticators can come in the form of hardware or software. The former are called _platform authenticators_ while the latter are called _cross-platform authenticators_. 

## Platform Authenticators

Platform Authenticators are built into devices such as phones and laptops. They are implemented in software, although they can also use a device’s TPM module or secure element to protect passkeys.

Some examples of platform authenticators are Windows Hello, Android or Apple's TouchID and FaceID.

Some platform authenticators allow users to store passkeys in the cloud, to be used across their devices.

## Cross-platform Authenticators

Cross-platform authenticators (sometimes called roaming authenticators) can be used across devices. They typically connect to a laptop, phone, or tablet using USB or NFC.

These devices are called FIDO security keys, and they typically use special hardware to protect the passkeys stored on them. The private keys associated with passkeys are bound to the device, and cannot be extracted. 

Examples of FIDO security keys are the YubiKey 5 or the Solo key.

## Hybrid 

TODO: explain cross-device authentication
