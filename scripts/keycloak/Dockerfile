FROM quay.io/keycloak/keycloak
COPY /build/passkey_authenticator.jar /opt/keycloak/providers/passkey_authenticator.jar
COPY keycloak_init.sh /opt/keycloak/bin/keycloak_init.sh
USER root
RUN chmod +x /opt/keycloak/bin/keycloak_init.sh
ENTRYPOINT ./opt/keycloak/bin/keycloak_init.sh & ./opt/keycloak/bin/kc.sh start-dev 
EXPOSE 3306