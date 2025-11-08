package com.a2y.salesHelper.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a2y.salesHelper.config.DeveloperConfig;
import com.a2y.salesHelper.pojo.Tenant;
import com.a2y.salesHelper.service.interfaces.TenantService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/dev")
@CrossOrigin(origins = "*")
@Slf4j
@Tag(name = "Developer APIs", description = "Internal developer APIs - Production access restricted")
@SecurityRequirement(name = "developer-auth")
public class DeveloperController {

    private final TenantService tenantService;
    private final DeveloperConfig developerConfig;

    @Autowired
    public DeveloperController(TenantService tenantService, DeveloperConfig developerConfig) {
        this.tenantService = tenantService;
        this.developerConfig = developerConfig;
    }

    /**
     * Developer-only API to add a new tenant to the database
     * This endpoint is restricted to developers only and not exposed to regular
     * users
     * 
     * @param tenant         Tenant object with tenantName
     * @param developerToken Secret developer token for authentication
     * @return Created tenant information or error response
     */
    @Operation(summary = "Add New Tenant (Developer Only)", description = "Creates a new tenant in the database. Restricted to developers only.", security = @SecurityRequirement(name = "developer-auth"))
    @PostMapping("/tenant/create")
    public ResponseEntity<?> createTenant(
            @RequestBody Tenant tenant,
            @RequestHeader("X-Developer-Token") String developerToken,
            @RequestHeader(value = "X-Developer-Secret", required = false) String developerSecret) {

        try {
            // Validate developer access
            if (!validateDeveloperAccess(developerToken, developerSecret)) {
                log.warn("Unauthorized developer access attempt with token: {}",
                        developerToken != null ? "***" : "null");
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("{\"error\": \"Developer access denied. Invalid authentication.\"}");
            }

            // Validate tenant data
            if (tenant == null || tenant.getTenantName() == null || tenant.getTenantName().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body("{\"error\": \"Tenant name is required and cannot be empty.\"}");
            }

            // Set default subscription plan if not provided
            if (tenant.getSubscriptionPlan() == null) {
                tenant.setSubscriptionPlan(com.a2y.salesHelper.enums.SubscriptionPlan.FREE);
            }

            // Create tenant
            Tenant createdTenant = tenantService.createTenant(tenant);

            log.info("Developer successfully created tenant: {}", createdTenant.getTenantName());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("{\"message\": \"Tenant created successfully\", \"tenant\": {\"id\": " +
                            createdTenant.getTenantId() + ", \"name\": \"" + createdTenant.getTenantName() +
                            "\", \"subscriptionPlan\": \"" + createdTenant.getSubscriptionPlan() + "\"" +
                            ", \"createdAt\": \"" + createdTenant.getCreatedAt() + "\"}}");

        } catch (RuntimeException e) {
            log.error("Developer tenant creation failed", e);
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            log.error("Unexpected error in developer tenant creation", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Internal server error occurred while creating tenant.\"}");
        }
    }

    /**
     * Developer-only API to get all tenants (for debugging/monitoring)
     * 
     * @param developerToken Secret developer token for authentication
     * @return List of all tenants in the system
     */
    @Operation(summary = "Get All Tenants (Developer Only)", description = "Retrieves all tenants for developer monitoring purposes.", security = @SecurityRequirement(name = "developer-auth"))
    @GetMapping("/tenants")
    public ResponseEntity<?> getAllTenants(@RequestHeader("X-Developer-Token") String developerToken) {

        try {
            // Validate developer access
            if (!validateDeveloperAccess(developerToken, null)) {
                log.warn("Unauthorized developer access attempt for tenant list");
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("{\"error\": \"Developer access denied. Invalid authentication.\"}");
            }

            // Get all tenants
            var tenants = tenantService.getAllTenants();

            log.info("Developer accessed tenant list. Found {} tenants", tenants.size());

            return ResponseEntity.ok()
                    .body("{\"tenants\": " + tenants + ", \"totalCount\": " + tenants.size() + "}");

        } catch (Exception e) {
            log.error("Error retrieving tenants for developer", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Failed to retrieve tenants.\"}");
        }
    }

    /**
     * Developer-only API to delete a tenant (for cleanup/testing)
     * 
     * @param tenantId       ID of the tenant to delete
     * @param developerToken Secret developer token for authentication
     * @return Success or error response
     */
    @Operation(summary = "Delete Tenant (Developer Only)", description = "Deletes a tenant from the database. Use with extreme caution.", security = @SecurityRequirement(name = "developer-auth"))
    @DeleteMapping("/tenant/{tenantId}")
    public ResponseEntity<?> deleteTenant(
            @PathVariable Long tenantId,
            @RequestHeader("X-Developer-Token") String developerToken) {

        try {
            // Validate developer access
            if (!validateDeveloperAccess(developerToken, null)) {
                log.warn("Unauthorized developer access attempt for tenant deletion");
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("{\"error\": \"Developer access denied. Invalid authentication.\"}");
            }

            // Check if tenant exists
            if (!tenantService.tenantExists(tenantId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("{\"error\": \"Tenant with ID " + tenantId + " not found.\"}");
            }

            // Delete tenant
            tenantService.deleteTenant(tenantId);

            log.info("Developer successfully deleted tenant ID: {}", tenantId);

            return ResponseEntity.ok()
                    .body("{\"message\": \"Tenant successfully deleted\"}");

        } catch (Exception e) {
            log.error("Error deleting tenant {} for developer", tenantId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Failed to delete tenant.\"}");
        }
    }

    /**
     * Validates developer access using token and optional secret
     * In production, this should use proper authentication mechanisms
     */
    private boolean validateDeveloperAccess(String developerToken, String developerSecret) {
        // Check if developer access is enabled
        if (!developerConfig.isDeveloperEnabled()) {
            log.warn("Developer access is disabled");
            return false;
        }

        // In a real implementation, this would:
        // 1. Validate JWT tokens
        // 2. Check against a developer registry
        // 3. Verify secret keys
        // 4. Check IP whitelisting
        // 5. Rate limiting

        // Token validation using configuration
        String validDevToken = developerConfig.getDeveloperToken();
        String validDevSecret = developerConfig.getDeveloperSecret();

        boolean isValid = developerToken != null &&
                developerToken.equals(validDevToken) &&
                (developerSecret == null || developerSecret.equals(validDevSecret));

        return isValid;
    }

    /**
     * Masks developer information for logging (security best practice)
     */
    private String maskDeveloperInfo(String token) {
        if (token == null)
            return "unknown";
        return token.length() > 8 ? token.substring(0, 4) + "***" + token.length() : "***";
    }
}
