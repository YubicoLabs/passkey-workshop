---
sidebar_position: 1
---

# Allow lists

An allow list is a curated list of authenticators that should be permitted to register and authenticate to your application. Any authenticator that is not identifiable, or part of your curated list should be rejected. This scenario works well for applications that require a more secure and controlled environment, and where you want to ensure that your users are leveraging high assurance authenticators. The guidance given on this page will teach you how to implement an allow list in your application, that will limit registrations to a few select authenticators.

This scenario works best when:

- Your organization requires the use of a set of approved authenticators
- You want to prevent users from registering authenticators like copyable aka multi-device passkeys
- You are required to limit registrations to high assurance devices

## User experience

Below demonstrate the flow of a user attempting to register a new passkey in your application.

![Allow list](/img/allowlist1.jpg)

In this flow the happy path comes from a user who immediately opts into sending attestation data, and ensuring that they are using an authenticator that is allowed in your environment.

It’s worth noting that in this experience most of the logic/decision points are left up to the application, with little intervention from the user, aside from creating the initial registration, and reacting to any request rejections.

Here are some considerations for this flow:

- Ensure that your users know to allow for attestation data to be sent during the registration ceremony, this can be done by modals, tipboxes, or instructions on your client application
- Ensure that users know which of their authenticators to use for the application
- Ensure error messages accurately reflect the issue at hand so that users have a better chance to self remediate

## Prerequisites

@TODO - Create page for this

## Implementation guidance

### Allow only trusted attestation

In this flow you must opt to only accept registrations from trusted attestation sources. This means that any registration sent to your application must include an attestation statement. If a user opts not to send an attestation statement, then their registration should be rejected. A user request may also be rejected if the authenticator they are using is not included in the MDS.

To achieve this using the java-webauthn-server, ensure that you set `allowUntrustedAttestation()` to `false`. This will ensure that any registration sent without an attestation statement, or an attestation statement not found in the MDS is rejected.

This configuration can be set in our sample application by performing the steps below:

1. In the [configuration file](https://github.com/YubicoLabs/passkey-workshop/blob/main/scripts/DeployProject.conf), change `RP_ALLOW_UNTRUSTED_ATTESTATION` to `false`
2. Run the [deploy command](/docs/deploy#deploying-the-project)

The code sample below demonstrates the sample code used to enable this behavior in our sample application.

::::note
The block of code below was introduced in the Attestation section on [relying party implementation guidance](/docs/advanced_use_cases/attestation/relying-party-implementation#configuration-changes)
::::

```java
this.relyingParty = RelyingParty.builder()
  .identity(generateIdentity())
  .credentialRepository(storageInstance.getCredentialStorage())
  .origins(generateOrigins())
  .attestationConveyancePreference(
      AttestationConveyancePreference.valueOf(System.getenv("RP_ATTESTATION_PREFERENCE")))
  // highlight-next-line
  .allowUntrustedAttestation(false)
  .attestationTrustSource(null) // Set this field to null, we will cover this in detail in the section on attestation
  .validateSignatureCounter(true)
  .build();
```

If you were only looking to use the MDS as your allow list, only accepting trusted attestation, then no other code is needed. The following sections will go over how to further reduce the scope of trusted attestation to a few selected authenticators.

Rejecting untrusted attestation will also help when attempting to block copyable passkeys. This is due to the fact that the copyable passkey implementations will not send attestation data during registration. By this logic, if you block any credential missing attestation data, then there’s a good chance that you’re blocking a copyable passkey.

### Allow only specific authenticators

The next step is to create a list of the authenticators that you want to allow in your application. There are a few different identifiers that you can use from a metadata statement; In this example we are going to leverage the AAGUID as the device identifier. An AAGUID is the unique identifier given to every authenticator in the MDS.

[AAGUID mappings for Yubikeys can be found in this support article](https://support.yubico.com/hc/en-us/articles/360016648959-YubiKey-Hardware-FIDO2-AAGUIDs). For non YubiKey AAGUIDs you can search the MDS manually or work with your authenticator manufacturer to find your specific AAGUIDs.

For this example we are going to be leveraging the AAGUIDs for the YubiKey 5Ci FIPS and YubiKey 5 FIPS Series with NFC.

This configuration can be set in our sample application by performing the steps below:

@TODO - Alter the steps below, we need a specific field for declaring AAGUIDs

1. In the [configuration file](https://github.com/YubicoLabs/passkey-workshop/blob/main/scripts/DeployProject.conf), change `RP_ALLOW_UNTRUSTED_ATTESTATION` to `false`
2. Run the [deploy command](/docs/deploy#deploying-the-project)

The remaining content below will demonstrate how this allow list is implemented in our sample application.

You can declare this list in a variety of different ways, in this example we will declare it as an ArrayList of strings.

The code sample below demonstrates how to create a curated list of AAGUIDs

```java
private final ArrayList<String> aaguids =
  new ArrayList<String>(
    Arrays.asList("85203421-48f9-4355-9bc8-8a53846e5083 ",
      "c1f9a0bc-1dd2-404a-b27f-8e29047a43fd"
  ));
```

Now that you have a list of AAGUIDs that you want to be accepted, let’s ensure that our instance of the MDS only contains entries for our curated list.

We will accomplish this by attaching a filter operation when we initialize the MDS in our application.

::::note
The block of code below was introduced in the Attestation section on [relying party implementation guidance](/docs/advanced_use_cases/attestation/relying-party-implementation)
::::

```java
FidoMetadataService mds = FidoMetadataService.builder()
  .useBlob(downloader)
  .filter(blobEntry -> aaguids.contains(blobEntry.getAaguid().get().asGuidString()))
  .build();
```

In the code sample above you are iterating through the entire MDS. You will retain each metadata statement for each entry that has an AAGUID that is contained in your curated list. Your final list will only contain entries that are related to the items marked in your AAGUID list.

The reason this filtering technique works is because the scope of your MDS is reduced to your desired authenticators. This means that your application will not deem authenticators out of this scope as trusted as there are no metadata entries to compare the AAGUID and trust root of an attestation statement sent during registration.

It should be noted that it’s not as simple as an attacker spoofing the AAGUID to gain access to your environment. Your curated AAGUID list is only meant to act as a filtering mechanism. The actual trust operation done by the java-webauthn-server will compare both the AAGUID and trust root certificate sent by the device, which gives a higher degree of assurance that your authenticator is the make and model it says it is.
