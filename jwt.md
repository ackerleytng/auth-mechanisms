# JWT (RFC 7519)

JWTs encode claims to be transmitted as JSON. Used as

+ The payload of a JSON Web Signature (JWS) structure
+ Plaintext of a JSON Web Encryption (JWE) structure

Claims can be digitally signed or integrity protected with a Message
Authentication Code (MAC) or encrypted.

JWTs are always represented using the JWS Compact Serialization or JWE Compact
Serialization.

## Overview

+ JWTs represent a set of claims as a JSON object
+ Names are always strings, values are arbitrary JSON values
+ Contents of the JSON Object Signing and Encryption (JOSE) Header describe the
  cryptographic operations applied to the JWT claims set
  + If the JOSE Header is for a JWS
    + The JWT is represented as a JWS
    + The claims are digitally signed or MACed
    + JWT Claims Set is the JWS Payload
  + If the JOSE Header is for a JWE
    + The JWT is represented as a JWE
    + Claims are encrypted
    + JWT Claims Set is the plaintext encrypted by the JWE
  + JWT can be nested in a JWE or JWS structure => nested JWT
+ JWT is represented as a sequence of URL-safe parts separated by '.'
  + Each part is a base64url-encoded value
  + Number of parts dependent on the representation of resulting JWE or JWS

### Example

#### JOSE Header

+ Encoded object is a JWT
+ JWT is a JWS, MACed using the HMAC SHA-256 algorithm

```
{"typ": "JWT",
 "alg": "HS256"}
```

#### JWT Claims Set

```
{"iss":"joe",
 "exp":1300819380,
 "http://example.com/is_root":true}
```

#### MACing

1. Compute the MAC of the encoded JOSE Header, encoded JWS Payload
2. base64url encode the HMAC value
3. Concatenate with '.' to yield a complete JWT

```
eyJ0eXAiOiJKV1QiLA0KICJhbGciOiJIUzI1NiJ9
.
eyJpc3MiOiJqb2UiLA0KICJleHAiOjEzMDA4MTkzODAsDQogImh0dHA6Ly9leGFt
cGxlLmNvbS9pc19yb290Ijp0cnVlfQ
.
dBjftJeZ4CVP-mB92K27uhbUJU1p1r_wW1gFWFOEjXk
```

### JOSE Header

+ `typ`: Type. Optional. Declares the media type of this complete JWT. Usually
  just `"JWT"`
+ `cty`: Content Type. Convey structural information. If nested
  signing/encryption is not employed, don't use this header. If it is employed,
  must include this header with value `"JWT"`
+ Unencrypted replicas of claims, like `iss`, `sub`, `aud`

## Unsecured JWTs

Are possible. Use `"none"` for `"alg"`.

## Creating JWTs

1. Create a JWT Claims Set. Whitespace is allowed.
2. Let the Message be the UTF-8 representation
3. Create a JOSE Header
4. Do the signing/encryption based on steps in JWS/JWE
5. If nested, repeat from step 3, add `cty`

## Validating JWTs

1. Verify that JWT contains at least 1 '.'
2. Let the encoded JOSE header be the part before the first '.'
3. Decode the encoded JOSE header
4. Verify that it is a valid JSON
5. Verify JOSE header parameters
6. Determine if JWT is JWT or JWS
7. Decode accordingly
8. If JOSE Header contains `cty`, return to step 1
9. Otherwise, base64url decode the Message
10. Verify that the result is a valid JSON
