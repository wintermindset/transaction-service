package com.wintermindset.transaction_service.entity;

public enum Role {
    ADMIN(true),    
    USER(false);

    private final boolean canManageUsers;

    private Role(boolean canManageUsers) {
        this.canManageUsers = canManageUsers;
    }

    public boolean canManageUsers() {
        return canManageUsers;
    }
}
