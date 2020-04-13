# Toy App

+ Built to test integration with CAS
+ Has two endpoints:
  + `/`: Landing page
    + Also try `/?name=alice`
  + `/protected`: The protected resource

# Getting Started

### Running in dev mode

```
./mvnw spring-boot:run
```

Then visit `http://localhost:8080/` in a browser.

### Packaging (for dockerization)

```
./mvnw package
```

### Testing package

```
java -jar target/toy-app-0.0.1-SNAPSHOT.jar
```
