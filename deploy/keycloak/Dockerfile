# build passkey_authenticator.jar from source
FROM eclipse-temurin AS builder
COPY /source /source
RUN apt-get update; apt-get install -y maven; mvn -f /source/pom.xml clean package

# use a specific keycloak version as the SPI is internal and may change without notice
FROM quay.io/keycloak/keycloak:21.1.2
COPY --from=builder /source/target/passkey_authenticator-1.0.0.jar /opt/keycloak/providers/passkey_authenticator.jar
ENV CLIENT_URL $CLIENT_URL
COPY keycloak_init.sh /opt/keycloak/bin/keycloak_init.sh
USER root
RUN chmod +x /opt/keycloak/bin/keycloak_init.sh
ENTRYPOINT ./opt/keycloak/bin/keycloak_init.sh $CLIENT_URL & ./opt/keycloak/bin/kc.sh start-dev 
EXPOSE 3306
