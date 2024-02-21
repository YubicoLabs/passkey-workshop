# build passkey_authenticator.jar from source
FROM eclipse-temurin AS builder
COPY /source /source
COPY /bank_source /bank_source
RUN apt-get update; apt-get install -y maven; mvn -f /source/pom.xml clean package; mvn -f /bank_source/pom.xml clean package

# use a specific keycloak version as the SPI is internal and may change without notice
FROM quay.io/keycloak/keycloak:21.1.2
COPY --from=builder /source/target/passkey_authenticator-1.0.0.jar /opt/keycloak/providers/passkey_authenticator.jar
COPY --from=builder /bank_source/target/keycloak-1.0.0.jar /opt/keycloak/providers/passkey_spi.jar
ENV DEMO_CLIENT_URL $DEMO_CLIENT_URL
ENV BANK_CLIENT_URL $BANK_CLIENT_URL
ENV RP_API $RP_API
COPY keycloak_init.sh /opt/keycloak/bin/keycloak_init.sh
USER root
RUN chmod +x /opt/keycloak/bin/keycloak_init.sh
# proxy edge is required when using a devtunnel
ENTRYPOINT ./opt/keycloak/bin/keycloak_init.sh & ./opt/keycloak/bin/kc.sh start-dev --proxy edge --log-level=WARN
EXPOSE 3306
