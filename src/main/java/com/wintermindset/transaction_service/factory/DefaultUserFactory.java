package com.wintermindset.transaction_service.factory;

import java.time.Instant;
import java.util.Objects;
import java.util.regex.Pattern;

import com.wintermindset.transaction_service.entity.UserEntity;
import com.wintermindset.transaction_service.enums.user.Role;

public class DefaultUserFactory implements UserFactory {

    private static final String USERNAME_PATTERN = 
        "^[a-zA-Z][a-zA-Z0-9_]{1,18}[a-zA-Z0-9]$";
    private static final Pattern USERNAME_PATTERN_COMPILED = Pattern.compile(USERNAME_PATTERN);
    private static final String VALIDATION_RULES = """
        Username requirements:
        1. Length: 3-20 characters
        2. First character: letter (a-z, A-Z)
        3. Last character: letter or digit
        4. Allowed characters: letters, digits, underscore""";

    @Override
    public UserEntity createUser(
            String username,
            String passwordHash,
            Role role,
            Instant creationTime
    ) {
        validateUsername(username);
        validatePasswordHash(passwordHash);
        validateRole(role);
        validateCreationTime(creationTime);
        return new UserEntity(username, passwordHash, role, creationTime);
    }

    private void validateUsername(String username) {
        Objects.requireNonNull(username, "Username must not be null");
        if (username.isBlank()) {
            throw new IllegalArgumentException("Username must not be blank");
        }
        if (username.length() < 3 || username.length() > 20) {
            throw new IllegalArgumentException(
                String.format("Username must be 3-20 characters long (got %d characters)", 
                             username.length())
            );
        }
        if (!USERNAME_PATTERN_COMPILED.matcher(username).matches()) {
            throw new IllegalArgumentException(
                String.format("Invalid username: '%s'. %s", username, VALIDATION_RULES)
            );
        }
    }

    private void validatePasswordHash(String passwordHash) {
        Objects.requireNonNull(passwordHash, "Password hash must not be null");
        if (passwordHash.isBlank()) {
            throw new IllegalArgumentException("Password hash must not be blank");
        }
    }

    private void validateRole(Role role) {
        Objects.requireNonNull(role, "Role must not be null");
    }

    private void validateCreationTime(Instant creationTime) {
        Objects.requireNonNull(creationTime, "Creation time must not be null");
    }
}
