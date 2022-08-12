# The goal of this Dockerfile is to cutomize Keycloak image with custom
# realm and themes
FROM quay.io/keycloak/keycloak:18.0.2
ADD themes/omni-ops /opt/keycloak/themes/omni-ops
ADD realms /opt/keycloak/data/import
