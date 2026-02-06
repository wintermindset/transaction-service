package com.wintermindset.transaction_service.user.exception;

public class UserIsUnderAgeException extends RuntimeException {
    
    public UserIsUnderAgeException() {
        super();
    }

    public UserIsUnderAgeException(String message) {
        super(message);
    }

    public UserIsUnderAgeException(Throwable cause) {
        super(cause);
    }

    public UserIsUnderAgeException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserIsUnderAgeException(
        String message,
        Throwable cause,
        boolean enableSuppression,
        boolean writableStackTrace
    ) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}