---
sidebar_position: 1
---

# Overview

This section covers an example Relying Party application for a high assurance scenario.

The Relying Party is based on a fictional bank that allows users to leverage any passkey type, but requires a high assurance passkey for sensitive transactions.
This section will cover what needs to be changed compared to the non-high assurance version of this workshop.

# Prerequisite knowledge

Before you continue into this section, please ensure that you review the previous section on
[Relying Party implementations](/docs/category/relying-party).
The previous sections provide generic implementation guidance on how to register, authenticate, and manage passkeys using a common Relying Party API.

From this point on, this section assumes that you know how the relying party API works.

# A High Assurance use case

The banking application will need to distinguish different levels of assurance (LoAs) as discussed in [Assurance Levels](/docs/high_assurance/assurance),
and implement Step up authentication as described in the section in [Architecture](/docs/high_assurance/architecture).

For our banking application, we use very simple criteria for distinguishing levels of assurance: we assume a low level of assurance by default,
and consider passkeys stored on authenticators listed in the FIDO Metadata Service (MDS), in conjunction with our AAGUID filters, as high assurance.
This typically means that syncable passkeys are associated with a low LoA,
whereas passkeys stored on security keys are associated with a high LoA.
This can of course be refined further by including properties of authenticators as listed in MDS,
such as listed FIDO certifications or hardware protection features, but we will keep it simple here.

All components in our architecture will need to be updated to implement our High Assurance policy, but we can break this down into three steps:

1. When registering a passkey, we need to determine and store the level of assurance of the authenticator used to store the passkey.
2. When authenticating using a previously registered passkey, we need to retrieve its level of assurance and communicate that back to the banking application.
3. When performing bank transactions, the level of assurance needs to be considered when enforcing the bank's policy on authorizing transactions.

The next sections are dedicated to these three steps.
