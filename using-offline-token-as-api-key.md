# Using offline tokens as API key

## What we want from an API key

+ API key must grant its bearer access to only a certain user's rights to a
  specific resource server
+ API key should be easy to use programmatically
  + It should ideally just be `curl -h 'Authorization: Bearer API_KEY' https://client.localhost/api/`
+ API key should not have as wide a scope as the user's username and password
  + The user's username and password grants the bearer access to too many
    systems and data

## Exploration

### 1. Get an offline token through browser cookies

I did this by configuring gatekeeper to request `offline_access` in the `scope`.

To do this, add the following to gatekeeper configuration

```
scope:
  - offline_access
```

Restart gatekeeper, and now trigger an access to a protected api. Sign in, and
have the results of the access in your browser.

Obtain the encrypted refresh token from the browser cookie `kc-state`.

### 2. Decrypt the cookie

I did this by modifying gatekeeper code.

```
git clone git@github.com:keycloak/keycloak-gatekeeper.git
```

Add this to `main.go`

```
	if strings.HasSuffix(os.Args[0], "gatekeeper-utils") {
		state := os.Args[1]
		key := os.Args[2]
		token, err := decodeText(state, key)
		if err != nil {
			fmt.Println(err)
			os.Exit(1)
		}

		fmt.Println(token)
		os.Exit(0)
	}
```

Compile gatekeeper

```
make
```

Then symlink the binary to `gatekeeper-utils`

```
ln -s keycloak-gatekeeper gatekeeper-utils
```

Execute `gatekeeper-utils` with `kc-state` and `key` (from gatekeeper config) as arguments

```
$ ./bin/gatekeeper-utils aeKFcycIiSpDZfQ9LrCXPpNu6GOJ3BeZOgUwlVsyWKqx242BoVp5TaRMUZDhHD+lrIExpx72/TNALuupFaHMmtbB3YuftjGb6N2pNLURrxwFndzkHzakbpQpBmgPEDkUr2YIFF5wCoizMuBmHRKjve8uJ/EzDbR5Y7drjGqgVl9SX7NT8kw/hpuBITOy0kx2GyNU2bIzyBixx4SrjMmgR+8age10GeSXinOMC97bvfKNaYrbcTPG5NK55k7SoOCfKFy3xwdL3e1My5M24eigXYywrilV7Piv5v4VhrbO2Ah34+42/EJyWbKOk3fwIHlPGWUEHf1QtOnznHk+Hb0YxH6rTAYWBmZNzItS6gUAH1f3d8fmyHBDfZNfnofB+Kw/qDTZgpy6FOdaGTvcCkdQc64ca5geO4fy+o+ZkzTFc/wkIjjMid070lZXXTZFuwzuJqRuRGOvrdCjf6CSFtQ+V1EX/u2oMBv/d8dZEUPSmF+e/PM/Kzt1cMQ9LERsYW/ej7lD7u72sK8OTUBhcSGbM9Hwc6FFddeKTMr5OLCDUpVaiBNKj2ob6jHSJbrjq6R1eGSXIs/OOR5126n6VJ63Jb2R0NAaoDRe3czgnk02dyw5g9yrX1PK//7MY7v+aR6OEnwCjVYf8sg8FnDfGx9NMW0nt6j3Evzk8hbthw0J/GAOqwG4hoZkV/n2Z3SWn2Pj24L6hMaZyHKjqB3i2macMpQqRTAajwCJvjrp4425ceqvCoFtMxTo9Sv/Q2d/R8jnJYZuU5DP+Xx4egQoL+ue8Qudz3coDRPe9DvV1AygEC/u51nHnKEbstLTEy0m/81vKf2oI+gQaCfsm77OFZ6riScMbZxAfP237vK5UJv92A yrYUNvsPZLfwMMwMiROu6vpbOXhTKdB0
eyJhbGciOiJIUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICIyMGM4YzJiYy1mNjc4LTQ1NmItYTEzYS04Yzg1MjJkYTAxNjIifQ.eyJpYXQiOjE1ODgyMzA2MDcsImp0aSI6IjE3NzQ4NDhkLWFjN2QtNGU0Mi1iNGU1LWUzOGI2MmI3OTNjYyIsImlzcyI6Imh0dHBzOi8va2V5Y2xvYWsubG9jYWxob3N0L2F1dGgvcmVhbG1zL2FwcGxpY2F0aW9ucyIsImF1ZCI6Imh0dHBzOi8va2V5Y2xvYWsubG9jYWxob3N0L2F1dGgvcmVhbG1zL2FwcGxpY2F0aW9ucyIsInN1YiI6IjJkMjUzZDY1LTIyYTUtNGNhNy1hNzNmLThmZDY4MmE1OGRkNyIsInR5cCI6Ik9mZmxpbmUiLCJhenAiOiJ3aG9hbWkiLCJzZXNzaW9uX3N0YXRlIjoiZGYyODQyNDUtZDRjNC00MGYyLTkyNWMtN2U3NmMwYzllOGRmIiwic2NvcGUiOiJvcGVuaWQgcHJvZmlsZSBlbWFpbCBvZmZsaW5lX2FjY2VzcyJ9.S4RuN-e_aTGFyrDjn5yy93AmEMyc-UNhslIIUebKIJA
$
```

And there's the refresh token! It's actually an offline token. If you decode that, (I used jwt.io) you'll get something like

```
{
  "iat": 1588230607,
  "jti": "1774848d-ac7d-4e42-b4e5-e38b62b793cc",
  "iss": "https://keycloak.localhost/auth/realms/applications",
  "aud": "https://keycloak.localhost/auth/realms/applications",
  "sub": "2d253d65-22a5-4ca7-a73f-8fd682a58dd7",
  "typ": "Offline",
  "azp": "whoami",
  "session_state": "df284245-d4c4-40f2-925c-7e76c0c9e8df",
  "scope": "openid profile email offline_access"
}
```

### 3. Use refresh token to get access token

Now you'll have to take the place of a client to get the access token.

```
curl -X POST -H "Authorization: Basic $(echo -n whoami:d802b673-4816-4201-8d1f-1ead9cc86bf2 | base64)" -kL 'https://keycloak.localhost/auth/realms/applications/protocol/openid-connect/token' -d 'grant_type=refresh_token&refresh_token=eyJhbGciOiJIUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICIyMGM4YzJiYy1mNjc4LTQ1NmItYTEzYS04Yzg1MjJkYTAxNjIifQ.eyJpYXQiOjE1ODgyMzA2MDcsImp0aSI6IjE3NzQ4NDhkLWFjN2QtNGU0Mi1iNGU1LWUzOGI2MmI3OTNjYyIsImlzcyI6Imh0dHBzOi8va2V5Y2xvYWsubG9jYWxob3N0L2F1dGgvcmVhbG1zL2FwcGxpY2F0aW9ucyIsImF1ZCI6Imh0dHBzOi8va2V5Y2xvYWsubG9jYWxob3N0L2F1dGgvcmVhbG1zL2FwcGxpY2F0aW9ucyIsInN1YiI6IjJkMjUzZDY1LTIyYTUtNGNhNy1hNzNmLThmZDY4MmE1OGRkNyIsInR5cCI6Ik9mZmxpbmUiLCJhenAiOiJ3aG9hbWkiLCJzZXNzaW9uX3N0YXRlIjoiZGYyODQyNDUtZDRjNC00MGYyLTkyNWMtN2U3NmMwYzllOGRmIiwic2NvcGUiOiJvcGVuaWQgcHJvZmlsZSBlbWFpbCBvZmZsaW5lX2FjY2VzcyJ9.S4RuN-e_aTGFyrDjn5yy93AmEMyc-UNhslIIUebKIJA' | jq
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100  4057  100  3391  100   666   118k  23785 --:--:-- --:--:-- --:--:--  141k
{
  "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJOMmQ0NFppTXZTSXZkSkFDcE1xT01mcnlkX1UyNWFsZnQ5eVJZY2Y0X0tZIn0.eyJleHAiOjE1ODgyMzIwODksImlhdCI6MTU4ODIzMTc4OSwiYXV0aF90aW1lIjoxNTg4MjI1ODgxLCJqdGkiOiJmNWVjZTE4OS1hYWJjLTQyOTQtODE3ZS03NGM1MDhhNDM3MTAiLCJpc3MiOiJodHRwczovL2tleWNsb2FrLmxvY2FsaG9zdC9hdXRoL3JlYWxtcy9hcHBsaWNhdGlvbnMiLCJhdWQiOlsid2hvYW1pIiwiYWNlIiwiYWNjb3VudCJdLCJzdWIiOiIyZDI1M2Q2NS0yMmE1LTRjYTctYTczZi04ZmQ2ODJhNThkZDciLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJ3aG9hbWkiLCJzZXNzaW9uX3N0YXRlIjoiZGYyODQyNDUtZDRjNC00MGYyLTkyNWMtN2U3NmMwYzllOGRmIiwiYWNyIjoiMCIsImFsbG93ZWQtb3JpZ2lucyI6WyJodHRwczovL3dob2FtaS5sb2NhbGhvc3QiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2UiOnsicm9sZXMiOlsiZGV0YWlsIiwibGlzdCJdfSwiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJvcGVuaWQgcHJvZmlsZSBlbWFpbCBvZmZsaW5lX2FjY2VzcyIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwibmFtZSI6IlVzZXIgWmVybyIsInByZWZlcnJlZF91c2VybmFtZSI6InVzZXIwIiwiZ2l2ZW5fbmFtZSI6IlVzZXIiLCJmYW1pbHlfbmFtZSI6Ilplcm8iLCJlbWFpbCI6InVzZXIwQG1haWwuY29tIn0.DXrR3URJHG9HaP3_myIT-C-EqBNagdo6KH3pIeauoZTbdlo1oWiM1mTcTfOzMm3Q1pBtFvtL3_u8dl0cjzNnCIArrcv52kFnp048Esyeft9L-B3fHzUgWcdDyBqgLWkuQIfTAIVLnP1IMUffKwFID5SWR8YvG62XeyMF9kaAnlvL27oE8k6pp_9dgvISslwf7mY5r7IAXWukWSpQ1dJchcILEsVSPsNURQSQUsshu_Rh4YJ5vtase0hurOYhiZqRfwbw5wnnOYF2QW9wNZEvno9XzjYkDvF0-yNqqiG40mWyOmcbTpTY4JcvX6Z7IZ54TtUq8-jMP2Wpe90m_Omz2w",
  "expires_in": 300,
  "refresh_expires_in": 0,
  "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICIyMGM4YzJiYy1mNjc4LTQ1NmItYTEzYS04Yzg1MjJkYTAxNjIifQ.eyJpYXQiOjE1ODgyMzE3ODksImp0aSI6ImNiNjhkZDUzLWEyZDgtNDk0Zi05MWMyLWI3ODE3YThkYTg2OSIsImlzcyI6Imh0dHBzOi8va2V5Y2xvYWsubG9jYWxob3N0L2F1dGgvcmVhbG1zL2FwcGxpY2F0aW9ucyIsImF1ZCI6Imh0dHBzOi8va2V5Y2xvYWsubG9jYWxob3N0L2F1dGgvcmVhbG1zL2FwcGxpY2F0aW9ucyIsInN1YiI6IjJkMjUzZDY1LTIyYTUtNGNhNy1hNzNmLThmZDY4MmE1OGRkNyIsInR5cCI6Ik9mZmxpbmUiLCJhenAiOiJ3aG9hbWkiLCJzZXNzaW9uX3N0YXRlIjoiZGYyODQyNDUtZDRjNC00MGYyLTkyNWMtN2U3NmMwYzllOGRmIiwic2NvcGUiOiJvcGVuaWQgcHJvZmlsZSBlbWFpbCBvZmZsaW5lX2FjY2VzcyJ9.x2RkgJeCCp8QvW4FL9FTIClwYMqcSz0SA6Bb4Ngw62k",
  "token_type": "bearer",
  "id_token": "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJOMmQ0NFppTXZTSXZkSkFDcE1xT01mcnlkX1UyNWFsZnQ5eVJZY2Y0X0tZIn0.eyJleHAiOjE1ODgyMzIwODksImlhdCI6MTU4ODIzMTc4OSwiYXV0aF90aW1lIjoxNTg4MjI1ODgxLCJqdGkiOiI5NGFjMmNlNy0zMDVhLTRhZmQtYTU3My1mZWJhMjg3N2ZhYjciLCJpc3MiOiJodHRwczovL2tleWNsb2FrLmxvY2FsaG9zdC9hdXRoL3JlYWxtcy9hcHBsaWNhdGlvbnMiLCJhdWQiOiJ3aG9hbWkiLCJzdWIiOiIyZDI1M2Q2NS0yMmE1LTRjYTctYTczZi04ZmQ2ODJhNThkZDciLCJ0eXAiOiJJRCIsImF6cCI6Indob2FtaSIsInNlc3Npb25fc3RhdGUiOiJkZjI4NDI0NS1kNGM0LTQwZjItOTI1Yy03ZTc2YzBjOWU4ZGYiLCJhY3IiOiIwIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJuYW1lIjoiVXNlciBaZXJvIiwicHJlZmVycmVkX3VzZXJuYW1lIjoidXNlcjAiLCJnaXZlbl9uYW1lIjoiVXNlciIsImZhbWlseV9uYW1lIjoiWmVybyIsImVtYWlsIjoidXNlcjBAbWFpbC5jb20ifQ.Nsu0iPQK_i0KQPjx4JJCFeFayrroEYGUWn4QHVy18nmhh06OzR5o96Eaz7paInbWfcjzb0JDd7bKWnsNW9ltumgCtT8IOM9ZT2o1uw2__g6H8EoAiQve0zfVUXRiIHovStlEIVqJXv6H99n_z4mIebN3oDZZbizo2YV07Rf0u2nD-apaGclzfvFr5_n2t8gva9gFFri98y-IhQ51OolqwbVlfSTYtWzJs8qzvhqkz2tcXOvzaFs2e7m6X_KRygXgP0wq4QNw8lulGZYbJxqxp4uTPRCQf0J6GFeascialYnuaj1RkoMNM1BXx54ofgooNycgqS0s5Y2p7opLNZZiHQ",
  "not-before-policy": 0,
  "session_state": "df284245-d4c4-40f2-925c-7e76c0c9e8df",
  "scope": "openid profile email offline_access"
}
```

With that access token, you can access the protected endpoint!

## Implementation Ideas

### Gateway component + offline token -> access token caching + endpoint for user to retrieve offline token

+ Gateway component
  + Will have to distinguish API Keys from regular access tokens
    + Could prefix API keys with a magic string
  + Will use received offline tokens to get access token
  + Will cache this offline token to access token mapping so that access tokens
    that are still valid will be reused
    + Could perhaps be implemented by storing offline tokens alongside
      traditional refresh tokens
  + Send access token upstream for resource server to validate
+ Endpoint for user to retrieve offline token per client, like `/api-key`
  + Will use the OAuth 2.0 authorization code flow, but request
    `offline_access` as an additional scope
  + With the authorization code from the User Agent, the endpoint can then
    obtain an offline token instead of the usual refresh token and return it to
    the user for safekeeping
  + Will need to implement the necessary protections of a redirection endpoint
    (csrf, etc)
    + Might be possible to redirect to one of gatekeeper's endpoints?
    + Perhaps it is possible to use a refresh token to request an offline token
      by specifying a wider scope?
