package com.wintermindset.transaction_service.auth.dto.request;

import jakarta.validation.constraints.NotNull;

public record AuthRequest(
    
    @NotNull
    String username,

    @NotNull
    String password
) {}