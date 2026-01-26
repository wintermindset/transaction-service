package com.wintermindset.transaction_service.factory;

import java.time.Instant;

import com.wintermindset.transaction_service.entity.Role;
import com.wintermindset.transaction_service.entity.UserEntity;

public interface UserFactory {
    public UserEntity createUser(
            String username,
            String passwordHash,
            Role role,
            Instant creationTime
    );
}
