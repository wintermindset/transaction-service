package com.wintermindset.transaction_service.user.command;

import java.time.LocalDate;

import com.wintermindset.transaction_service.user.enums.UserRole;

public record CreateUserProfileCommand(
    String username,
    String rawPassword,
    String fullName,
    LocalDate birthday,
    UserRole role
) {}