package com.a2y.salesHelper.enums;

import lombok.*;
import org.springframework.stereotype.Service;

/**
 * Enum representing user roles in the application.
 * Each role has a string value associated with it.
 */
public enum Role {
    ADMIN("Admin"),
    USER("User");

    private final String value;

    Role(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
