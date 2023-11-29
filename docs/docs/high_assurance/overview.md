---
sidebar_position: 1
---

# Overview of high assurance scenarios

With the recent uptick of passkey adoption in the consumer space, enterprises and public sector application developers will be looking to understand how to appropriately incorporate the technology into their applications. This module will discuss how passkeys fit into high assurance scenarios.

In our case, high assurance scenarios are any use cases where the application is operating in high-risk, or regulated industries such as healthcare, finance, energy, or the public sector.

As outlined in the previous section on the [types of passkeys](/docs/fundamentals/types-of-passkeys), there are risks associated with the use of copyable passkeys. Copyable passkeys are not device bound, and may be synced to other devices, or shared between users. This removes the assurance that the user leveraging the passkey is the same user that created it. While copyable passkeys are still more secure than passwords, they don't offer the complete assurance that is often required for regulatory applications.

High assurance scenarios are often focused on internal use cases, but there are cases where a consumer application requires high assurance for regulatory compliance. So even if your security policy has a preference for high assurance authenticators, the convenience of copyable passkeys may be needed for users who either 1) can't purchase specialty hardware, or 2) might not always carry a security key with them.

The guidance provided in this module will demonstrate a consumer bank scenario that allows for both types of passkeys, but will **require** step-up authentication using a high assurance passkey for "high-risk" transactions.

Some of the concepts explored in this module are:

- UX guidance for allowing both device bound and copyable passkeys
- Using passkeys to perform step up authentication
- Using attestation to determine the assurance level of a passkey
- How mobile apps can use passkeys for OIDC + OAuth flows

Click the **next** button below to learn more about high assurance passkey scenarios!
