package com.wintermindset.transaction_service.entity;

import java.time.Instant;

import com.wintermindset.transaction_service.enums.user.DeactivationReason;
import com.wintermindset.transaction_service.enums.user.Role;

public final class UserEntityTestFactory {

    private UserEntityTestFactory() {
    }

    public static UserEntity createActiveUser() {
        return createActiveUser("testuser", Role.USER, "hashed-password", Instant.now());
    }

    public static UserEntity createActiveUser(String username) {
        return createActiveUser(username, Role.USER, "hashed-password", Instant.now());
    }

    public static UserEntity createActiveUser(String username, Role role) {
        return createActiveUser(username, role, "hashed-password", Instant.now());
    }

    public static UserEntity createActiveUser(String username, Role role, String passwordHash) {
        return createActiveUser(username, role, passwordHash, Instant.now());
    }

    public static UserEntity createActiveUser(String username, Role role, String passwordHash, Instant createdAt) {
        return new UserEntity(username, passwordHash, role, createdAt);
    }

    public static UserEntity createDeactivatedUser() {
        UserEntity user = createActiveUser();
        user.deactivate(Instant.now(), DeactivationReason.ADMIN_ACTION, Role.ADMIN);
        return user;
    }

    public static UserEntity createDeactivatedUser(String username) {
        UserEntity user = createActiveUser(username);
        user.deactivate(Instant.now(), DeactivationReason.ADMIN_ACTION, Role.ADMIN);
        return user;
    }

    public static UserEntity createDeactivatedUser(String username, Role role) {
        UserEntity user = createActiveUser(username, role);
        user.deactivate(Instant.now(), DeactivationReason.ADMIN_ACTION, Role.ADMIN);
        return user;
    }

    public static UserEntity createDeactivatedUser(String username, Role role, Instant deactivatedAt) {
        UserEntity user = createActiveUser(username, role);
        user.deactivate(deactivatedAt, DeactivationReason.ADMIN_ACTION, Role.ADMIN);
        return user;
    }
}
