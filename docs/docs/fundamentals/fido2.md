---
sidebar_position: 4
---

# FIDO 2

The technical term for passkeys is "discoverable FIDO2 credentials".

FIDO2 is a set of open standards for passwordless authentication.
It describes how a user authenticates to a _relying party_ (RP) via a _client_ using an authenticator.
The RP is typically a web site (for instance Github).
The client can be a web browser (such as Chrome) of a platform (for instance Microsoft Windows)

Two important standards used in FIDO 2 are:

- Webauthn - an API implemented by clients to facilitate registration/authentication ceremonies
- CTAP - a protocol for clients to interface with authenticators

In this workshop we will be mostly dealing with Webauthn, as that is the API used by relying parties to let users sign in with passkeys.

![FIDO2](/img/platform-roaming-figure2.png)