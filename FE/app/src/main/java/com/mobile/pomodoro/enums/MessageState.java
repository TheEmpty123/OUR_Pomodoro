package com.mobile.pomodoro.enums;

import androidx.annotation.NonNull;

public enum MessageState {
    LOGIN_SUCCESSFUL("Login successful"),
    INVALID_PASSWORD("Invalid password"),
    USER_NOT_FOUND("User not found"),
    USER_REGISTERED_SUCCESSFULLY("User registered successfully"),
    USERNAME_ALREADY_EXISTS("Username already exists"),
    REGISTRATION_FAILED("Registration failed");

    private final String text;

    private MessageState(String text) {
        this.text = text;
    }

    @NonNull
    @Override
    public String toString() {
        return text;
    }

    public MessageState valueOfLabel(String label) {
        for (MessageState e : values()) {
            if (e.text.equals(label)) {
                return e;
            }
        }
        return null;
    }
}
