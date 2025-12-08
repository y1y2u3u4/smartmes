package com.smartmes.enums;

/**
 * Downtime Status Enum
 * Status of downtime report
 */
public enum DowntimeStatus {
    /**
     * Pending - Awaiting response
     */
    PENDING("Pending", "Awaiting response"),

    /**
     * Processing - Being handled
     */
    PROCESSING("Processing", "Issue is being handled"),

    /**
     * Resolved - Issue resolved
     */
    RESOLVED("Resolved", "Issue has been resolved");

    private final String displayName;
    private final String description;

    DowntimeStatus(String displayName, String description) {
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
