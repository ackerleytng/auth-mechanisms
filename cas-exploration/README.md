# CAS exploration setup

This makes use of CAS's docker installation and the bootiful project.

## Quickstart

Key things to look out for in the output of `docker-compose logs -f`:

+ cas should be `Ready to process requests`
+ cas should have
  + `Importing keystore /tmp/cas.p12 to /etc/cas/thekeystore` or
  + `Warning: Overwriting existing alias selfsignedcas in destination keystore`
+ cas should have `<Loaded [1] service(s) from [EmbeddedResourceBasedServiceRegistry].>`
+ bootiful should have
  + `Certificate was added to keystore` or
  + `keytool error: java.lang.Exception: Certificate not imported, alias <cas> already exists`
+ bootiful should have `Started BootifulCasClientApplication in <some number of seconds>`

Visit CAS at `https://cas.localhost`.

> That's the interface a user will get to sign into CAS - there is no built-in admin interface for CAS

If you're stuck at `Task :compileJava NO-SOURCE`, bootiful has not started yet. Give it a while more.

Only when bootiful has started up will going to `https://bootiful.localhost` work.

When you're there, test logging in as a user with

+ username: user
+ password: default

## Developing

Start the group of services as shown in the Quickstart.

The spring boot server is already set up to auto-reload when the class files change, but the class files change only when recompilation happens, so we have to start auto-rebuilding on code change with

```
docker-compose exec bootiful sh -c 'cd /bootiful && ./gradlew build --continuous'
```

I keep two windows open to watch the progress of autoreloading.
