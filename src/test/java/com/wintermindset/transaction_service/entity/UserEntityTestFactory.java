package com.wintermindset.transaction_service.entity;

import java.time.Instant;

import com.wintermindset.transaction_service.enums.user.DeactivationReason;
import com.wintermindset.transaction_service.enums.user.Role;

public final class UserEntityTestFactory {

    private UserEntityTestFactory() {
    }

    public static UserEntity createActiveUser() {
        return new UserEntity(
                "testuser",
                "hashed-password",
                Role.USER,
                Instant.now()
        );
    }

    public static UserEntity createDeactivatedUser() {
        UserEntity user = createActiveUser();
        user.deactivate(Instant.now(), DeactivationReason.ADMIN_ACTION, Role.ADMIN);
        return user;
    }
}
