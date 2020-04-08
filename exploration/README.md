# A protected httpbin for testing

## Quickstart

Add this to your hosts file

```
127.0.0.1 keycloak.test
127.0.0.1 httpbin.test
127.0.0.1 httpbin-two.test
```

Start everything

```
docker-compose up -d
```

A script will automatically register the httpbin client and set up a user for httpbin.

Check that everything is up by watching

```
docker-compose logs -f
```

### Browser

Start your browser, visit https://httpbin.test:7443 (include the https part - Chrome will not add it automatically for you)

Everything should load as per normal.

Now, in your address bar, try https://httpbin.test:7443/get

You should get redirected to keycloak for a login. Log in with `user0` and `password`.

You should be prompted to change your password. Change it to whatever you like.

### Keycloak admin

Visit https://keycloak.test:8443/

Log in with `admin` and `password`

## Notes

### Using kcadm.sh

To use `kcadm.sh`, exec into the keycloak container, then

```
export PATH=$PATH:/opt/jboss/keycloak/bin
```
