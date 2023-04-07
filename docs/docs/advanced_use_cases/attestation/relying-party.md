---
sidebar_position: 3
---

# Relying party

A relying party (RP) is an industry term that is used to describe an application that is used to verify the identity of an entity through authentication, and to provide the correct level of authorization based on the permissions granted to the entity.

In terms of passkey applications, this is the component that will manage credentials, verify and issue authentication challenges, and grant access when appropriate. While relying parties for passkey applications facilitate similar ceremonies, they are not one size fits all, and should reflect the security policies set by business or regulatory requirements. In this guide when we refer to the backend application, note that we are only referencing the responsibilities of the relying party.

## Build vs buy

It is not always required to develop each component of an RP. There are many services that provide passkey/webauthn functionality out-of-the-box (such as Okta, or Azure B2C).

The determination of build vs buy should be made by evaluating options against your security policies, use case, and developer resources. Some use cases may work well using the out of the box features of a service. Other use cases may require more specific functionality based on your security policies that may leave you with no choice but to develop your own solution.

The examples in the workshop are a demonstration of a fully custom built solution; or in more accurate terms, a solution comprised of different mainstream services that work together to create a passkey experience. Note that the components used in our examples are not meant to act as our recommended technologies; rather, we are trying to demonstrate that a passkey application, if architected correctly, can be built using technologies familiar to many developers.

Once you have grasped the fundamental concepts, feel free to leverage the interfaces defined in our application to integrate your preferred technologies.

## Components in our RP example

### Application layer

Our application was developed using the Java Spring-Boot framework. This application leverages [Yubico's java-webauthn-server library](https://github.com/Yubico/java-webauthn-server), which comes with pre-built methods and classes to help you ingest and process WebAuthn requests.

RPs are not only limited to using Java, and Yubico's library. [This page](https://github.com/herrjemand/awesome-webauthn) will provide different alternatives for libraries and SDKs that can be used to ease your app development.

### Identity provider

Our example leverages [Keycloak](https://www.keycloak.org/) as our identity provider, and authorization server.

It's important to note that Keycloak provides WebAuthn functionality out of the box. With that said our implementation leverages a custom Keycloak authentication SPI, that connects to our application layer component (mentioned above). We took this approach in-order to extend our application logic beyond what is provided in Keycloak.

### Credential repository

Our application leverages MySQL as the database leveraged by the application. Below you will find the schemas that are used to define the different tables in our database

#### Credential repository

The credential repository is the table that will store the passkeys that are created by the user. Keep in mind that these are the public keys that correspond to the private key, managed by your authenticator.

One of the main advantages that passkeys have over passwords is that a compromise of this table is not as severe, as would be the case when it contained passwords. The public keys are useless without the corresponding private key.

Note that many of the fields are noted as TEXT. Where noted, this is either a base64url string denoting an identifier, or a json string that contains data used by the application.

```sql
CREATE TABLE credential_registrations (
  id BIGINT,
  credential TEXT, --JSON string containing the credential
  credentialid TEXT, --base64url string denoting the ID of the credential
  credential_nickname TEXT,
  last_update_time BIGINT,
  last_used_time BIGINT,
  registration_time BIGINT,
  user_handle TEXT --base64url string denoting the ID of the user
);
```

#### Attestation request repository

The attestation request repository will store the requests that have been issued by the relying party for the creation of a new passkey. This is important to implement as you will want a mechanism to ensure only valid registration requests are sent to your application. This will allow you to:

- Block unprompted registrations
- Block registrations that send a challenge that does not match what was previously issued
- Invalidate registration requests that have been used or have exceeded a timeout period

```sql
CREATE TABLE attestation_requests (
  id BIGINT,
  attestation_request TEXT, --JSON string containing the PublicKeyCredentialCreationOptions
  is_active BOOLEAN,
  request_id TEXT --base64url string denoting the ID of the request
);
```

#### Assertion request repository

The assertion request repository will store the requests that have been issued by the relying party for authenticating with a passkey. You will want a mechanism to store and refer to authentication requests that have been sent. This will enable you to:

- Block unprompted authentications
- Block authentications that utilized a challenge that does not match what was issued
- Refer to the original challenge that was issued in order to verify signed challenges
- Invalidate authentication requests that have been used or have exceeded a timeout period

```sql
CREATE TABLE assertion_requests (
  id BIGINT,
  assertion_request TEXT, --JSON string containing the PublicKeyCredentialCreationOptions
  is_active BOOLEAN,
  request_id TEXT --base64url string denoting the ID of the request
);
```

### Metadata repository

This example leverages the [FIDO Metadata Service (MDS)](https://fidoalliance.org/metadata/), which is a collection of metadata for authenticators. The MDS can be used to evaluate the root of trust sent with a credential during registration in order to identify the device, and correlate it to a metadata entry containing a variety of different data on the authenticator.

The repository is offered in the form of a BLOB hosted on a FIDO Alliance resource. If your application is connected to an external network, then you can download the BLOB using a cURL request, or you can download and self-host it for non-public facing resources hosted in your environment.

The [Yubico java-webauthn-server library](https://github.com/Yubico/java-webauthn-server) includes support for the MDS, where you are able to download the BLOB, and validate attestation statements.
