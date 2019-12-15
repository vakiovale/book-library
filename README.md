[![Build Status](https://travis-ci.org/valtteripyyhtia/book-library.svg?branch=master)](https://travis-ci.org/valtteripyyhtia/book-library)

# Book library

## How to

### Test login

Endpoint `localhost:3000/test-login?user=my-user-name` can be used to retrieve a valid JWT token in development/testing environments.

### cURL

Get all books:
```curl -v -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyMUBleGFtcGxlLmNvbSJ9.0Ps8iZ07c5x8zphFSr6T0UO-BVoJm7UdejtdQs_HtVo" localhost:3000/books```

Create a book:
```curl -v -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJsaWJyYXJpYW5AYm9va2xpYnJhcnkifQ.2k9ezDqwTPPFOsxKp-ah7NGtTxE6nsRdnywDu8Fm084" -H "Content-Type: application/json" localhost:3000/books -d "{\"name\": \"Revelation Space\"}"```

Local development environment uses `jwtsecret-dev` as a JWT secret. Header for JWT token is ```{
  "alg": "HS256",
  "typ": "JWT"
}```
