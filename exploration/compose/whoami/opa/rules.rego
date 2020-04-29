package authz

import input

# Helper to get the token payload.
token = {"payload": payload} {
  [header, payload, signature] := io.jwt.decode(input.token)
}

default allow = false

# Permit "/"
allow {
  # Use count because the json serializer serializes the empty lua table {} as
  #   a json object instead of array
  count(input.path) == 0
  input.method == "GET"
}

allow {
  input.path == ["api"]
  input.method == "GET"
  token.payload.preferred_username == "user0"
}
