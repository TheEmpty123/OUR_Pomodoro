package com.mobile.pomodoro.CustomException;

public class FailedToRegisterException extends Exception{
    public FailedToRegisterException(String message) {
        super(message);
    }

    public FailedToRegisterException(String message, Throwable cause) {
        super(message, cause);
    }

    public FailedToRegisterException(Throwable cause) {
        super(cause);
    }
}
