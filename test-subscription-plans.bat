@echo off
echo Testing Subscription Plans API...
echo ==================================

set BASE_URL=http://localhost:8088
set DEV_TOKEN=dev-secret-2024

echo.
echo 📋 Available Subscription Plans:
echo 1. FREE - Basic features with limited access
echo 2. BASIC - Standard features for small businesses  
echo 3. PROFESSIONAL - Advanced features for growing companies
echo 4. ENTERPRISE - Full features for large organizations
echo 5. CUSTOM - Tailored solutions for specific needs
echo.

echo 🧪 Test 1: Creating tenant with FREE plan...
curl -X POST %BASE_URL%/api/dev/tenant/create ^
  -H "X-Developer-Token: %DEV_TOKEN%" ^
  -H "Content-Type: application/json" ^
  -d "{\"tenantName\": \"Free Company\", \"subscriptionPlan\": \"FREE\"}"
echo.
echo.

echo 🧪 Test 2: Creating tenant with PROFESSIONAL plan...
curl -X POST %BASE_URL%/api/dev/tenant/create ^
  -H "X-Developer-Token: %DEV_TOKEN%" ^
  -H "Content-Type: application/json" ^
  -d "{\"tenantName\": \"Professional Corp\", \"subscriptionPlan\": \"PROFESSIONAL\"}"
echo.
echo.

echo 🧪 Test 3: Creating tenant with ENTERPRISE plan...
curl -X POST %BASE_URL%/api/dev/tenant/create ^
  -H "X-Developer-Token: %DEV_TOKEN%" ^
  -H "Content-Type: application/json" ^
  -d "{\"tenantName\": \"Enterprise Solutions\", \"subscriptionPlan\": \"ENTERPRISE\"}"
echo.
echo.

echo 🧪 Test 4: Creating tenant without plan (defaults to FREE)...
curl -X POST %BASE_URL%/api/dev/tenant/create ^
  -H "X-Developer-Token: %DEV_TOKEN%" ^
  -H "Content-Type: application/json" ^
  -d "{\"tenantName\": \"Default Company\"}"
echo.
echo.

echo 🧪 Test 5: Creating tenant with invalid plan (should fail)...
curl -X POST %BASE_URL%/api/dev/tenant/create ^
  -H "X-Developer-Token: %DEV_TOKEN%" ^
  -H "Content-Type: application/json" ^
  -d "{\"tenantName\": \"Invalid Corp\", \"subscriptionPlan\": \"INVALID_PLAN\"}"
echo.
echo.

echo 📊 Listing all tenants to see subscription plans...
curl -X GET %BASE_URL%/dev/tenants ^
  -H "X-Developer-Token: %DEV_TOKEN%"
echo.
echo.

echo ✅ Subscription plans test completed!
pause
