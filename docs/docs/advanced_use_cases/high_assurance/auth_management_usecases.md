---
sidebar_position: 2
---

# Authenticator management use cases

In this section we are going to dive deeper into common use cases and scenarios to help you better understand what approach you should take when determining the need to limit certain FIDO2 authenticators in your application. By the end of this section you should understand if your application needs to provide limitations on certain authenticators, and where you can go to learn how to implement those limitations.

## Choosing an authenticator management strategy

When creating an authenticator management strategy, you want to gauge the risk level of your application to determine what types of authenticators you want enrolled in your environment. If your application falls under a regulatory body, is responsible for financial transactions, or contains sensitive information then you may want to consider not only limiting your application to a high assurance device, but also consider putting constraints on specific high assurance devices that can be used.

The lower risk your application is, the more lenient and permissive you can be in the kinds of authenticators that you allow. For instance if you are making a consumer facing application, such as a social media application, then you can allow for low assurance authenticators for users as it’s already an improvement over passwords, and the repercussions of a compromised account aren’t always severe.

If you are having trouble determining what assurance level you should be using, NIST provides guidance in [Figure 6-2 of their Digital Identity Guidelines](https://nvlpubs.nist.gov/nistpubs/SpecialPublications/NIST.SP.800-63-3.pdf).

The diagram below presents a spectrum of strategies that can be taken based on the assurance level of your application.

![Authenticator management spectrum](/img/auth_spec.jpg)

From this diagram we are going to dive into each box and relate them to common use cases and user scenarios. The goal is to test which experience resonates the most with your target user base, and allow you to determine which implementation guide is right for you.

## Overview of authenticator management strategies

### Allow passkeys from any authenticator

This is the most direct scenario for your application. In this instance your relying party is going to accept any passkey that attempts to register in your application.

This scenario works best when:

- You have a wide consumer audience
- You don’t want a barrier of entry to use your application
- You want your users to utilize anything from high assurance security keys, to copyable passkeys.

### Vulnerability remediation

There is another scenario when allowing all types of authenticators to be used in your environment; what do you do if an authenticator is found as vulnerable? In a high risk controlled environment you may opt to reject the authenticator entirely, but you may want to take a different approach for a consumer facing application. In this scenario we recommend to send alerts to account holders who may have a passkey registered that was created with an authenticator with a vulnerability.

### Limit passkeys based on characteristics/features

This scenario is where you begin to add controls to make your application dynamically respond to different passkey types. This will allow you to add a higher degree of security to user accounts, while not constraining account holders to specific authenticators. Your relying party will be able to adapt and route users across different flows based on the risk profile of the authenticator used, and the action being attempted by the user.

It’s worth noting that this category is where you will begin to introduce friction for users who are not leveraging high assurance devices. A user who is leveraging a high assurance device will be able to seamlessly authenticate due to the very high degree of confidence that their request is legitimate. Authentication ceremonies performed with lower assurance devices may require another factor to authenticate, or step-up authentication to perform certain actions.

This scenario works best when:

- A user’s authenticator is missing user verification
- A user needs to perform a sensitive action that can only be done from a session where a high assurance passkey was used to authenticate

### Deny passkeys from specific authenticators

In the previous sections we were highly permissive in the authenticators allowed to register passkeys to our environment, and even if a device wasn’t completely trusted we supplied options to allow them to be used. As we transition into this section we will begin to entirely block certain authenticators from creating passkeys in an environment. This means that any attempt to register one of these passkeys in our application will be rejected.

Within your environment you will create a curated list of FIDO2 authenticators that will be rejected by your application. This list could be based off of an external repository such as the FIDO MDS that tracks authenticators with known issues, or a manually curated list of authenticators deemed untrustworthy by your organization.

This scenario works best when:

- You want to prevent users from registering passkeys with certain properties, e.g.copyable passkeys
- Your organization has decided not to trust certain authenticator manufacturers
- You want your application to automatically remediate rather than send user alerts for authenticators with vulnerabilities

### Allow passkeys from specific authenticators

In this scenario you will exercise the highest degree of control in your high risk environment by restricting registration to only a select list of curated authenticators. In a high risk environment you want to ensure that you are limiting authorized authenticators to high assurance devices to ensure that you have a very high degree of confidence that the authenticator used in the authentication ceremony is in the possession of the real account holder.

This scenario works best when:

- Your organization requires the use of a set of approved authenticators
- You want to prevent users from registering low assurance authenticators, like copyable passkeys
- You are required to limit registrations from high assurance devices
