FROM quay.io/keycloak/keycloak:26.2.0

COPY keycloak_init.sh /opt/keycloak/bin/keycloak_init.sh

USER root
RUN chmod +x /opt/keycloak/bin/keycloak_init.sh
USER keycloak

ENTRYPOINT ["/bin/bash", "-c", "/opt/keycloak/bin/keycloak_init.sh & exec /opt/keycloak/bin/kc.sh start-dev"]
