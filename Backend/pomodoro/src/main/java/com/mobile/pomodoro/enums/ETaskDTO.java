package com.mobile.pomodoro.enums;

public enum ETaskDTO {
    WORK ("WORK"),
    BREAK ("BREAK"),
    LONG_BREAK ("LONG_BREAK"),
    ;

    final String work;

    ETaskDTO(String work) {
        this.work = work;
    }
}
