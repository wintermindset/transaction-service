package com.wintermindset.transaction_service.user.factory;

import java.time.Instant;
import java.time.LocalDate;

import com.wintermindset.transaction_service.user.enums.DeactivationReason;
import com.wintermindset.transaction_service.user.enums.DeactivationSource;
import com.wintermindset.transaction_service.user.enums.UserRole;
import com.wintermindset.transaction_service.user.entity.UserProfileEntity;

public final class UserEntityTestFactory {

    private UserEntityTestFactory() {
    }

    public static UserProfileEntity createActiveUser() {
        return createActiveUser("testuser", UserRole.USER, "hashed-password", Instant.now());
    }

    public static UserProfileEntity createActiveUser(String username) {
        return createActiveUser(username, UserRole.USER, "hashed-password", Instant.now());
    }

    public static UserProfileEntity createActiveUser(String username, UserRole role) {
        return createActiveUser(username, role, "hashed-password", Instant.now());
    }

    public static UserProfileEntity createActiveUser(String username, UserRole role, String passwordHash) {
        return createActiveUser(username, role, passwordHash, Instant.now());
    }

    public static UserProfileEntity createActiveUser(String username, UserRole role, String passwordHash, Instant createdAt) {
        String fullName = "Test User";
        LocalDate birthday = LocalDate.of(2000, 1, 1);
        return new UserProfileEntity(
                username,
                passwordHash,
                fullName,
                birthday,
                role,
                createdAt
        );
    }

    public static UserProfileEntity createDeactivatedUser() {
        UserProfileEntity user = createActiveUser();
        user.deactivate(Instant.now(), DeactivationReason.ADMIN_ACTION, DeactivationSource.ADMIN);
        return user;
    }

    public static UserProfileEntity createDeactivatedUser(String username) {
        UserProfileEntity user = createActiveUser(username);
        user.deactivate(Instant.now(), DeactivationReason.ADMIN_ACTION, DeactivationSource.ADMIN);
        return user;
    }

    public static UserProfileEntity createDeactivatedUser(String username, UserRole role) {
        UserProfileEntity user = createActiveUser(username, role);
        user.deactivate(Instant.now(), DeactivationReason.ADMIN_ACTION, DeactivationSource.ADMIN);
        return user;
    }

    public static UserProfileEntity createDeactivatedUser(String username, UserRole role, Instant deactivatedAt) {
        UserProfileEntity user = createActiveUser(username, role);
        user.deactivate(deactivatedAt, DeactivationReason.ADMIN_ACTION, DeactivationSource.ADMIN);
        return user;
    }
}