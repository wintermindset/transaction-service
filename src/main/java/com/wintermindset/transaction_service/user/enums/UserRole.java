package com.wintermindset.transaction_service.user.enums;

public enum UserRole {

    ADMIN(true),    
    USER(false);

    private final boolean canManageUsers;

    private UserRole(boolean canManageUsers) {
        this.canManageUsers = canManageUsers;
    }

    public boolean canManageUsers() {
        return canManageUsers;
    }
}