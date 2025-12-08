package com.smartmes.enums;

/**
 * Downtime Type Enum
 * Types of downtime in production
 */
public enum DowntimeType {
    /**
     * Equipment failure
     */
    EQUIPMENT_FAILURE("Equipment Failure", "Equipment failure or malfunction"),

    /**
     * Material shortage
     */
    MATERIAL_SHORTAGE("Material Shortage", "Insufficient raw materials or components"),

    /**
     * Quality issue
     */
    QUALITY_ISSUE("Quality Issue", "Product quality problem"),

    /**
     * Operator error
     */
    OPERATOR_ERROR("Operator Error", "Human error during operation"),

    /**
     * Other
     */
    OTHER("Other", "Other types of downtime");

    private final String displayName;
    private final String description;

    DowntimeType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
