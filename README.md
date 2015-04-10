### Json Web Tokens (Auth)
http://jwt.io/

### Async Http (Request)
http://loopj.com/android-async-http

### Picasso (Images)
https://github.com/square/picasso

#### Auth Forkflow
```
# login
/v1/users/login -\
                  -> me
/v1/users       -/
# register

# login
  req       -> body -> { user: {...credentials...}}
  response  -> { token: "JWT" }

# register
  req       -> body    -> { user: {...info...} }
  response  -> { token: "JWT" }

# me
  req       -> headers -> "Authorization": "{{JWT}}"
  response  -> { user: { ... } }
```
