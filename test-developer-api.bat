@echo off
echo Testing Developer API...
echo ========================

REM Test with default token
echo Testing with default token...
curl -X POST http://localhost:8088/api/dev/tenant/create ^
  -H "X-Developer-Token: dev-secret-2024" ^
  -H "Content-Type: application/json" ^
  -d "{\"tenantName\": \"Test Company\"}"

echo.
echo.
echo Testing list tenants...
curl -X GET http://localhost:8088/api/dev/tenants ^
  -H "X-Developer-Token: dev-secret-2024"

echo.
echo.
echo Testing without token (should fail)...
curl -X POST http://localhost:8088/api/dev/tenant/create ^
  -H "Content-Type: application/json" ^
  -d "{\"tenantName\": \"Hacker Company\"}"

echo.
echo Done!
pause
