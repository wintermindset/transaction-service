package com.wintermindset.transaction_service.user.exception;

public class BadContactException extends RuntimeException {
    
    public BadContactException() {
        super();
    }

    public BadContactException(String message) {
        super(message);
    }

    public BadContactException(Throwable cause) {
        super(cause);
    }

    public BadContactException(String message, Throwable cause) {
        super(message, cause);
    }

    public BadContactException(
        String message,
        Throwable cause,
        boolean enableSuppression,
        boolean writableStackTrace
    ) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}