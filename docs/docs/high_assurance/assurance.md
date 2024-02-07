---
sidebar_position: 2
---

# Assurance levels

First let's understand the concept of assurance level. In our context assurance refers to the authenticator assurance level that is defined in the guidance given by NIST in their report Digital Identity Guidelines. These are the technical requirements for federal agencies implementing digital identity solutions. We are utilizing this guidance as some of the principles should be taken under consideration for applications that operate in high risk scenarios.

For our purpose assurance is broken down into two levels, low and high assurance.

## Low assurance
Low assurance authenticators require that a device only requires a single factor. The person attempting to sign in only needs to prove possession and control of the device to authenticate into an account.

It's important to understand that any WebAuthn is still more secure than a password. So while the experience outlined above seems too seamless to be secure, the authenticator is still phishing resistant, and requires possession and control of an authenticator to gain access to an account.

If an application is lower risk (i.e doesn’t deal with financial data, personal information, or possess personal or safety risks) then it should be free to leverage low assurance authenticators.

## High assurance
High assurance authenticators require that there are two different authentication factors AND that the device is a hard authenticator; providing impersonation resistance. This means that the private key is only present on one device, and can’t be copied or synced to another authenticator.

High assurance is used for high risk applications that pose critical risk to a variety of areas such as financial risk, personal safety, or exposure of sensitive data.

## Choosing the right passkey
For the rest of this article, we are going to explore different use cases to help you determine what type of passkey is best for your scenario. We will dig deep into both consumer and enterprise use cases to highlight how assurance levels are not one-size-fits-all, and can fit into either space.

Figure 1 provides an informational graphic that highlights the concepts that will be covered in the rest of this article.

![Architecture](/img/passkey_choose.png)

## Use Cases

### Consumer Use Case
These are use cases that can be utilized by the broad consumer space. Apps that can be downloaded onto personal devices such as: social media, personal productivity tools, streaming apps, and more.

What's important to note about consumer applications are the large number of permutations that could exist in terms of authenticators, operating systems, and browsers.

Because consumer applications are typically low risk, the use of low assurance authenticators is acceptable. If the goal of implementing passkeys in your consumer application is to reduce the possibility of phishing attacks, and streamlining the user experience, then **any** form of passkey will be acceptable.

You do not want to impose limits on consumer users that will require them to buy specialty hardware to use your application. The big draw of passkeys in the consumer space is that the vendors who develop operating systems and devices have, or are in the process of integrating passkeys into everyday consumer devices like phones and laptops.

For consumer applications it's better to err on the side of permissive when it comes to the authenticators that you allow. Any form of WebAuthn is still better than no WebAuthn. With that said, there are examples in the consumer space where a high degree of assurance is required. To validate this claim, let's evaluate a few consumer user cases to better understand how to determine assurance levels.

### Enterprise Use Case
These are use cases that can be found in non-consumer applications. This could be an application that is only internal to a company, an application used by a government agency, an application offered to partners/customers of an enterprise, and other similar use cases. The sensitive, or confidential nature of these applications will typically land them with the requirement to leverage high assurance authenticators.

In these scenarios it's recommended to leverage SDCs. This would reduce the ability for a credential to be utilized outside of the device that it was originally created on.

You may want to consider leveraging [attestation](../category/attestation) and an [authenticator management strategy](.././advanced_use_cases/high_assurance/auth_management_mech) in order to tighten controls on the devices allowed to register in your application.

There may be some enterprise use cases that don't require high assurance devices. To validate this claim, let's evaluate a few enterprise use cases to better understand how to determine assurance levels.
