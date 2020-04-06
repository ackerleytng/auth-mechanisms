# JWS (RFC 7515)

+ JSON Web Signature (JWS) represents content secured with digital signatures
  or Message Authentication Codes (MACs) using JSON
+ JWE cryptographic mechanisms provide integrity protection for an arbitrary
  sequence of octets

## JWS format

```
BASE64URL(UTF8(JWS Protected Header)) || '.' ||
BASE64URL(JWS Payload) || '.' ||
BASE64URL(JWS Signature)
```

### Building that up

+ JWE Protected Header
  + This is a JSON containing
    + `typ` Usually `"JWT"`, but could be omitted as well
    + `alg` the algorithm used to secure the JWS
+ JWS Payload
  + The JSON containing the claims
+ JWS Signature
  + The output of signing the JWS Signing Input (`ASCII(BASE64URL(UTF8(JWS
    Protected Header)) || '.' || BASE64URL(JWS Payload))`) with a key

### Flexibility

JWS has many parameters that can be declared in the JOSE Header, most are the
same as those in [JWE](./jwe.md).

## Interesting Security Considerations

### Replay Protection

Applications using JWS or JWE can thwart replay attacks by including a unique
message identifier as integrity-protected content in the JWS or JWE message,
and have the recipient verify that the message has not previously been received
or acted upon.
