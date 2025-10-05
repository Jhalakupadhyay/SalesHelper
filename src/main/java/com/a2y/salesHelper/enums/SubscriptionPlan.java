package com.a2y.salesHelper.enums;

/**
 * Enum representing different subscription plans available for tenants
 */
public enum SubscriptionPlan {

    /**
     * Free tier - Basic features with limited access
     */
    FREE("Free", "Basic features with limited access"),

    /**
     * Basic tier - Standard features for small businesses
     */
    BASIC("Basic", "Standard features for small businesses"),

    /**
     * Professional tier - Advanced features for growing companies
     */
    PREMIUM("Premium", "Advanced features for growing companies"),

    /**
     * Enterprise tier - Full features for large organizations
     */
    ENTERPRISE("Enterprise", "Full features for large organizations"),

    /**
     * Custom tier - Tailored solutions for specific needs
     */
    CUSTOM("Custom", "Tailored solutions for specific needs");

    private final String displayName;
    private final String description;

    SubscriptionPlan(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    /**
     * Get the display name for the subscription plan
     * 
     * @return Display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Get the description of the subscription plan
     * 
     * @return Description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Check if this is a premium plan (Paid tiers)
     * 
     * @return true if premium, false if free
     */
    public boolean isPremium() {
        return this != FREE;
    }

    /**
     * Check if this plan supports advanced features
     * 
     * @return true if advanced features are available
     */
    public boolean supportsAdvancedFeatures() {
        return this == PREMIUM || this == ENTERPRISE || this == CUSTOM;
    }

    /**
     * Get plan tiers for API responses
     * 
     * @return Tier level (1-FREE, 2-BASIC, 3-PROFESSIONAL, 4-ENTERPRISE, 5-CUSTOM)
     */
    public int getTier() {
        return switch (this) {
            case FREE -> 1;
            case BASIC -> 2;
            case PREMIUM -> 3;
            case ENTERPRISE -> 4;
            case CUSTOM -> 5;
        };
    }
}
