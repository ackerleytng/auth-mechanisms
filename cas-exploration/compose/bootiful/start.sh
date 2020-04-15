#!/bin/sh

keytool -import -alias cas -file /tmp/tls.crt -keystore $JAVA_HOME/jre/lib/security/cacerts -storepass changeit -noprompt

cd /bootiful

exec ./gradlew clean bootRun
