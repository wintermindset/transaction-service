package com.wintermindset.transaction_service.user.service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.wintermindset.transaction_service.user.exception.BadPasswordException;
import com.wintermindset.transaction_service.user.exception.UserAlreadyExistsException;
import com.wintermindset.transaction_service.user.exception.UserNotFoundException;
import com.wintermindset.transaction_service.user.command.CreateUserContactCommand;
import com.wintermindset.transaction_service.user.command.CreateUserProfileCommand;
import com.wintermindset.transaction_service.user.entity.UserContactEntity;
import com.wintermindset.transaction_service.user.entity.UserProfileEntity;
import com.wintermindset.transaction_service.user.repository.UserContactRepository;
import com.wintermindset.transaction_service.user.repository.UserProfileRepository;
import com.wintermindset.transaction_service.user.validator.UserContactValidator;
import com.wintermindset.transaction_service.user.validator.UserProfileValidator;

@Service
public class UserService {
    
    private final UserProfileRepository userProfileRepository;
    private final UserContactRepository userContactRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserProfileValidator userProfileValidator;
    private final UserContactValidator userContactValidator;

    public UserService(
        UserProfileRepository userProfileRepository,
        UserContactRepository userContactRepository,
        PasswordEncoder passwordEncoder,
        UserProfileValidator userProfileValidator,
        UserContactValidator userContactValidator
    ) {
        this.userProfileRepository = userProfileRepository;
        this.userContactRepository = userContactRepository;
        this.passwordEncoder = passwordEncoder;
        this.userProfileValidator = userProfileValidator;
        this.userContactValidator = userContactValidator;
    }

    public Optional<UserProfileEntity> getByUsername(String username) {
        userProfileValidator.validateUsername(username);
        return userProfileRepository.findByUsername(username);
    }

    public UserProfileEntity createUserProfile(
        CreateUserProfileCommand createUserCommand
    ) {
        validateCreateUserProfileCommand(createUserCommand);
        String passwordHash = passwordEncoder.encode(
            createUserCommand.rawPassword()
        );
        return userProfileRepository.save(
            new UserProfileEntity(
                createUserCommand.username(),
                passwordHash,
                createUserCommand.fullName(),
                createUserCommand.birthday(),
                createUserCommand.role(),
                Instant.now()
            )
        );
    }

    private void validateCreateUserProfileCommand(CreateUserProfileCommand command) {
        userProfileValidator.validate(command);
        String username = command.username();
        if (userProfileRepository.existsByUsername(username)) {
            throw new UserAlreadyExistsException(
                "User with username " + username + " already exists"
            );
        }
    }

    public UserContactEntity createUserContact(CreateUserContactCommand command) {
        userContactValidator.validate(command);
        UserProfileEntity user = userProfileRepository.findById(command.userId())
            .orElseThrow(() -> new UserNotFoundException(
                "User not found with id: " + command.userId()
            )
        );
        UserContactEntity contact = new UserContactEntity(
            user,
            command.contactType(),
            command.value(),
            Instant.now()
        );
        return userContactRepository.save(contact);
    }

    public boolean checkPassword(UUID userId, String rawPassword) {
        UserProfileEntity user = userProfileRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        return passwordEncoder.matches(rawPassword, user.getPasswordHash());
    }

    public void updatePassword(UUID userId, String oldPassword, String newPassword) {
        userProfileValidator.validatePassword(newPassword);
        UserProfileEntity user = userProfileRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new BadPasswordException("Old password is incorrect");
        }
        String newPasswordHash = passwordEncoder.encode(newPassword);
        user.changePasswordHash(newPasswordHash, Instant.now());
        userProfileRepository.save(user);
    }

    public UserProfileEntity getReferenceByUsername(String username) {
        return userProfileRepository.findByUsername(username)
            .orElseThrow(() -> new UserNotFoundException(
                "User with username " + username + " doesn't exist"
            )
        );
    }
}