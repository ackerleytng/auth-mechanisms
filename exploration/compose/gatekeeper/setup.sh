#!/bin/sh

# Sets up the applications realm if it does not exist, then
# Sets up a client if it does not exist, then
# Writes client's client_secret to stdout

keycloak_url=${KEYCLOAK_URL:-https://keycloak.localhost}
export KEYCLOAK_USERNAME=${KEYCLOAK_USERNAME:-admin}
export KEYCLOAK_PASSWORD=${KEYCLOAK_PASSWORD:-password}
client_roles=$@

if [ -z $GATEKEEPER_CLIENT_ID ]; then
    echo ">>> GATEKEEPER_CLIENT_ID must be set!"
    exit 1
fi

script_dir=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)

# Get the token (default: expires in 1 minute)
token=$($script_dir/get-token.sh)

# Wait up to 30s until applications realm is created
for i in $(seq 30); do
    echo "Wait for applications realm to be created... $i" 1>&2
    return_code=$(curl -X GET -o /dev/null -H "Content-Type: application/json" -H "Authorization: Bearer $token" -w '%{http_code}' -sSk "$keycloak_url/auth/admin/realms/applications")
    [ "$return_code" -eq "200" ] && break
    sleep 1;
done

# Create get id of client first
client_id=$(curl -X GET -H "Content-Type: application/json" -H "Authorization: Bearer $token" -sSk "$keycloak_url/auth/admin/realms/applications/clients" | jq -r ".[] | select(.clientId == \"${GATEKEEPER_CLIENT_ID}\") | .id")

# Get client's id
if [ -z $client_id ]; then
    # Create a client
    echo "Creating client $GATEKEEPER_CLIENT_ID" 1>&2
    curl -X POST -H "Content-Type: application/json" -H "Authorization: Bearer $token" -sSk "$keycloak_url/auth/admin/realms/applications/clients" --data-binary @- >/dev/null <<EOF
{
  "clientId": "${GATEKEEPER_CLIENT_ID}",
  "name": "${GATEKEEPER_CLIENT_ID}",
  "enabled": true,
  "baseUrl": "https://${GATEKEEPER_CLIENT_ID}.localhost",
  "directAccessGrantsEnabled": true,
  "redirectUris": ["https://${GATEKEEPER_CLIENT_ID}.localhost/*"]
}
EOF

    # Get the id again
    client_id=$(curl -X GET -H "Content-Type: application/json" -H "Authorization: Bearer $token" -sSk "$keycloak_url/auth/admin/realms/applications/clients" | jq -r ".[] | select(.clientId == \"${GATEKEEPER_CLIENT_ID}\") | .id")

    # Add the Audience mapper for this client (aud claim should be the same as client_id)
    echo "Adding audience mapper for client" 1>&2
    curl -H "Content-Type: application/json" -H "Authorization: Bearer $token" -sSk "$keycloak_url/auth/admin/realms/applications/clients/$client_id/protocol-mappers/models" --data-binary @- >/dev/null <<EOF
{
  "protocol": "openid-connect",
  "config": {
    "id.token.claim": "false",
    "access.token.claim": "true",
    "included.client.audience": "${GATEKEEPER_CLIENT_ID}"
  },
  "name": "${GATEKEEPER_CLIENT_ID}-aud",
  "protocolMapper": "oidc-audience-mapper"
}
EOF

    # Add any requested client roles
    for role in $client_roles; do
        echo "Adding role $role to client" 1>&2
        curl -H "Content-Type: application/json" -H "Authorization: Bearer $token" -sSk "$keycloak_url/auth/admin/realms/applications/clients/$client_id/roles" --data-binary "{\"name\": \"$role\"}"
    done
fi

# Get client's secret
curl -X GET -H "Content-Type: application/json" -H "Authorization: Bearer $token" -sSk "$keycloak_url/auth/admin/realms/applications/clients/$client_id/client-secret" | jq -r '.value'
