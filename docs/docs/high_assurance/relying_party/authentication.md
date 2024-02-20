---
sidebar_position: 3
---

# Authentication

This section covers authentication for a high assurance scenario.

When signing in with a passkey, we want to use this credential's `isHighAssurance` property to define a Level of Assurance (LoA) for the authentication event.
This LoA is then returned to the banking application to enforce its policy for authorizing transactions.

In our architecture, authentication is performed by the OpenID Connect Provider, a role performed by Keycloak.
As explained in the [Architecture section](/docs/high_assurance/architecture), Keycloak supports ACR claims to convey information
about the strength of the mechanism used for authenticating the user.
We will use the  `isHighAssurance` property of credentials stored during registration to return this LoA during authentication.
This of course requires another couple of changes to our Relying Party implementation, as well as to passkey authentication module used by KeyCloak.

## API

Let's first look at the definition of our passkey API, in particular the `assertion/result` method.

Before, this method's [response](/docs/relying-party/auth-flow#response-1) simply returned a `status` to denote the result of the authentication ceremony.
Now, we need to extend this response to also include the LoA we assigned to this authentication ceremony, determined by the properties of the credential used during authentication.

For instance, when authenticating with a passkey that was assigned a high assurance level, the value `2` is returned in an `loa` response property:

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

We do not need any changes to our Data Sources - everything we need is already covered with registration.

## Authentication flow

We do need to extend the implementation of the Authentication flow that was discussed in our earlier section on [Authentication flows](/docs/relying-party/auth-flow#implementation-1).
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
