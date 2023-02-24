Cursom authentication flows can be implemented using its Authentication Service PRovider Interface (SPI).

See [documentation](https://www.keycloak.org/docs/latest/server_development/#_auth_spi)

An example of an implementation of an Authentication SPI can be found
[here](https://github.com/PacktPublishing/Keycloak-Identity-and-Access-Management-for-Modern-Applications/tree/master/ch13/simple-risk-based-authenticator/src/main/java/org/keycloak/book/ch13/authentication)

A Video of its deployment, [here](https://youtu.be/y8a5tA8Cs68)

It seems the inferface has changed somewhat, as the example code (written for Keycloak 12) doesn't work as is on Keycloak 20.0.3
