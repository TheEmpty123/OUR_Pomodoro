package com.mobile.pomodoro.enums;

import androidx.annotation.NonNull;

// enum hiển thị tên mode và thời gian
// cập nhật thời gian mặc định default setting thì vào đây chỉnh
public enum TimerMode {
    FOCUS("FOCUS TIME", 1800000L),      // 30p cho focus mode
    SHORT_BREAK("SHORT BREAK", 300000L), // 5p
    LONG_BREAK("LONG BREAK", 900000L);   // 15p

    private final String displayName;
    private final long originalDuration;   // biến này làm thời gian gốc
    private long duration;                 // thời gian hiện tại (có thể thay đổi từ settings)

    TimerMode(String displayName, long duration) {
        this.displayName = displayName;
        this.originalDuration = duration;  // Lưu giá trị gốc để reset
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

    // dùng khi reset thời gian về mặc định, nó là biến lưu giá trị gốc, không bao giờ bị thay đổi
    public long getOriginalDuration() {
        return originalDuration;
    }

    public void resetToOriginal() {
        this.duration = this.originalDuration;
    }

    public static long getDefaultDuration(TimerMode mode) {
        return mode.getOriginalDuration();
    }

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
        return "TimerMode{" + "displayName='" + displayName + '\'' + ", originalDuration=" + originalDuration + ", currentDuration=" + duration + '}';
    }
}
