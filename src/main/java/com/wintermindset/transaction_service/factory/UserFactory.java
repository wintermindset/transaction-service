package com.wintermindset.transaction_service.factory;

import java.time.Instant;

import com.wintermindset.transaction_service.entity.UserEntity;
import com.wintermindset.transaction_service.enums.user.Role;

public interface UserFactory {
    UserEntity createUser(
            String username,
            String passwordHash,
            Role role,
            Instant creationTime
    );
}
