# A few toy apps for testing authentication and authorization

## Quickstart

Start everything

```
docker-compose up -d
```

A script will automatically register the httpbin client and set up a user for httpbin.

Check that everything is up by watching

```
docker-compose logs -f
```

If you're playing with roles, you might want to give `user0` some client roles
through the keycloak web interface. You'll need to do that first if you're
working with `ace` or `whoami`.

### Browser

Start your browser, visit https://httpbin.localhost

Everything should load as per normal.

Now, in your address bar, try https://httpbin.localhost/get

You should get redirected to keycloak for a login. Log in with `user0` and `password`.

You should be prompted to change your password. Change it to whatever you like.

### Keycloak admin

Visit https://keycloak.localhost/

Log in with `admin` and `password`

## Notes

### Using kcadm.sh

To use `kcadm.sh`, exec into the keycloak container, then

```
export PATH=$PATH:/opt/jboss/keycloak/bin
```
