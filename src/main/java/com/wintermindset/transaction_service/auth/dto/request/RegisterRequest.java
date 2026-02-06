package com.wintermindset.transaction_service.auth.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

    @NotNull
    String username,

    @NotNull
    String password,

    @NotNull
    @NotBlank
    @Size(max = 100)
    String fullName,

    @NotNull
    LocalDate birthday
) {}