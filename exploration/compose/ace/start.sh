#!/bin/sh

keytool -import -alias keycloak -file /tmp/tls.crt -keystore $JAVA_HOME/jre/lib/security/cacerts -storepass changeit -noprompt

exec ./gradlew clean bootRun
