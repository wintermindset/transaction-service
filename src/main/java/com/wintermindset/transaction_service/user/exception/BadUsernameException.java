package com.wintermindset.transaction_service.user.exception;

public class BadUsernameException extends RuntimeException {
    
    public BadUsernameException() {
        super();
    }

    public BadUsernameException(String message) {
        super(message);
    }

    public BadUsernameException(Throwable cause) {
        super(cause);
    }

    public BadUsernameException(String message, Throwable cause) {
        super(message, cause);
    }

    public BadUsernameException(
        String message,
        Throwable cause,
        boolean enableSuppression,
        boolean writableStackTrace
    ) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}