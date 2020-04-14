#!/bin/sh

echo "Starting bootiful"

apk add git
git clone https://github.com/UniconLabs/bootiful-cas-client.git /bootiful

keytool -import -alias cas -file /tmp/tls.crt -keystore $JAVA_HOME/jre/lib/security/cacerts -storepass changeit -noprompt

cp /tmp/application.yml /bootiful/src/main/resources/
cd /bootiful

exec ./gradlew clean bootRun
