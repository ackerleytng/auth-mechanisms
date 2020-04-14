#!/bin/sh

# Import certs so CAS can use this cert (will just overwrite if one already exists)
keytool -importkeystore -deststorepass changeit -destkeypass changeit -destkeystore /etc/cas/thekeystore -srckeystore /tmp/cas.p12 -srcstoretype PKCS12 -srcstorepass password -alias selfsignedcas -noprompt

# Start CAS itself
# Note that because of the way CAS parses the cas.authn.accept.users property, password cannot be password
exec java -server -noverify -Xmx2048M -jar cas.war \
     --server.port=8443 \
     --server.ssl.keyAlias=selfsignedcas \
     --server.servlet.contextPath=/ \
     --server.tomcat.accesslog.enabled=true \
     --cas.server.name='https://cas.localhost' \
     --cas.server.prefix='https://cas.localhost/' \
     --cas.serviceRegistry.initFromJson=true \
     --cas.serviceRegistry.watcherEnabled=true \
     --cas.serviceRegistry.json.location=file:///services \
     --cas.authn.accept.users=user::default,username::password
