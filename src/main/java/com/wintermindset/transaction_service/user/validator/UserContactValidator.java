package com.wintermindset.transaction_service.user.validator;

import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.wintermindset.transaction_service.user.command.CreateUserContactCommand;
import com.wintermindset.transaction_service.user.enums.ContactType;
import com.wintermindset.transaction_service.user.exception.BadContactException;

@Component
public class UserContactValidator {

    private static final Pattern RU_PHONE_PATTERN = Pattern.compile("^\\+7\\d{10}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    public void validate(CreateUserContactCommand command) {
        if (command.contactType() == ContactType.PHONE) {
            validatePhone(command.value());
        } else {
            validateEmail(command.value());
        }
    }

    private void validatePhone(String phone) {
        if (!RU_PHONE_PATTERN.matcher(phone).matches()) {
            throw new BadContactException("Invalid phone number");
        }
    }

    private void validateEmail(String email) {
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new BadContactException("Invalid email");
        }
    }
}