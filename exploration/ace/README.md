# Ace: A tiny api to get company information

Gatekeeper is used like a kong replacement - only `/` is whitelisted at
gatekeeper, so all other endpoints will be redirected to keycloak for
authentication.

Gatekeeper is also used to provide the logout endpoint, so to logout, just
browse to the following

```
https://ace.localhost/oauth/logout
```

With an access token, requests can then pass gatekeeper and reach ace. Ace will
then check for the access token for authenticated endpoints, and permit access
based on role in token.

Authorization was configured with the help of [Spring Security's Method Security](https://docs.spring.io/spring-security/site/docs/current/reference/html5/#jc-method)

| endpoint         | method | authenticated | role       | description                                                  |
| ---              | ---    | ---           | ---        | ---                                                          |
| `/`              | `GET`  | no            | any        | Landing "page", returns a json of all the apis you can reach |
| `/companies/`    | `GET`  | yes           | ace:list   | List all companies                                           |
| `/companies/:id` | `GET`  | yes           | ace:detail | Get detail of company with id :id                            |

## Configuring ca certs for ace's jvm

Take cert presented at `keycloak.localhost` and import it into the java cert store

```
keytool -printcert -sslserver keycloak.localhost -rfc | keytool -import -v -alias keycloak.localhost -keystore $JAVA_HOME/jre/lib/security/cacerts -storepass changeit -noprompt
```
