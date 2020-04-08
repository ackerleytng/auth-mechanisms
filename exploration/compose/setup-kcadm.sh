#!/bin/sh

kcadm.sh config credentials --server http://keycloak:8080/auth --realm master --user admin --password password

# This won't create another realm if one with this name exists
kcadm.sh create realms -s realm=applications -s displayName=Applications -s enabled=true

# Create a client
result=$(kcadm.sh create clients -r applications -f - << EOF
{
  "clientId": "httpbin",
  "name": "httpbin",
  "enabled": true,
  "baseUrl": "https://httpbin.test:7443",
  "redirectUris": ["https://httpbin.test:7443/*"]
}
EOF
      )

if [ "$result" = "Client httpbin already exists" ]; then
    client_id=$(kcadm.sh get clients -r applications --fields id,clientId 2>/dev/null | grep -B1 httpbin | grep id | cut -d '"' -f 4)
else
    client_id=$(echo "$result" | grep -Eo "'.*'" | tr -d "'")
fi

# Get client secret
kcadm.sh get -r applications "clients/$client_id/client-secret" 2>/dev/null | grep value | cut -d '"' -f 4
