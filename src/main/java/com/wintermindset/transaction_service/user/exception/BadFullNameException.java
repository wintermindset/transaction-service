package com.wintermindset.transaction_service.user.exception;

public class BadFullNameException extends RuntimeException {
    
    public BadFullNameException() {
        super();
    }

    public BadFullNameException(String message) {
        super(message);
    }

    public BadFullNameException(Throwable cause) {
        super(cause);
    }

    public BadFullNameException(String message, Throwable cause) {
        super(message, cause);
    }

    public BadFullNameException(
        String message,
        Throwable cause,
        boolean enableSuppression,
        boolean writableStackTrace
    ) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}