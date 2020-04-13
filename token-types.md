# Token Types

> Ever wondered what `Bearer` means in `Authorization: Bearer <token>`?

> Others include: Basic, Digest, HOBA, OAuth, Mutual... See https://www.iana.org/assignments/http-authschemes/http-authschemes.xhtml

## Handle vs Assertion

How is the information content of a token represented in the token?

### Handle

+ A reference to some internal data structure within the authorization server
+ The resource server will then have to contact the authorization server to
  obtain/verify claims before permitting access
+ Usually referred to as an opaque token because the resource server does not
  need to be able to interpret the token
  + Resource server just uses the token to get information
+ Advantages:
  + Enables simple revocation and does not require cryptographic mechanisms to
    protect token content from being modified
+ Disadvantages:
  + Require communication between the issuing and consuming entity (e.g., the
    authorization server and resource server) in order to validate the token
    and obtain token-bound data
  + Might have a negative impact on performance and scalability
  + Usually used if the issuing and consuming entity are the same

### Assertion

+ A parseable token, contains information about the user and the client
+ Typically has a duration, audience, and is digitally signed in order to
  ensure data integrity and origin authentication
+ Can be directly validated and used by a resource server without interactions
  with the authorization server
  + Better performance and scalability, but token revocation is more difficult
+ Examples:
  + SAML (Security Assertion Markup Language) assertions
  + Kerberos Tickets

## Bearer vs Proof

How should the token be used? Must the resource server verify the requester?

### Bearer Token

+ A token that can be used by any client who has received the token
+ Mere possession is enough to use the token
+ Communication between endpoints must be secured to ensure that snooping
  parties cannot get and abuse the token

> Remember cheques? Anyone holding a cheque can bank the money into their own
>   account, unless the "Or Bearer" part is cancelled out

### Proof Token

+ A token that can only be used by a specific client
+ Use of the token requires the client to perform some action to prove it is an
  authorized user of the token. Examples:
  + MAC-type access tokens require the client to sign a resource request
    + Resource server will then verify the access token using a key
      (pre-negotiated or otherwise communicated)

## Source

+ RFC6819
