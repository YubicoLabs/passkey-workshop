---
sidebar_position: 4
---

# Banking Application

This section covers an example of a protected API for a high assurance scenario.

# Transactions

As explained in the [Architecture section](/docs/high_assurance/architecture), the banking API (or the OAuth2 Resource Server as it is also called)
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

TODO: also set filters for audience etc.

The access tokens issued by our OAuth2 Authorization Server are self-contained JWT tokens.
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

## API

The banking API is kept very simple.
If you deployed the banking application on `localhost`, you can find its OpenAPI definition [here](http://localhost:8082/).

All API methods require an OAuth2 access token for authorization.
Some API calls require an access token with an `acr` claim indicating that authentication was performed with a passkey on a high assurance level.
Most is the case with the `account/{accountId}/transations` method, where transactions involving a transfer of low amounts of money can be performed
with any LoA, but transactions involving more that $1000 must be authorized with a high LoA.

The ACR value is extracted from the JWT token with the `getAcr()` method defined in the API controller:

```java
    private int getAcr() {
        JwtAuthenticationToken token = (JwtAuthenticationToken) SecurityContextHolder.getContext()
                .getAuthentication(); 
        Jwt jwt = (Jwt) token.getCredentials();
        return Integer.parseInt((String) jwt.getClaims().get("acr"));
    }
```

:::::note
When extracting claims from JWT tokens, it is important that the token's signature has been verified.
In our implementation, this is performed automatically using the Spring Security module.
:::::

The check for the current assurance level is implemented in the `createTransaction()` method as follows:

```java
  public TransactionCreateResponse createTransaction(
    int acr, String type, double amount, String description, String userhandle) throws Exception {
        
    ...

    if (amount >= 1000 && acr < 2) {
      throw new AuthenticationException("User does not have the correct permissions. Please reauthenticate");
    }

    ...
}
```

TODO:  generate an RFC-9470 compliant error response header including required loa

The generated `AuthenticationException` will result in an HTTP response error with status 401 (`Unauthorized`),
and the required level of assurance (`acr_values="2"`),
after which the client is expected to obtain a new access token with the higher LoA.
