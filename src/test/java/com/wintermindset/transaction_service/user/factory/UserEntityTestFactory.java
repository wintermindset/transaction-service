package com.wintermindset.transaction_service.user.factory;

import java.time.Instant;

import com.wintermindset.transaction_service.user.entity.UserEntity;
import com.wintermindset.transaction_service.user.enums.DeactivationReason;
import com.wintermindset.transaction_service.user.enums.UserRole;

public final class UserEntityTestFactory {

    private UserEntityTestFactory() {
    }

    public static UserEntity createActiveUser() {
        return createActiveUser("testuser", UserRole.USER, "hashed-password", Instant.now());
    }

    public static UserEntity createActiveUser(String username) {
        return createActiveUser(username, UserRole.USER, "hashed-password", Instant.now());
    }

    public static UserEntity createActiveUser(String username, UserRole role) {
        return createActiveUser(username, role, "hashed-password", Instant.now());
    }

    public static UserEntity createActiveUser(String username, UserRole role, String passwordHash) {
        return createActiveUser(username, role, passwordHash, Instant.now());
    }

    public static UserEntity createActiveUser(String username, UserRole role, String passwordHash, Instant createdAt) {
        return new UserEntity(username, passwordHash, role, createdAt);
    }

    public static UserEntity createDeactivatedUser() {
        UserEntity user = createActiveUser();
        user.deactivate(Instant.now(), DeactivationReason.ADMIN_ACTION, UserRole.ADMIN);
        return user;
    }

    public static UserEntity createDeactivatedUser(String username) {
        UserEntity user = createActiveUser(username);
        user.deactivate(Instant.now(), DeactivationReason.ADMIN_ACTION, UserRole.ADMIN);
        return user;
    }

    public static UserEntity createDeactivatedUser(String username, UserRole role) {
        UserEntity user = createActiveUser(username, role);
        user.deactivate(Instant.now(), DeactivationReason.ADMIN_ACTION, UserRole.ADMIN);
        return user;
    }

    public static UserEntity createDeactivatedUser(String username, UserRole role, Instant deactivatedAt) {
        UserEntity user = createActiveUser(username, role);
        user.deactivate(deactivatedAt, DeactivationReason.ADMIN_ACTION, UserRole.ADMIN);
        return user;
    }
}
