### Json Web Tokens (Auth)
http://jwt.io/

### Async Http (Request)
http://loopj.com/android-async-http

### Picasso (Images)
https://github.com/square/picasso

### Requests
```ruby
# login
POST     /v1/users/login
# register
POST     /v1/users
# get user info
POST     /v1/users/me                                           #need to be auth
# user likes a discount
POST     /v1/users/:id/like/:client_id/discount/:discount_id    #need to be auth
# update or create mobile device
PUT      /v1/users/:id/mobile                                   #need to be auth
# update user info
PUT      /v1/users/:id                                          #need to be auth
# get discounts based on user categories
GET      /v1/discounts                                          #need to be auth
```

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
  req       -> headers -> "Authorization": "Bearer {{JWT}}"
  response  -> { user: { ... } }
```

###

### Update User Info
```ruby
PUT /v1/users/:id #need to be auth
req ->  headers ->  "Authorization": "Bearer {{JWT}}"
    ->  params  ->  user_id # id
    ->  body    -> {
          user: {
            lastname: String, # optional
            name: String, # optional
            categories: [ # optional
              {
                status: Boolean, # required (true -> add) or (false -> delete)
                name: String, # required
                description: String # optional
              },
              ...
            ]
          }
        }
response -> status 200
```

### User Likes Discount
```ruby
POST /v1/users/:id/like/:client_id/discount/:discount_id
req ->  headers ->  "Authorization": "Bearer {{JWT}}"
    ->  params  ->  user_id     # id
                ->  client_id   # owner of liked discount
                ->  discount_id # liked discount id
    ->  body    ->  null
response -> status 200
```

### Update or Create Mobile Device
```ruby
PUT /v1/users/:id/mobile
req ->  headers  ->  "Authorization": "Bearer {{JWT}}"
    ->  body     ->  {
      token: String,
      platform: String # (android|apple)
    }
```

### Get discounts
```ruby
# if user doesn't have any liked category it is going to fetch all disccounts,
# but if it does, it is goingt to fetch all discounts based on it's categories.
GET /v1/discounts
req       ->  headers  ->  "Authorization": "Bearer {{JWT}}"
          ->  params   ->  null
          ->  body     ->  null
response  -> {
  discounts: [
    {
      name: String, # client's name
      rate: Float, # client's rate
      discounts: [ # client's available discounts
        {
          discount_rate: String, # discount %
          title: String,
          secret_key: String,
          status: Boolean,
          created_at: DateTime,
          duration: Integer,
          duration_term: String,
          hashtags: [ String, ... ],
          categories: [
            {
              name: String,
              description: String
            },
            ...
          ]
        },
        ...
      ],
      addresses: [ String, ... ], # client's addresses
      categories: [ # client's categories
        {
          name: String,
          description: String
        },
        ...
      ],
      locations: [ # client's Geo Locations
        {
          latitude: Float,
          longitude: Float
        },
        ...
      ]
    },
    ...
  ]
}
```
