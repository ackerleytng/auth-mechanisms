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

Only when bootiful has started up will going to `https://bootiful.localhost` work

Visit CAS at `https://cas.localhost`.

> That's the interface a user will get to sign into CAS - there is no built-in admin interface for CAS
