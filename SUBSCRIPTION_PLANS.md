# Subscription Plans Feature Documentation

## Overview
The SalesHelper application now supports subscription plans for tenants, allowing different tiers of service based on business needs.

## Subscription Plan Enum

### Available Plans

| Plan | Display Name | Description | Tier | Premium |
|------|-------------|-------------|------|---------|
| `FREE` | Free | Basic features with limited access | 1 | ❌ |
| `BASIC` | Basic | Standard features for small businesses | 2 | ✅ |
| `PROFESSIONAL` | Professional | Advanced features for growing companies | 3 | ✅ |
| `ENTERPRISE` | Enterprise | Full features for large organizations | 4 | ✅ |
| `CUSTOM` | Custom | Tailored solutions for specific needs | 5 | ✅ |

### Enum Methods

```java
// Check if plan is premium (paid tier)
boolean isPremium() 

// Check if plan supports advanced features
boolean supportsAdvancedFeatures()

// Get tier level (1-5)
int getTier()

// Get display name
String getDisplayName()

// Get description
String getDescription()
```

## Database Schema

### Tenant Table Updates
```sql
ALTER TABLE tenant ADD COLUMN subscription_plan VARCHAR(50) NOT NULL DEFAULT 'FREE';
```

The `subscription_plan` column stores the enum value as a string in the database.

## API Updates

### Developer API - Create Tenant

**Endpoint:** `POST /api/dev/tenant/create`

**Request Body:**
```json
{
  "tenantName": "Company Name",
  "subscriptionPlan": "PROFESSIONAL"  // Optional - defaults to FREE
}
```

**Available subscriptionPlan values:**
- `FREE`
- `BASIC` 
- `PROFESSIONAL`
- `ENTERPRISE`
- `CUSTOM`

**Response:**
```json
{
  "message": "Tenant created successfully",
  "tenant": {
    "id": 1,
    "name": "Company Name",
    "subscriptionPlan": "PROFESSIONAL",
    "createdAt": "2024-01-15T10:30:00"
  }
}
```

### Developer API - List Tenants

**Endpoint:** `GET /api/dev/tenants`

**Response includes subscription plan:**
```json
{
  "tenants": [
    {
      "tenantId": 1,
      "tenantName": "Company Name",
      "subscriptionPlan": "PROFESSIONAL",
      "createdAt": "2024-01-15T10:30:00",
      "updatedAt": "2024-01-15T10:30:00"
    }
  ],
  "totalCount": 1
}
```

### Regular Tenant API - Update Tenant

**Endpoint:** `PUT /api/tenants/{tenantId}`

**Request Body:**
```json
{
  "tenantName": "Updated Company Name",
  "subscriptionPlan": "ENTERPRISE"  // Optional - keeps existing if not provided
}
```

## Usage Examples

### 1. Create Tenant with Free Plan
```bash
curl -X POST http://localhost:8088/api/dev/tenant/create \
  -H "X-Developer-Token: dev-secret-2024" \
  -H "Content-Type: application/json" \
  -d '{"tenantName": "Startup Inc"}'
```

### 2. Create Tenant with Professional Plan
```bash
curl -X POST http://localhost:8088/api/dev/tenant/create \
  -H "X-Developer-Token: dev-secret-2024" \
  -H "Content-Type: application/json" \
  -d '{"tenantName": "Growing Corp", "subscriptionPlan": "PROFESSIONAL"}'
```

### 3. Create Tenant with Enterprise Plan
```bash
curl -X POST http://localhost:8088/api/dev/tenant/create \
  -H "X-Developer-Token: dev-secret-2024" \
  -H "Content-Type: application/json" \
  -d '{"tenantName": "Large Enterprise", "subscriptionPlan": "ENTERPRISE"}'
```

## Frontend Integration

### Displaying Subscription Plans
```javascript
// Example JavaScript for displaying subscription plans
const subscriptionPlans = {
  FREE: { displayName: 'Free', tier: 1, premium: false },
  BASIC: { displayName: 'Basic', tier: 2, premium: true },
  PROFESSIONAL: { displayName: 'Professional', tier: 3, premium: true },
  ENTERPRISE: { displayName: 'Enterprise', tier: 4, premium: true },
  CUSTOM: { displayName: 'Custom', tier: 5, premium: true }
};

function formatSubscriptionPlan(plan) {
  return subscriptionPlans[plan]?.displayName || plan;
}
```

## Testing

Run the test script to verify subscription plan functionality:
```bash
test-subscription-plans.bat
```

## Validation Rules

1. **Default Plan**: If no subscription plan is provided, defaults to `FREE`
2. **Valid Values**: Only enum values are accepted (`FREE`, `BASIC`, `PROFESSIONAL`, `ENTERPRISE`, `CUSTOM`)
3. **Case Sensitivity**: Plan names are case-sensitive and should be uppercase
4. **Update Behavior**: When updating a tenant, subscription plan is optional and keeps existing value if not provided

## Business Logic Examples

```java
// Check if tenant has premium access
if (tenant.getSubscriptionPlan().isPremium()) {
    // Enable premium features
}

// Check if tenant supports advanced features
if (tenant.getSubscriptionPlan().supportsAdvancedFeatures()) {
    // Enable advanced functionality
}

// Compare plan tiers
if (tenant1.getSubscriptionPlan().getTier() > tenant2.getSubscriptionPlan().getTier()) {
    // tenant1 has higher tier access
}
```

## Migration Notes

- All existing tenants will retain their data
- New tenants default to `FREE` plan
- Database migration will add the `subscription_plan` column with default `FREE` value
- No breaking changes to existing APIs

## Security Considerations

- Subscription plans are tenant-scoped and isolated
- Developer API requires authentication for creating tenants with different plans
- Plan validation happens at the service layer
- Subscription plan changes are logged for audit purposes
