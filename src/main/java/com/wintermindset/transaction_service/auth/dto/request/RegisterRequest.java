package com.wintermindset.transaction_service.auth.dto.request;

public record RegisterRequest(
        String username,
        String password,
        String role
) {
    
}
