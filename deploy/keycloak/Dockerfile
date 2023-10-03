FROM quay.io/keycloak/keycloak:21.0.1
COPY passkey_authenticator.jar /opt/keycloak/providers/passkey_authenticator.jar
COPY passkey_spi.jar /opt/keycloak/providers/passkey_spi.jar
COPY keycloak_init.sh /opt/keycloak/bin/keycloak_init.sh
RUN mkdir -p /opt/keycloak/data/import
COPY bank_realm_export.json /opt/keycloak/data/import/bank_realm_export.json
USER root
RUN chmod +x /opt/keycloak/bin/keycloak_init.sh
ENTRYPOINT ./opt/keycloak/bin/keycloak_init.sh & ./opt/keycloak/bin/kc.sh start-dev --import-realm
EXPOSE 3306
