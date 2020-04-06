# JWE (RFC 7516)

+ JSON Web Encryption (JWE) represents encrypted content using JSON
+ JWE cryptographic mechanisms encrypt and provide integrity protection for an
  arbitrary sequence of octets

## Terminology

+ AEAD Algorithm
  + Regular encryption algorithms turn (plaintext, key) => ciphertext
  + An AEAD algorithm, in addition, also
    + Takes Additional Authenticated Data
    + Provides integrated content integrity check over the ciphertext and
      Additional Authenticated Data
    + Returns two outputs, the Authentication Tag in addition to the ciphertext
  + AEAD algorithms simultaneously assure both confidentiality and authenticity
    of data
    + Defends against chosen plaintext attack, or
    + When plaintext is binary - changing the ciphertext can result in
      seemingly legitimate decrypted plaintext
  + Some approaches
    + Encrypt-then-MAC (different keys), return both together
    + Encrypt-and-MAC (same key), return both together
    + MAC-then-Encrypt (same key), return single ciphertext

## JWE format

```
BASE64URL(UTF8(JWE Protected Header)) || '.' ||
BASE64URL(JWE Encrypted Key) || '.' ||
BASE64URL(JWE Initialization Vector) || '.' ||
BASE64URL(JWE Ciphertext) || '.' ||
BASE64URL(JWE Authentication Tag)
```

### Building that up

+ JWE Protected Header
  + This is a JSON containing
    + `alg` the algorithm used to encrypt the Content Encryption Key to become
      JWE Encrypted Key
    + `enc` the algorithm used for the ciphertext and Authentication Tag
+ Content Encryption Key (CEK): Randomly generated
+ JWE Initialization Vector: Randomly generated
+ JWE Encrypted Key
  + Is the CEK, encrypted
  + Will encrypt using the recipient's public key, algorithm in `alg`
+ Additional Authenticated Data
  + `ASCII(BASE64URL(UTF8(JWE Protected Header)))`
+ JWE Ciphertext
  + Encrypt plaintext with CEK, JWE Initialization Vector, Additional Authenticated Data
  + Get JWE Ciphertext and JWE Authentication Tag

## Flexibility of JWE

JWE provides many different parameters that can be declared in the JOSE Header

+ JWE can be compressed - specify the compression algorithm in the `zip` header parameter
+ To communicate the public key that was used to encrypt the CEK, use either
  + `jku` - the URI pointing to a set of JSON-encoded public keys containing
    the one used for encryption
  + `jwk` - The public key itself
  + `kid` - The key ID
  + `x5u` - the URI pointing to the X.509 public key certificate or certificate
    chain containing the public key used for encryption
  + `x5c` - The certificate chain, as a JSON
  + Some others, like certificate thumbprint
+ Different ways of managing keys: Key Management Mode
  + Key Encryption: CEK is encrypted using asymmetric encryption
  + Key Wrapping: CEK is encrypted using symmetric key algorithm
  + Direct Key Agreement: Just agree on the CEK value
  + Key Agreement with Key Wrapping: Agree on a symmetric key used to encrypt CEK
  + Direct Encryption: Use an agreed symmetric key in place of CEK
+ When Direct Encryption or Direct Key Agreement is used, `JWE Encrypted Key`
  will just be empty
+ If the encryption algorithm does not need an initialization vector, `JWE
  Initialization Vector` will just be empty
+ If the algorithm does not use an Authentication Tag, `JWE Authentication Tag`
  will just be empty
