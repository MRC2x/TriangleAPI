#!/bin/bash

echo "Testing Triangle API Service"
echo "============================"

BASE_URL=${BASE_URL:-"http://localhost:8080"}
TOKEN="9ea8c6a6-73f5-4ea1-8ec8-f8a3b00a2564"

echo "Base URL: $BASE_URL"
echo ""

# Test 1: Health check
echo "1. Testing health endpoint..."
curl -s "$BASE_URL/actuator/health" | jq '.' 2>/dev/null || curl -s "$BASE_URL/actuator/health"
echo ""

# Test 2: Create triangle
echo "2. Creating a triangle..."
RESPONSE=$(curl -s -X POST "$BASE_URL/triangle/" \
  -H "Content-Type: application/json" \
  -H "X-User: $TOKEN" \
  -d '{"separator": ";", "input": "3;4;5"}')

echo "Response: $RESPONSE"
TRIANGLE_ID=$(echo $RESPONSE | grep -o '"id":"[^"]*"' | cut -d'"' -f4)
echo "Triangle ID: $TRIANGLE_ID"
echo ""

# Test 3: Get all triangles
echo "3. Getting all triangles..."
curl -s -X GET "$BASE_URL/triangle/all" \
  -H "X-User: $TOKEN" | jq '.' 2>/dev/null || curl -s -X GET "$BASE_URL/triangle/all" -H "X-User: $TOKEN"
echo ""

# Test 4: Get specific triangle
if [ ! -z "$TRIANGLE_ID" ]; then
    echo "4. Getting triangle by ID: $TRIANGLE_ID"
    curl -s -X GET "$BASE_URL/triangle/$TRIANGLE_ID" \
      -H "X-User: $TOKEN" | jq '.' 2>/dev/null || curl -s -X GET "$BASE_URL/triangle/$TRIANGLE_ID" -H "X-User: $TOKEN"
    echo ""
    
    # Test 5: Get area
    echo "5. Getting triangle area..."
    curl -s -X GET "$BASE_URL/triangle/$TRIANGLE_ID/area" \
      -H "X-User: $TOKEN" | jq '.' 2>/dev/null || curl -s -X GET "$BASE_URL/triangle/$TRIANGLE_ID/area" -H "X-User: $TOKEN"
    echo ""
    
    # Test 6: Get perimeter
    echo "6. Getting triangle perimeter..."
    curl -s -X GET "$BASE_URL/triangle/$TRIANGLE_ID/perimeter" \
      -H "X-User: $TOKEN" | jq '.' 2>/dev/null || curl -s -X GET "$BASE_URL/triangle/$TRIANGLE_ID/perimeter" -H "X-User: $TOKEN"
    echo ""
    
    # Test 7: Delete triangle
    echo "7. Deleting triangle..."
    curl -s -X DELETE "$BASE_URL/triangle/$TRIANGLE_ID" \
      -H "X-User: $TOKEN"
    echo " (Status: $?)"
    echo ""
fi

# Test 8: Test authentication
echo "8. Testing authentication (should fail)..."
curl -s -X GET "$BASE_URL/triangle/all" \
  -H "X-User: invalid-token" | jq '.' 2>/dev/null || curl -s -X GET "$BASE_URL/triangle/all" -H "X-User: invalid-token"
echo ""

echo "Service test completed!" 