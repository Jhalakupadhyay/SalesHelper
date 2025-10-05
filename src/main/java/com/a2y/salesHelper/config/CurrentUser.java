package com.a2y.salesHelper.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class CurrentUser {
    private CurrentUser() {
    }

    public static Long getTenantId() {
        UserPrincipal up = getUserPrincipal();
        return up != null ? up.getTenantId() : null;
    }

    public static Long getUserId() {
        UserPrincipal up = getUserPrincipal();
        return up != null ? up.getUserId() : null;
    }

    public static String getEmail() {
        UserPrincipal up = getUserPrincipal();
        return up != null ? up.getUsername() : null;
    }

    private static UserPrincipal getUserPrincipal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null)
            return null;
        Object principal = auth.getPrincipal();
        if (principal instanceof UserPrincipal) {
            return (UserPrincipal) principal;
        }
        return null;
    }
}
