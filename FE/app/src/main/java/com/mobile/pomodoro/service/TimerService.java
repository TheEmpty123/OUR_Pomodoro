package com.mobile.pomodoro.service;

import android.os.CountDownTimer;

import com.mobile.pomodoro.enums.TimerMode;

import java.util.Locale;

import lombok.Getter;

// xử lí logic đếm thời gian, tính progress bar, format giờ phút
public class TimerService {

    public interface TimerCallback {
        void onTick(long millisUntilFinished, String formattedTime, int progressPercentage);

        void onFinish();

        void onTimerStateChanged(boolean isRunning);
    }

    private CountDownTimer countDownTimer;
    // Getters
    @Getter
    private long timeLeftInMillis; // đếm thời gian còn lại
    private long totalTime; // thời gian ban đầu
    @Getter
    private boolean timerRunning = false;
    private TimerCallback callback;
    @Getter
    private TimerMode currentMode = TimerMode.FOCUS;

    public TimerService(TimerCallback callback) {
        this.callback = callback;
    }

    // khởi tạo timer
    public void initializeTimer(TimerMode mode) {
        this.currentMode = mode;
        setTimerForCurrentMode();
    }

    // thiết lập thời gian theo chế độ
    private void setTimerForCurrentMode() {
        timeLeftInMillis = currentMode.getDuration();
        totalTime = timeLeftInMillis;

        if (callback != null) {
            String formattedTime = formatTime(timeLeftInMillis);
            callback.onTick(timeLeftInMillis, formattedTime, 100);
        }
    }

    public void startTimer() {
        // hết thời gian thì reset
        if (timeLeftInMillis <= 0) {
            setTimerForCurrentMode();
        }

        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;

                if (callback != null) {
                    String formattedTime = formatTime(timeLeftInMillis);
                    int progress = calculateProgress();
                    callback.onTick(timeLeftInMillis, formattedTime, progress);
                }
            }

            @Override
            public void onFinish() {
                countDownTimer = null;
                timeLeftInMillis = 0;
                timerRunning = false;

                if (callback != null) {
                    callback.onFinish();
                    callback.onTimerStateChanged(false);
                }
            }
        }.start();

        timerRunning = true;
        if (callback != null) {
            callback.onTimerStateChanged(true);
        }
    }

    public void pauseTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        timerRunning = false;

        if (callback != null) {
            callback.onTimerStateChanged(false);
        }
    }

    public void resetTimer() {
        pauseTimer();
        setTimerForCurrentMode();
    }

    // chuyển sang mode khác (break)
    public void switchToMode(TimerMode mode) {
        pauseTimer();
        currentMode = mode;
        setTimerForCurrentMode();
    }

    // định dạng giờ giấc
    private String formatTime(long milliseconds) {
        int hours = (int) (milliseconds / 3600000);
        int minutes = (int) (milliseconds % 3600000) / 60000;
        int seconds = (int) (milliseconds % 60000) / 1000;

        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
    }

    // tính phần trăm cho progress bar
    private int calculateProgress() {
        if (totalTime > 0) {
            return (int) ((timeLeftInMillis * 100) / totalTime);
        }
        return 0;
    }

    // kiểm tra trạng thái có đang chạy hay không để restore lại về thời gian đã dc cài đặt từ trc
    public void restoreTimerState(long timeLeft, boolean wasRunning) {
        this.timeLeftInMillis = timeLeft;
        this.timerRunning = false; // bắt đầu ở trạng thái pause

        if (timeLeft > 0) {
            this.totalTime = currentMode.getDuration();

            if (callback != null) {
                String formattedTime = formatTime(timeLeftInMillis);
                int progress = calculateProgress();
                callback.onTick(timeLeftInMillis, formattedTime, progress);
            }

            if (wasRunning) {
                startTimer();
            }
        } else {
            setTimerForCurrentMode();
        }
    }

    public void destroy() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        timerRunning = false;
    }
}