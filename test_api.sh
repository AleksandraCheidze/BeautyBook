#!/bin/bash

BASE_URL="http://localhost:8081/api"

# Register admin
echo "Registering admin..."
curl -X POST "$BASE_URL/users/register" \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@test.com","password":"admin123","role":"ADMIN"}'
echo -e "\n"

# Login
echo "Logging in..."
TOKEN=$(curl -X POST "$BASE_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@test.com","password":"admin123"}' | jq -r '.accessToken')
echo "Token: $TOKEN"
echo -e "\n"

# Create categories
echo "Creating categories..."
curl -X POST "$BASE_URL/categories" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"name":"Manicure","description":"All types of manicure"}'
echo -e "\n"

curl -X POST "$BASE_URL/categories" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"name":"Pedicure","description":"All types of pedicure"}'
echo -e "\n"

# Get categories
echo "Getting categories..."
CATEGORIES=$(curl -X GET "$BASE_URL/categories")
echo "$CATEGORIES"
echo -e "\n"

# Get first category ID
CATEGORY_ID=$(echo "$CATEGORIES" | jq -r '.[0].id')

# Create procedures
echo "Creating procedures..."
curl -X POST "$BASE_URL/procedures" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "{\"name\":\"Classic Manicure\",\"description\":\"Basic manicure service\",\"duration\":60,\"price\":30.0,\"categoryId\":$CATEGORY_ID}"
echo -e "\n"

curl -X POST "$BASE_URL/procedures" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "{\"name\":\"Gel Polish\",\"description\":\"Long-lasting gel polish\",\"duration\":90,\"price\":45.0,\"categoryId\":$CATEGORY_ID}"
echo -e "\n"

# Get procedures
echo "Getting procedures..."
curl -X GET "$BASE_URL/procedures"
echo -e "\n" 