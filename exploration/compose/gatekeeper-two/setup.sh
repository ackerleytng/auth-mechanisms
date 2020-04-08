#!/bin/sh

# Sets up the applications realm if it does not exist, then
# Sets up a httpbin client if it does not exist, then
# Writes httpbin's client_secret to stdout

keycloak_url=${KEYCLOAK_URL:-https://localhost:8443}

# Get the token (default: expires in  1 minute)
token=$(curl -X POST -H 'Accept: application/json' -sSk "$keycloak_url/auth/realms/master/protocol/openid-connect/token" -d 'client_id=admin-cli&password=password&username=admin&grant_type=password' | cut -d '"' -f4)

# Create a realm - will just not create if it already exists
curl -X POST -H "Content-Type: application/json" -H "Authorization: Bearer $token" -sSk "$keycloak_url/auth/admin/realms" -d '{"displayName": "applications", "enabled": true, "realm": "applications"}' > /dev/null

# Create a client - will just not create if it already exists
curl -X POST -H "Content-Type: application/json" -H "Authorization: Bearer $token" -sSk "$keycloak_url/auth/admin/realms/applications/clients" -d '{"clientId": "httpbin-two", "name": "httpbin-two", "enabled": true, "baseUrl": "https://localhost:6443", "redirectUris": ["https://localhost:6443/*"]}' > /dev/null

# Create user for this client with temporary password "password" (user will have to change on first login)
curl -X POST -H "Content-Type: application/json" -H "Authorization: Bearer $token" -sSk "$keycloak_url/auth/admin/realms/applications/users" -d '{"username": "user0", "firstName": "User", "lastName": "Zero", "email": "user0@mail.com", "emailVerified": true, "enabled": true, "credentials": [ { "type": "password", "value": "password", "temporary": true } ] }' > /dev/null

# Get httpbin's id
client_id=$(curl -X GET -H "Content-Type: application/json" -H "Authorization: Bearer $token" -sSk "$keycloak_url/auth/admin/realms/applications/clients" | jq -r '.[] | select(.clientId == "httpbin-two") | .id')

# Add the Audience mapper for this client (aud claim should read httpbin)
curl -H "Content-Type: application/json" -H "Authorization: Bearer $token" -sSk "$keycloak_url/auth/admin/realms/applications/clients/$client_id/protocol-mappers/models" --data-binary '{"protocol":"openid-connect","config":{"id.token.claim":"false","access.token.claim":"true","included.client.audience":"httpbin-two"},"name":"httpbin-aud","protocolMapper":"oidc-audience-mapper"}' > /dev/null

# Get httpbin's secret
curl -X GET -H "Content-Type: application/json" -H "Authorization: Bearer $token" -sSk "$keycloak_url/auth/admin/realms/applications/clients/$client_id/client-secret" | jq -r '.value'
