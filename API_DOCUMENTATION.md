# Real-Time Auction Backend API Documentation

## Base URL
```
http://localhost:7000/api
```

## Authentication
All endpoints except `/auth/*` require authentication using a Bearer token in the Authorization header:
```
Authorization: Bearer <token>
```

## Endpoints

### Authentication

#### Register User
```http
POST /auth/register
```
Request body:
```json
{
  "name": "string",
  "email": "string",
  "password": "string",
  "address": "string"
}
```
Response:
```json
{
  "token": "string"
}
```

#### Login
```http
POST /auth/login
```
Request body:
```json
{
  "email": "string",
  "password": "string"
}
```
Response:
```json
{
  "token": "string"
}
```

### Auctions

#### Get All Auctions
```http
GET /auctions
```
Query Parameters:
- `status` (optional): Filter by auction status
- `page`: Page number (0-based)
- `size`: Number of items per page
- `sort`: Sort field and direction (e.g., "createdAt,desc")

Response:
```json
{
  "content": [
    {
      "id": "number",
      "title": "string",
      "description": "string",
      "startingPrice": "number",
      "currentPrice": "number",
      "status": "string",
      "startTime": "string",
      "endTime": "string",
      "seller": {
        "id": "number",
        "name": "string",
        "email": "string"
      },
      "bids": [
        {
          "id": "number",
          "amount": "number",
          "timestamp": "string",
          "bidder": {
            "id": "number",
            "name": "string"
          }
        }
      ]
    }
  ],
  "totalElements": "number",
  "totalPages": "number",
  "size": "number",
  "number": "number"
}
```

#### Get Auction by ID
```http
GET /auctions/{id}
```
Response: Same as single auction object from above

#### Create Auction
```http
POST /auctions
```
Request body:
```json
{
  "title": "string",
  "description": "string",
  "startingPrice": "number",
  "startTime": "string",
  "endTime": "string",
  "categories": ["string"],
  "tags": ["string"]
}
```

#### Update Auction
```http
PUT /auctions/{id}
```
Request body: Same as create auction

#### Delete Auction
```http
DELETE /auctions/{id}
```

### Bids

#### Place Bid
```http
POST /bids
```
Request body:
```json
{
  "auctionId": "number",
  "amount": "number"
}
```

#### Get Bid History
```http
GET /bids/{auctionId}
```
Response:
```json
[
  {
    "id": "number",
    "amount": "number",
    "timestamp": "string",
    "bidder": {
      "id": "number",
      "name": "string"
    }
  }
]
```

#### Get Highest Bid
```http
GET /bids/auction/{auctionId}/highest
```
Response: Single bid object

### Categories

#### Search Categories
```http
GET /categories/search
```
Query Parameters:
- `query`: Search term
- `page`: Page number
- `size`: Items per page

Response:
```json
{
  "content": [
    {
      "id": "number",
      "name": "string",
      "description": "string"
    }
  ],
  "totalElements": "number",
  "totalPages": "number"
}
```

#### Get All Categories
```http
GET /categories
```
Response: Array of category objects

#### Create Category
```http
POST /categories
```
Request body:
```json
{
  "name": "string",
  "description": "string"
}
```

### Tags

#### Search Tags
```http
GET /tags/search
```
Query Parameters:
- `query`: Search term
- `page`: Page number
- `size`: Items per page

Response: Paginated list of tags

#### Get All Tags
```http
GET /tags
```
Response: Array of tag objects

#### Create Tag
```http
POST /tags
```
Request body:
```json
{
  "name": "string"
}
```

### Reviews

#### Get User Reviews
```http
GET /reviews/user/{userId}
```
Query Parameters:
- `page`: Page number
- `size`: Items per page

Response:
```json
{
  "content": [
    {
      "id": "number",
      "rating": "number",
      "comment": "string",
      "createdAt": "string",
      "reviewer": {
        "id": "number",
        "name": "string"
      }
    }
  ],
  "totalElements": "number",
  "totalPages": "number"
}
```

#### Get User Rating
```http
GET /reviews/user/{userId}/rating
```
Response: Average rating as number

#### Get Review Count
```http
GET /reviews/user/{userId}/count
```
Response: Total number of reviews as number

#### Create Review
```http
POST /reviews
```
Request body:
```json
{
  "reviewedId": "number",
  "rating": "number",
  "comment": "string"
}
```

### Watchlist

#### Get User Watchlist
```http
GET /watchlist/user/{userId}
```
Response:
```json
{
  "id": "number",
  "auctions": [
    {
      "id": "number",
      "title": "string",
      "currentPrice": "number",
      "status": "string"
    }
  ]
}
```

#### Add to Watchlist
```http
POST /watchlist/user/{userId}/auction/{auctionId}
```

#### Remove from Watchlist
```http
DELETE /watchlist/user/{userId}/auction/{auctionId}
```

### Wallet

#### Get Balance
```http
GET /wallet/balance
```
Response: Current balance as number

#### Deposit
```http
POST /wallet/deposit
```
Query Parameters:
- `amount`: Amount to deposit

Response: Transaction object

#### Withdraw
```http
POST /wallet/withdraw
```
Query Parameters:
- `amount`: Amount to withdraw

Response: Transaction object

#### Get Transactions
```http
GET /wallet/transactions
```
Query Parameters:
- `page`: Page number
- `size`: Items per page

Response:
```json
{
  "content": [
    {
      "id": "number",
      "amount": "number",
      "type": "string",
      "description": "string",
      "timestamp": "string",
      "status": "string"
    }
  ],
  "totalElements": "number",
  "totalPages": "number"
}
```

### Admin

#### Get All Users
```http
GET /admin/users
```
Response: Array of user objects

#### Update User Role
```http
PUT /admin/users/{userId}/role
```
Request body:
```json
{
  "role": "string"
}
```

#### Get Dashboard Statistics
```http
GET /admin/statistics/dashboard
```
Response:
```json
{
  "totalUsers": "number",
  "totalAuctions": "number",
  "activeAuctions": "number",
  "totalBids": "number",
  "totalRevenue": "number"
}
```

### WebSocket

#### Subscribe to Auction Bids
```http
WS /ws/auctions/{auctionId}/bids
```
Messages:
- New bid notification:
```json
{
  "type": "NEW_BID",
  "data": {
    "id": "number",
    "amount": "number",
    "bidder": {
      "id": "number",
      "name": "string"
    },
    "timestamp": "string"
  }
}
```

## Error Responses

All endpoints may return the following error responses:

```json
{
  "status": "number",
  "error": "string",
  "message": "string",
  "timestamp": "number"
}
```

Common status codes:
- 400: Bad Request
- 401: Unauthorized
- 403: Forbidden
- 404: Not Found
- 500: Internal Server Error

## Pagination

All list endpoints support pagination with the following query parameters:
- `page`: Page number (0-based)
- `size`: Number of items per page
- `sort`: Sort field and direction (e.g., "createdAt,desc")

## Date Format

All dates are returned in ISO 8601 format:
```
YYYY-MM-DDTHH:mm:ss.SSSZ
```

## Rate Limiting

API requests are limited to:
- 100 requests per minute for authenticated users
- 20 requests per minute for unauthenticated users 