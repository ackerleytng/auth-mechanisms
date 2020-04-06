# OpenID Connect

+ Is an identity layer on top of the OAuth 2.0 protocol
+ Allows clients to
  + Verify the identity of the End-User based on the authentication performed by an authorization server
  + Obtain basic profile information about the End-User in an interoperable and REST-like manner

## Core Specification

+ Defines the core OpenID Connect functionality:
  + Authentication built on top of OAuth 2.0
  + The use of Claims to communicate information about the End-User
+ Describes the security and privacy considerations for using OpenID Connect
+ Use of this extension is requested by Clients by including the `openid` scope value in the Authorization Request
+ Information about the authentication performed is returned in a JSON Web Token (JWT)

### Parties

+ OAuth 2.0 Authentication Servers implementing OpenID Connect are also referred to as OpenID Providers (OPs)
+ OAuth 2.0 Clients using OpenID Connect are also referred to as Relying Parties (RPs)

## ID Token

+ The primary extension that OpenID Connect makes to OAuth 2.0 to enable
  End-Users to be Authenticated is the ID Token data structure
+ Is a security token that contains Claims about the Authentication of an
  End-User by an Authorization Server when using a Client
+ Claims used within the ID Token
  + `iss`: Issuer Identifier for the issuer of the response. A case sensitive
    URL using the `https` scheme that contains scheme, host, and optionally,
    port number and path components and no query or fragment components
  + `sub`: Subject Identifier. A locally unique and never reassigned identifier
    within the Issuer for the End-User. Case-sensitive
  + `aud`: Audience(s) that this ID Token is intended for. It MUST contain the
    OAuth 2.0 client_id of the Relying Party as an audience value. MAY also
    contain identifiers for other audiences
  + `exp`: Expiration time on or after which the ID Token MUST NOT be accepted
    for processing
  + `iat`: Issued At. Time at which the JWT was issued (unix epoch time, UTC)
  + `auth_time`: Time when the End-User authentication occurred (unix epoch
    time, UTC)
  + `nonce`: String value used to associate a Client session with an ID Token,
    and to mitigate replay attacks. The value is passed through unmodified from
    the Authentication Request to the ID Token. If present in the ID Token,
    Clients MUST verify that the nonce Claim Value is equal to the value of the
    nonce parameter sent in the Authentication Request
  + `acr`: Authentication Context Class Reference. "0" for not meeting
    requirements, such as a long-lived browser cookie, otherwise an absolute
    URI or an RFC 6711 [RFC6711] registered name SHOULD be used as the acr
    value. Case sensitive.
  + `amr`: Authentication Methods References. JSON array of strings that are
    identifiers for authentication methods used in the authentication. For
    instance, values might indicate that both password and OTP authentication
    methods were used
  + `azp`: Authorized party - the party to which the ID Token was issued. If
    present, it MUST contain the OAuth 2.0 Client ID of this party

+ Must be signed using JWS, optionally both signed and encrypted to provide
  authentication, integrity, non-repudiation, and optionally, confidentiality

## Authentication

+ OpenID Connect performs authentication to log in the End-User or to determine
  that the End-User is already logged in.
+ OpenID Connect returns the result of the Authentication performed by the
  Server to the Client in a secure manner so that the Client can rely on it

### Authentication using Authorization Code Flow

+ Steps
  1. Client prepares an Authentication Request containing the desired request
     parameters.
  2. Client sends the request to the Authorization Server.
     + Can be done by sending the User Agent to the Authorization Server's
       Authorization Endpoint
     + Communication with Authorization Endpoint must use TLS
  3. Authorization Server Authenticates the End-User.
  4. Authorization Server obtains End-User Consent/Authorization.
  5. Authorization Server sends the End-User back to the Client with an
     Authorization Code.
     + Authorization Response must return parameters by adding them as query
       parameters to the `redirect_uri` in the Authorization Request
  6. Client requests a response using the Authorization Code at the Token
     Endpoint.
  7. Client receives a response that contains an ID Token and Access Token in
     the response body.
  8. Client validates the ID token and retrieves the End-User's Subject
     Identifier.
+ Authentication Request parameters
  + `scope`: Must contain `openid` scope value
  + `response_type`: Used to determine authorization processing flow. When
    using authorization code flow, this value is `code`
  + `client_id`: Client Identifier. (pre-registered at authorization server)
  + `redirect_uri` Redirection URI to which the response will be
    sent. (pre-registered at authorization server)
  + `state`: Opaque value to maintain state between request and callback, used
    to mitigate CSRF
  + `response_mode`: Informs the Authorization Server of the mechanism to be
     used for returning Authorization Response parameters from the
     Authorization Endpoint. (`query` for query parameter or `fragment`)
+ Interesting optional parameters
  + `prompt`: Prompting instructions for the Authorization Server. can be used
    by the client to make sure the End-User is still present for the current
    session or to bring attention to the request
    + `none`: Don't display authentication or user content pages. If user is
      not already authenticated, return error. Can be used to cheeck for
      existing authentication or consent
    + `login`: Prompt user for reauthentication (a bit like CAS' `renew` parameter)
    + `consent`: Must prompt user for consent, otherwise return error
    + `select_account`: Prompt End-User to select account, otherwise return error
  + `max_age`: Maximum Authentication Age. Specifies the allowable elapsed time
    in seconds since the last time the End-User was actively authenticated by
    the OP. If the elapsed time is greater than this value, the OP MUST attempt
    to actively re-authenticate the End-User
+ The methods used by the Authorization Server to Authenticate the End-User
  (e.g. username and password, session cookies, etc.) are beyond the scope of
  this specification
+ Authentication Response parameters
  + `state`: For state management, usually CSRF protection
  + `code`: The Authentication Code
+ Getting tokens (Access Token, ID Token (OpenID Connect part), Refresh Token)
  + Get it from the Token Endpoint
  + Must use TLS
  + No change from OAuth 2.0 other than inclusion of `id_token` in response
+ ID Token Validation by Client
  + If encrypted, decrypt it using the keys/algorithms specific during client
    registration with OP. If it was negotiated during registration but client
    received unencrypted token, client must reject
  + Validate that `iss` claim matches OpenID Provider (agreed during
    registration)
  + Validate `aud` claim contains `client_id`. `aud` can be a list. Reject if
    not in list.
    + If there are multiple claims, there must be an `azp` claim
  + If `azp` is present, `azp` must match `client_id`
  + If ID Token is received via direct communication, the TLS server validation
    MAY be used to validate the issuer, in place of checking the token
    signature
  + `alg` should default to `RS256` or match the algorithm agreed during
    registration
  + If the JWT `alg` Header Parameter uses a MAC based algorithm such as
    `HS256`, `HS384`, or `HS512`, the octets of the UTF-8 representation of the
    `client_secret` corresponding to the `client_id` contained in the `aud`
    (audience) Claim are used as the key to validate the signature.
  + Check for expiry. The current time MUST be before the time represented by
    the `exp` Claim.
  + The `iat` Claim can be used to reject tokens that were issued too far away
    from the current time, limiting the amount of time that nonces need to be
    stored to prevent attacks
  + If a `nonce` value was sent in the Authentication Request, a `nonce` Claim
    MUST be present and its value checked to verify that it is the same value
    as the one that was sent in the Authentication Request

### Authentication using Implicit Flow

+ Steps
  1. Client prepares an Authentication Request containing the desired request parameters.
  2. Client sends the request to the Authorization Server.
  3. Authorization Server Authenticates the End-User.
  4. Authorization Server obtains End-User Consent/Authorization.
  5. Authorization Server sends the End-User back to the Client with an ID Token and, if requested, an Access Token.
  6. Client validates the ID token and retrieves the End-User's Subject Identifier.
+ Authorization Request parameters
  + `response_type`: either `id_token token` or `id_token`
  + `redirect_uri`: Must match one of the redirection URI values pre-registered
  + `nonce`: Required. To associate a client session with ID Token, and mitigate replay attacks
+ Authorization Response parameters
  + `access_token`: if `response_type` contains `token`
  + `token_type`: `Bearer` or other negotiated type
  + `id_token`: The `id_token` from OpenID Connect
  + `state`: For client to verify
  + `expires_in`: Expiration time of the Access Token
+ Access Token Validation
  + Client should validate the Access Token if issued together with the ID Token
    + Hash the ASCII representation of the Access Token with the algorithm specified in the ID Token
    + Check that against the `at_hash` value in the ID Token
    + This ensures that the Access Token was issued together with this ID Token
+ ID Token itself is validated by checking the signature of the ID token
  + Must check the value of the nonce claim to see that it is the same value as
    the one sent in the authentication request

### Authentication using Hybrid Flow

Advantage over Implicit or Authorization Code Flows: Using the hybrid flow
allows the client to use the ID Token in one round trip

+ Steps: same as Authorization Code Flow. Difference is the parameters returned
  by the Authorization Server
+ Authorization Request parameters
  + `response_type`: one of `code id_token`, `code token` or `code id_token token`
+ Authorization Response parameters
  + `access_token`, `id_token` and `code`, according to requested `response_type`
+ Authorization Code Validation
  + Hash the ASCII representation of the Authorization Code with the algorithm specified in the ID Token
  + Check that against the `c_hash` value in the ID Token
  + This ensures that the Authorization Code was issued together with this ID Token
+ ID Token must be validated with the Authorization Code and Access Token
  depending on which was issued together. ID token sent in redirection from
  User-Agent must match the one obtained from the Token Endpoint by the Client

## Claims in ID Token

+ Standard claims are quite self-explanatory, I think, other than `sub`, which is the identifier
  for the End-User at the Issuer (maybe some uuid or something)
+ Seems like you can insert any claim your app needs
+ Validate through signature or encryption

## Offline Access

+ OpenID Connect defines an `offline_access` `scope` value to request offline access
+ A Refresh Token will be issued to obtain an Access Token that grants access
  to the End-User's UserInfo Endpoint even when the End-User is not logged in

## Refresh Tokens

### Refresh Request

+ Clients must authenticate to the Token Endpoint
+ Authorization Server MUST validate
  + The Refresh Token
  + That it was issued to the Client
  + That the Client successfully authenticated using its Client Authentication method
+ ID Token that is returned with the token refresh request must match the original ID Token issued

## Interesting Security Considerations

### Request Disclosure

Mitigations
+ Use TLS all the way
+ Use `request` parameter to pass requests as a JWT to the authorization server
  + Can encrypt the JWT
+ Use `request_uri` to pass the request parameters by reference

### Server Masquerading

Mitigations
+ Client authenticates servers using TLS
+ Signed or encrypted JWTs to authenticate server

### Token Manufacture/Modification

+ Extension of validity period
+ Increased scope

Mitigations
+ Token should be digitally signed by the OP, RP should validate the signature
+ Use TLS to guard against third party attackers

### Access Token Redirect

Attacker uses access token for one resource to obtain access to a second resource

Mitigations
+ Access token should be audience and scope restricted

### Token Substitution

A malicious user swaps tokens, e.g. swap an Authorization Code for a legitimate
user with another token the attacker has. Attacker can copy the token out of
one session and use it in a HTTP message for a different session. Known as "cut
and paste" attack.

Implicit Flow of OAuth 2.0 is not designed to mitigate this risk

In OpenID Connect, this is mitigated through the ID Token. ID Token is a signed
security token thaht provides claims such as `iss`, `sub`, `aud`, `azp`,
`at_hash`, `c_hash`. Can use the ID token to detect token substitution attack.

`c_hash` is used to prevent Authorization Code substitution and `at_hash` is
used to prevent Authorization Code substitution.

Attacker can subvert the communication channel between the Authorization/Token
Endpoint and Client to reorder messages. This can be mitigated by using TLS.

### Timing Attack

Use elapsed time differences taken by successful and unsuccessful decryption
operations.

Mitigations
+ Don't terminate validation process early
+ Intentionally slow unsuccessful code path
+ Rate limiting

## Interesting Privacy Considerations

### Need for Encrypted Requests

Sometimes, just knowing that the client is requesting for a certain claim is
revealing something about the user. Encrypt requests to prevent this kind of
leakage.
