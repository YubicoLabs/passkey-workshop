---
sidebar_position: 2
---

# Authenticators

Authenticators are used to generate, store, and use passkeys.

When signing up to a website, for instance, a user generates a passkey using an authenticator in a _registration_ ceremony. The authenticator will generate a new private and public key pair for that specific website.
The private key is securely stored in an authenticator, and the public key is sent to the website to associate with the user's account.

Later, when the user returns to the website and needs to sign in, the authenticator is used to authenticate to the website with the passkey stored on the authenticator. In this _authentication_ ceremony, a public key authentication protocol is used where the web site sends a rondom string called the _challenge_ and the authenticator signs that challenge together with some other data using the private key stored on the authenticator. The signature is sent back to the server and verified with the public key associated with the user's account during registration.

Authenticators can store multiple passkeys, because each passkey is associated with a specific web site (or origin), and a passkey cannot be used to authenticate to origins other that the one it was registered on.

Authenticators can come in the form of software or hardware. The former are called _platform authenticators_ while the latter are called _cross-platform authenticators_. 

## Platform Authenticators

Platform Authenticators are built into devices such as phones and laptops. They are implemented in software, although they can also use a device’s TPM module or secure element to protect passkeys.

Some examples of platform authenticators are Windows Hello, or the ones built into Android or Apple devices.
Platform authenticators typically require a user's biometric such as Apple's TouchID or FaceID.

Some platform authenticators allow users to store passkeys in the cloud, to be used across their devices.

## Cross-platform Authenticators

Cross-platform authenticators (sometimes called roaming authenticators) can be used across devices. They typically connect to a laptop, phone, or tablet using USB or NFC.

These devices are called FIDO security keys, and they typically use special hardware to protect the passkeys stored on them. The private keys associated with passkeys are bound to the device, and cannot be extracted. 

Security keys have a limited amount of storage available for passkeys. This means that, unlike platform authenticators, there is a limit to the number of passkeys that can be stored. 
The maximum number of passkeys that can be stored on a security key depends on the specific security key make and model.

Examples of FIDO security keys are the YubiKey 5 or the Solo key, but there are many other vendors of security keys.

## Hybrid 

TODO: explain cross-device authentication