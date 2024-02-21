---
sidebar_position: 2
---

# Registration

This section covers registration for a high assurance scenario.

We already discussed attestation, the FIDO Metadata Service (MDS), and how to add attestation support to a Relying Party in the section on
[attestation](/docs/category/attestation), so we will not repeat that here.

This time however, we will use the fact that an authenticator is listed in MDS to determine whether or not the authenticator (and thus its stored passkeys) is to be considered High Assurance. For this, we will introduce a new property called `isHighAssurance` that we will store for each credential registered.

## API

We begin by extending the passkey API that was described in [API definition](/docs/relying-party/api-def).
If you deployed the banking application on `localhost`, you can find its OpenAPI definition [here](http://localhost:8080/swagger-ui/).

### Registration API

When sending a public-key attestation response using ([`/attestation/result`](http://localhost:8080/swagger-ui/index.html#/v1/serverAuthenticatorAttestationResponse)), the `isHighAssurance` property is returned together with the other information stored in the repository:

For instance:

```json
{
  "status": "created",
  "credential": {
    "id": "DthUeofXNtlMevkt_M7aiD3cm70...",
    "type": "public-key",
    "nickName": "YubiKey 5Ci",
    "registrationTime": "2022-07-21T17:32:28Z",
    "lastUsedTime": "2022-07-21T18:15:06Z",
    "iconURI": "YubiKey 5Ci",
    "isHighAssurance": true,
    "state": "ENABLED"
  }
}
```

### Credentials API

Similarly, when retrieving credential data for a specific user using
([`/user/credentials/{userName}`](http://localhost:8080/swagger-ui/index.html#/v1/userCredentialsByID)), the `isHighAssurance` property is returned together with the other information stored in the repository. For instance:

```json
{
  "credentials": [
    {
      "id": "qn1QwPCX7kmwji1sVPnkB63Vaq29sc-uheZ_n_ejdMFs3pCzsKN5Dmlm9EVHfR6O",
      "type": "public-key",
      "nickName": "YubiKey 5 Series",
      ...
      "isHighAssurance": true,
      "state": "ENABLED"
    }
  ]
}
```

## Data Sources

We continue with data sources, based on what we described earlier in [Data sources and RP configurations](/docs/relying-party/config-and-data).
We need to change our credential repository to store whether or not a passkey is stored on a high assurance authenticator.

Let's revisit the `buildCredentialDBO` method used to package passkey information to store in our credential repository.
Earlier, we added information like the name and the icon of the authenticator the passkey was stored on, when that authenticator was listed in MDS.
We will now simply add a boolean `isHighAssurance`. We assign the value `true` if we found a matching MDS entry when resolving an attestation, and assign the value `false` otherwise:

```java
  private CredentialRegistration buildCredentialDBO(PublicKeyCredentialCreationOptions request,
      RegistrationResult result) {
    Optional<MetadataStatement> maybeMetadataEntry = resolveAttestation(result);

    String credentialName;
    String iconURI;
    boolean isHighAssurance;

    if (maybeMetadataEntry.isPresent()) {
      credentialName = maybeMetadataEntry.get().getDescription().isPresent()
          ? maybeMetadataEntry.get().getDescription().get()
          : "My new passkey";
      iconURI = maybeMetadataEntry.get().getIcon().isPresent()
          ? maybeMetadataEntry.get().getIcon().get()
          : null;
      isHighAssurance = true;
    } else {
      credentialName = "My new passkey";
      iconURI = null;
      isHighAssurance = false;
    }

    return CredentialRegistration.builder()
        .userIdentity(request.getUser())
        .credentialNickname(Optional.of(credentialName))
        .registrationTime(clock.instant())
        .lastUpdateTime(clock.instant())
        .lastUsedTime(clock.instant())
        .credential(RegisteredCredential.builder()
            .credentialId(result.getKeyId().getId())
            .userHandle(request.getUser().getId())
            .publicKeyCose(result.getPublicKeyCose())
            .signatureCount(result.getSignatureCount())
            .build())
        .iconURI(Optional.ofNullable(iconURI))
        .isHighAssurance(isHighAssurance)
        .state(StateEnum.ENABLED)
        .build();
  }
```

:::::note
The FIDO Metadata Service (MDS) is used to retrieve properties of a specific authenticator make and model.
Here we assume these properties define the assurance level of credentials stored on such authenticators.
The `isHighAssurance` property introduced here is a property of the credential (passkey).
:::::

:::::warning
The fact that an authenticator is listed in MDS doesn't automatically mean it is to be considered high assurance.
This depends on the local policy towards trusted authenticators.
We just define a very simply policy here that assumes passkeys stored on authenticators registered in MDS are to be considered high assurance.
When implementing a high assurance scenario yourself, think carefully about what your policy should be like and implement that policy accordingly.
:::::

Next, we'll add our new property to the `CredentialRegistrationDBO` class that is used to store credentials in MySQL server:

```java
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "credential_registrations")
public class CredentialRegistrationDBO {
...

  @Getter
  @Column(columnDefinition = "boolean default false")
  boolean isHighAssurance;
}
```

We will need to persist this information in the database by updating the table schema that is utilized for our credential repository (see [our initial version](/docs/architecture/relying-party.md)) accordingly:

```sql
CREATE TABLE credential_registrations (
    ...
    is_high_assurance BOOL DEFAULT FALSE,
    ...
)
```

## Registration Flow

Note that for adding the new `isHighAssurance` property, we do not need to change our
[Attestation options method](/docs/relying-party/reg-flow#attestation-options-method).
The only thing we need to change is the.
[Attestation result method](/docs/relying-party/reg-flow#attestation-result-method)

Similar to what was explained in [registration flow](/docs/relying-party/reg-flow.md), the implementation of the `/attestation/result` method simply returns the credentials properties, now including its high assurance status:

```java
  public AttestationResultResponse attestationResult(AttestationResultRequest response) throws Exception {

        ...

        return new AttestationResultResponse().status("created").credential(
            UserCredentialsResponseCredentialsInner.builder()
                .id(toStore.getCredential().getCredentialId().getBase64Url())
                .type("public-key")
                .nickName(toStore.getCredentialNickname().get())
                .registrationTime(toStore.getRegistrationTime().atOffset(ZoneOffset.UTC))
                .lastUsedTime(toStore.getLastUsedTime().atOffset(ZoneOffset.UTC))
                .iconURI((toStore.getIconURI().isPresent() ? toStore.getIconURI().get() : null))
                .isHighAssurance(toStore.isHighAssurance())
                .state(toStore.getState().getValue())
                .build());
  }
```

## Retrieving credentials

The `/user/credentials/{userName}` method is changed similarly:

```java
  public UserCredentialsResponse getUserCredentials(String userName) throws Exception {

    ...

      List<UserCredentialsResponseCredentialsInner> credList = credentials.stream()
          .map(cred -> UserCredentialsResponseCredentialsInner.builder()
              .id(cred.getCredential().getCredentialId().getBase64Url())
              .type("public-key")
              .nickName(cred.getCredentialNickname().get())
              .registrationTime(cred.getRegistrationTime().atOffset(ZoneOffset.UTC))
              .lastUsedTime(cred.getLastUsedTime().atOffset(ZoneOffset.UTC))
              .iconURI((cred.getIconURI().isPresent() ? cred.getIconURI().get() : null))
              .isHighAssurance(cred.isHighAssurance())
              .state(cred.getState().getValue())
              .build())
          .collect(Collectors.toList());
    }
```

Next, we'll have a closer look at authentication.
