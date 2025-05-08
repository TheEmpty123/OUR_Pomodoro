package com.mobile.pomodoro.CustomException;

public class FailedToLoginException extends Exception{
    public FailedToLoginException(String message) {
        super(message);
    }

    public FailedToLoginException(String message, Throwable cause) {
        super(message, cause);
    }

    public FailedToLoginException(Throwable cause) {
        super(cause);
    }
}
