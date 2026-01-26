package com.wintermindset.transaction_service.service;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.wintermindset.transaction_service.entity.UserEntity;
import com.wintermindset.transaction_service.enums.user.Role;
import com.wintermindset.transaction_service.exception.user.BadPasswordException;
import com.wintermindset.transaction_service.exception.user.UserNotFoundException;
import com.wintermindset.transaction_service.exception.user.UserAlreadyExistsException;
import com.wintermindset.transaction_service.repository.UserRepository;

@Service
public class UserService {

    private static final Pattern USERNAME_FIRST_CHAR_IS_LETTER = Pattern.compile("^[a-zA-Z].*");
    private static final Pattern USERNAME_LAST_CHAR_IS_LETTER_OR_DIGIT = Pattern.compile(".*[a-zA-Z0-9]$");
    private static final Pattern USERNAME_ALLOWED_CHARS = Pattern.compile("^[a-zA-Z0-9_]+$");
    
    private static final Pattern PASSWORD_HAS_CHAR_IN_LOWERCASE = Pattern.compile(".*[a-z].*");
    private static final Pattern PASSWORD_HAS_CHAR_IN_UPPERCASE = Pattern.compile(".*[A-Z].*");
    private static final Pattern PASSWORD_HAS_DIGIT = Pattern.compile(".*\\d.*");
    private static final Pattern PASSWORD_HAS_SPECIAL = Pattern.compile(
            ".*[!@#$%^&*()_+\\[\\]{}|;:'\",.<>?/].*"
    );

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
                UserRepository userRepository,
                PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserEntity createUser(String username, String rawPassword, Role role, Instant creationTime) {
        String trimmedUsername = username.trim();
        validateUsername(trimmedUsername);
        if (userRepository.existsByUsername(trimmedUsername)) {
            throw new UserAlreadyExistsException(username);
        }
        validatePassword(rawPassword);
        validateRole(role);
        validateCreationTime(creationTime);
        String passwordHash = passwordEncoder.encode(rawPassword);
        return userRepository.save(new UserEntity(trimmedUsername, passwordHash, role, creationTime));
    }

    private void validateUsername(String username) {
        Objects.requireNonNull(username, "Username must not be null");
        StringBuilder errors = new StringBuilder();
        if (username.length() < 3 || username.length() > 20) {
            errors.append("\n- Username must be 3-20 characters long.");
        }
        if (!USERNAME_FIRST_CHAR_IS_LETTER.matcher(username).matches()) {
            errors.append("\n- Username must start with letter.");
        }
        if (!USERNAME_LAST_CHAR_IS_LETTER_OR_DIGIT.matcher(username).matches()) {
            errors.append("\n- Username must end with letter or digit.");
        }
        if (!USERNAME_ALLOWED_CHARS.matcher(username).matches()) {
            errors.append(
                "\n- Username must contain only letters, digits and underscores."
            );
        }
        if (!errors.isEmpty()) {
            throw new UserNotFoundException("Bad username." + errors.toString());
        }
    }

    private void validatePassword(String password) {
        Objects.requireNonNull(password, "Password must not be null");
        StringBuilder errors = new StringBuilder();
        if (password.length() < 8 || password.length() > 32) {
            errors.append("\n- Password must be 8-32 characters long.");
        }
        if (!PASSWORD_HAS_CHAR_IN_LOWERCASE.matcher(password).matches()) {
            errors.append("\n- Password must contain at least one lowercase letter.");
        }
        if (!PASSWORD_HAS_CHAR_IN_UPPERCASE.matcher(password).matches()) {
            errors.append("\n- Password must contain at least one uppercase letter.");
        }
        if (!PASSWORD_HAS_DIGIT.matcher(password).matches()) {
            errors.append("\n- Password must contain at least one digit.");
        }
        if (!PASSWORD_HAS_SPECIAL.matcher(password).matches()) {
            errors.append(
                "\n- Password must contain at least one special character (!@#$%^&*()_+[]{}|;:'\",.<>?/)."
            );
        }
        if (!errors.isEmpty()) {
            throw new BadPasswordException("Bad password." + errors.toString());
        }
    }

    private void validateRole(Role role) {
        Objects.requireNonNull(role, "Role must not be null");
    }

    private void validateCreationTime(Instant creationTime) {
        Objects.requireNonNull(creationTime, "Creation time must not be null");
    }

    public Optional<UserEntity> findById(UUID id) {
        return userRepository.findById(id);
    }

    public void updatePassword(UUID userId, String oldPassword, String newPassword) {
        Objects.requireNonNull(userId, "User ID must not be null");
        Objects.requireNonNull(oldPassword, "Old password must not be null");
        Objects.requireNonNull(newPassword, "New password must not be null");
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new BadPasswordException("Old password is incorrect");
        }
        validatePassword(newPassword);
        String newPasswordHash = passwordEncoder.encode(newPassword);
        user.setPasswordHash(newPasswordHash);
        userRepository.save(user);
    }
}
