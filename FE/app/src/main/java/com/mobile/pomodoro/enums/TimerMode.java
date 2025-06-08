package com.mobile.pomodoro.enums;

import androidx.annotation.NonNull;

// enum hiển thị tên mode và thời gian
public enum TimerMode {
    FOCUS("FOCUS TIME", 1800000L),      // 30p cho focus mode
    SHORT_BREAK("SHORT BREAK", 300000L), // 5p
    LONG_BREAK("LONG BREAK", 900000L);   // 15p

    private final String displayName;
    private long duration;

    TimerMode(String displayName, long duration) {
        this.displayName = displayName;
        this.duration = duration;
    }

    public String getDisplayName() {
        return displayName;
    }

    public long getDuration() {
        return duration;
    }

    public long getDurationInMinutes() {
        return duration / 60000; //chuyển từ mili giây sang phút
    }

    // cập nhật thời gian (setting)
    public void updateDuration(long newDuration) {
        this.duration = newDuration;
    }

    // chuyển phút sang mili giây, tại CountDownTimer dùng mili giây
    public void updateDurationFromMinutes(long minutes) {
        this.duration = minutes * 60 * 1000;
    }


    @NonNull
    @Override
    public String toString() {
        return "TimerMode{" +
                "displayName='" + displayName + '\'' +
                ", duration=" + duration +
                '}';
    }
}
