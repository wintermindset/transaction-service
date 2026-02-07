package com.wintermindset.transaction_service.user.validator;

import java.time.LocalDate;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.wintermindset.transaction_service.user.command.CreateUserProfileCommand;
import com.wintermindset.transaction_service.user.exception.BadContactException;
import com.wintermindset.transaction_service.user.exception.BadFullNameException;
import com.wintermindset.transaction_service.user.exception.UserIsUnderAgeException;
import com.wintermindset.transaction_service.user.exception.UserNotFoundException;

@Component
public class UserProfileValidator {
    
    private static final Pattern USERNAME_FIRST_CHAR_IS_LETTER = Pattern.compile("^[a-zA-Z].*");
    private static final Pattern USERNAME_ALLOWED_CHARS = Pattern.compile("^[a-zA-Z0-9_]+$");
    private static final Pattern USERNAME_LAST_CHAR_IS_LETTER_OR_DIGIT = Pattern.compile(
        ".*[a-zA-Z0-9]$"
    );
    
    private static final Pattern PASSWORD_HAS_CHAR_IN_LOWERCASE = Pattern.compile(".*[a-z].*");
    private static final Pattern PASSWORD_HAS_CHAR_IN_UPPERCASE = Pattern.compile(".*[A-Z].*");
    private static final Pattern PASSWORD_HAS_DIGIT = Pattern.compile(".*\\d.*");
    private static final Pattern PASSWORD_HAS_SPECIAL = Pattern.compile(
            ".*[!@#$%^&*()_+\\[\\]{}|;:'\",.<>?/].*"
    );

    private static final Pattern FULL_NAME_PATTERN = Pattern.compile(
        "^[A-Z][a-z]+ [A-Z][a-z]+ [A-Z][a-z]+$"
    );

    public void validate(CreateUserProfileCommand command) {
        validateUsername(command.username());
        validatePassword(command.rawPassword());
        validateBirthday(command.birthday());
        validateFullName(command.fullName());
    }

    public void validateUsername(String username) {
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

    public void validatePassword(String password) {
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
            throw new BadContactException("Bad password." + errors.toString());
        }
    }

    public void validateBirthday(LocalDate birthday) {
        if (birthday.plusYears(18).isAfter(LocalDate.now())) {
            throw new UserIsUnderAgeException();
        }
    }

    public void validateFullName(String fullName) {
        if (!FULL_NAME_PATTERN.matcher(fullName).matches()) {
            throw new BadFullNameException();
        }
    }
}