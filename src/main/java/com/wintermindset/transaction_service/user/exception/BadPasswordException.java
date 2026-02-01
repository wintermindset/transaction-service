package com.wintermindset.transaction_service.user.exception;

public class BadPasswordException extends RuntimeException {
    
    public BadPasswordException() {
        super();
    }

    public BadPasswordException(String message) {
        super(message);
    }

    public BadPasswordException(Throwable cause) {
        super(cause);
    }

    public BadPasswordException(String message, Throwable cause) {
        super(message, cause);
    }

    public BadPasswordException(
                String message,
                Throwable cause,
                boolean enableSuppression,
                boolean writableStackTrace
    ) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
