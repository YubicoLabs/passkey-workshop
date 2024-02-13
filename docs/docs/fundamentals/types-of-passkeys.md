---
sidebar_position: 3
---

# Types of passkeys

Depending on the authenticator, different types of passkeys can be supported.

# Device-bound bound passkeys

These are also called single-device passkeys, as they are bound to the specific hardware device they were created on.
It is not possible to make a copy of such passkeys - the private key associated with the passkey can never leave its authenticator. This means that if you lose the device, you lose access to the passkey stored on that device.

Device-bound passkeys are typically stored on FIDO security keys, which contain dedicated hardware to ensure private keys cannot be exported from the device.

Of course, the fact that a device-bound passkey cannot be copied makes it a possession factor for MFA, but it also means you cannot make a backup of a device-bound passkey.

This makes device-bound keys ideal for high assurance use cases typically found in enterprise environments.

# Copyable passkeys

For low assurance use cases, it can make sense to sacrifice a bit of security to enable passkeys to be copied.
Copyable passkeys, also called multi-device passkeys, can be copied to multiple devices. For instance to make a backup of a passkey in case a device with a platform authenticator that stores a passkey is lost.

Passkeys can also be syncable, meaning that they are synchronized to a user's cloud account. When a user buys a new device and logs into that device with a cloud account, the passkeys stored in that cloud account are automatically synchronized to the new device.

Although copyable passkeys are less secure than device-bound passkeys, they still are a major security improvement over passwords because passkeys are never reused across origins and automatically protect against phishing.

![Copyable vs device-bound passkeys](/img/copyable-vs-hardwarebound.png)
