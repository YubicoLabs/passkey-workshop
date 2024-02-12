---
sidebar_position: 1
---

This section will cover an example Relying Party application for a high assurance scenario. 
The Relying Party is based on a fictional bank that allows users to leverage any passkey type, but requires a high assurance passkey for sensitive transactions. 
This section will cover what needs to be changed compared to the non-high assurance version of this workshop.

# Prerequisite knowledge

Before you continue into this section, please ensure that you review the previous section on 
[Relying Party implementations](/docs/category/relying-party).
The previous sections provide generic implementation guidance on how to register, authenticate, and manage passkeys using a common Relying Party API. 

From this point on, this section assumes that you know how the relying party API works.

# Overview

The banking application will need to distinguish different levels of assurance (LoAs) as discussed in [Assurance Levels](docs/high_assurance/assurance), 
and implement Step up authentication as described in the section in [Architecture](/docs/high_assurance/architecture).

For our banking application, we use very simple criteria for distinguishing levels of assurance: we assume a low level of assurance by default,
and consider passkeys stored on authenticators listed in the FIDO Metadata Service (MDS) as high assurance.
This typically means that syncable passkeys are associated with a low LoA,
whereas passkeys stored on security keys are associated with a high LoA.
This can of course be refined further by including properties of authenticators as listed in MDS, 
such as listed FIDO certifications or hardware protection features, but we will keep it simple here.

All components in our architecture will need to be updated to implement our High Assurance policy, but we can break this down into three steps:

1. When registering a passkey, we need to determine and store the level of assurance of the authenticator used to store the passkey.
2. When authenticating using a previously registered passkey, we need to retrieve its level of assurence and communicate that back to the banking application.
3. When performing bank transactions, the level of assurence needs to be considered when enforcing the bank's policy on authorizing transactions.

We will dedicate separate sections to these three steps below.

# Registration

We already discussed attestation, the FIDO Metadata Service (MDS), and how to add attestation support to a Relying Party in the section on
[attestation](/docs/category/attestation), so we will not repeat that here.

This time however, we will use the fact that an authenticator is listed in MDS to determine whether or not the authenticator (and thus its stored passkeys) is to be considered High Assurance. For this, we will introduce a new property called `isHighAssurance` that we will store for each credential registered.

## API

We begin by extending the passkey API that was described in [API definition](docs/relying-party/api-def).
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

We continue with data sources, based on what we described earlier in [Data sources and RP configurations](docs/relying-party/config-and-data).
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
The `isHighAssurance` property introduced here is a property of the credential (passkey).
:::::

:::::danger
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

We will need to persist this information in the database.
We update the table schema that is utilized for our credential repository (see [our initial version](/docs/architecture/relying-party.md)) accordingly:

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

Similar to what was explained in [registration flow](docs/relying-party/reg-flow.md), the implementation of the `attestation/result` method simply returns the credentials properties, now including its high availability status:

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

The `user/credentials/{userName}` method is similar:

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

# Authentication

TODO: why use a different propery `loa` instead of `isHighAssurance`?

When signing in with a passkey, we want to use this credential's `isHighAssurance` property to define a Level of Assurance (LoA) for the authentication event.
This LoA is then returned to the banking application to enforce its policy for authorizing transactions.

In our architecture, authentication is performed by the OpenID Connect Provider, a role performed by Keycloak.
As explained in the [Architecture section](docs/high_assurance/architecture), Keycloak supports ACR claims to convey information
about the strength of the mechanism used for authenticating the user.
We will use the  `isHighAssurance` property of credentials stored during registration to return this LoA during authentication.
This of course requires another couple of changes to our Relying Party implementation, as well as to passkey authentication module used by KeyCloak.

## API

Let's first look at the definition of our passkey API, in particular the `assertion/result` method.

Before, this method's [response](/docs/relying-party/auth-flow#response-1) simply returned a `status` to denote the result of the authentication ceremony.
Now, we need to extend this reponse to also include the LoA we assigned to this authentication ceremony, determined by the properties of the credential used during authentication.

For instance, when authenticating with a passkey that was assigned a high assurence, the value `2` is returned in an `loa` response property:

```json
{
  "status": "ok",
  "loa": "2"
}
```

Here, `1` indicates a low LoA, and `2` indicates a high LoA.
We capture this in the following enum Type:

```java
  public enum loaEnum {
    HIGH(2),
    LOW(1);

    private int value;

    loaEnum(int value) {
      this.value = value;
    }

    @JsonValue
    public int getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static loaEnum fromValue(int value) {
      for (loaEnum b : loaEnum.values()) {
        if (b.value == value) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }

  }
```

## Data Sources

We do not need any changes to pur Data Sources - everything we need is already covered with registration.

## Authentication flow

We do need to extend the implementation of the Authentication flow as it was discussed in our earlier section on [Authentication flows](docs/relying-party/auth-flow#implementation-1).
Fortunately, the changes are trivial: we just need to retrieve the `isHighAssurance` status of the credential used, and translate that to an `loa` response property:

```java
public AssertionResultResponse assertionResponse(AssertionResultRequest response) throws Exception {

    ... 

        CredentialRegistration usedCredentialRegistration = relyingPartyInstance.getStorageInstance()
            .getCredentialStorage().getByCredentialId(result.getCredential().getCredentialId()).stream().findFirst()
            .get();

        loaEnum resultLoa = usedCredentialRegistration.isHighAssurance() ? loaEnum.HIGH : loaEnum.LOW;

        return AssertionResultResponse.builder().status("ok").loa(resultLoa).build();
}
```

Keycloak will use the `loa` value returned by the `assertion/result` method in its authentication module to create the `acr` value.
This is rather specific to Keycloak, but for completeness, this is implemented using Keycloak's `AuthenticationFlowContext` and `AcrStore` classes:

```java
        AcrStore acrStore = new AcrStore(context.getAuthenticationSession());
        acrStore.setLevelAuthenticated(assertionResponse.getLoa());
```

# Bank Transactions

As explained in the [Architecture section](docs/high_assurance/architecture), the banking API (or the OAuth2 Resource Server as it is also called)
can use the ACR value to determine the Level of Assurance associated with the authentication ceremony in order to implement a policy for authorizing bank transactions.

Just like our Webauthn Relying Party application, the banking API is implemented using [Spring Boot](https://spring.io/projects/spring-boot).
This means we do not need to implement any OAuth2 flows, as they are available as a module in [Spring Security](https://spring.io/projects/spring-security).
Best of all, an OAuth2 Resource Server can be implemented very easily with [minimal configuration](https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html#oauth2resourceserver-jwt-minimalconfiguration).

We simply point to our Keycloak instance using the following configuration file in our Spring Boot `application.properties` file:

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          jwk-set-uri: http://localhost:8081/realms/BankApp/protocol/openid-connect/certs
```

The access tokens issued by our OAuth2 Autohorization Server are self-contained JWT tokens.
The `jwk-set-uri` points to the keys required to verify these JWT tokens.

Next, we can set a custom JWT configuration.
Here, we will require an access token for any requests to our banking API.

For instance:

```java

@Configuration
@EnableWebSecurity
public class ResourceServerSecurityConfiguration {

        @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
        String jwkSetUri;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                // @formatter:off
                http
                .authorizeHttpRequests((authorize) -> authorize
                                .antMatchers("/v1/status").permitAll()
                                .antMatchers("/v1/**").authenticated() // TODO: filter on scopes if necessary
                                .anyRequest().permitAll()
                )
                .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);
                // @formatter:on
                http.cors();    // bypass authorization checks for preflight checks
                return http.build();
        }

        @Bean
        JwtDecoder jwtDecoder() {
                return NimbusJwtDecoder.withJwkSetUri(this.jwkSetUri).build();
        }

}

```



- Bank API = V1ApiController.java - getAcr() returns jwt.acr, createTransaction check LoA






##### bank_app/src/main/java/com/yubicolabs/bank_app/api/V1ApiController.java

```java

bankOperations.createTransaction(getAcr(), transactionCreateRequest.getType().getValue(),
                            transactionCreateRequest.getAmount().doubleValue(),
                            transactionCreateRequest.getDescription(), getUserHandleFromJwt())


  public TransactionCreateResponse createTransaction(int acr, String type, double amount, String description,
      String userhandle)
      throws Exception { ... }


      if (amount >= 1000 && acr < 2) {
        throw new AuthenticationException("User does not have the correct permissions. Please reauthenticate");
      }


```