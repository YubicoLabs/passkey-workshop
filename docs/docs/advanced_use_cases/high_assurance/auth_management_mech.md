---
sidebar_position: 3
---

# Authenticator management mechanisms

In the previous section we outlined high assurance scenarios and use cases. In this section we are going to demonstrate how to use attestation as a way to enforce strict authenticator requirements from your relying party. At the end of this section you should understand the different mechanisms that can be used to manage the authenticators that can register to your application.

## Methods for limiting authenticators

Below are different methods that can be used to perform authenticator management.

### Allow list

An allow list is a curated list of authenticators that should be allowed to register and authenticate to your application. Any authenticator that is not identifiable, or part of your curated list should be rejected

This method works well for situations where you’re an enterprise and you want to limit your application to only use the authenticators that you purchased from a trusted manufacturer.

![Allow list](/img/allowlist1.jpg)

### Deny list

Curated list of authenticators that should not be allowed to register and authenticate to your application. Any authenticator should be able to register to your application, but if an authenticator is identified in your curated list then it should be rejected.

This method works well for situations where you want to prevent or limit the use of a vulnerable authenticator in your application.

![Deny list](/img/denylist1.jpg)
