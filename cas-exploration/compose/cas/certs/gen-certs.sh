#!/bin/bash

script_dir="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

# Generate self-signed key and cert using openssl
openssl req -x509 -newkey rsa:4096 -keyout tls.key -out tls.crt -days 365 -subj "/CN=cas.localhost" -addext "subjectAltName=DNS:cas.localhost" -nodes

# Convert to pkcs12 keystore for keytool to import
openssl pkcs12 -export -out cas.p12 -inkey tls.key -in tls.crt -password pass:password -name selfsignedcas
