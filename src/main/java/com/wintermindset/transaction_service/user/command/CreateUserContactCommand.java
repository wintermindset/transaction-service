package com.wintermindset.transaction_service.user.command;

import java.util.UUID;

import com.wintermindset.transaction_service.user.enums.ContactType;

public record CreateUserContactCommand(
    UUID userId,
    ContactType contactType,
    String value
) {}