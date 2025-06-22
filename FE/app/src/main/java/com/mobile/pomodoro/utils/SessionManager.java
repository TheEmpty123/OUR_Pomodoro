package com.mobile.pomodoro.utils;

import android.view.View;

import com.mobile.pomodoro.R;
import com.mobile.pomodoro.enums.TimerMode;
import com.mobile.pomodoro.response_dto.PlanTaskResponseDTO;
import com.mobile.pomodoro.utils.Timer.TimerAnimationHelper;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

// quản lý luồng làm việc của các task/session bao gồm:

/// Chuyển task
/// Đếm session hoàn thành
/// cập nhật lại UI
public class SessionManager {

    public interface SessionCallback {
        void onTaskChanged(PlanTaskResponseDTO task);

        void onAllTasksCompleted();

        void onSessionStatsUpdated(int completed, int current);

        void onModeAutoSwitch(TimerMode newMode);
    }

    @Getter
    private List<PlanTaskResponseDTO> taskList;
    // Setters for state restoration
    @Setter
    @Getter
    private int currentTaskIndex = 0;
    private int completedSessionsCount = 0;
    private int totalSessions = 4;
    private SessionCallback callback;

    public SessionManager(SessionCallback callback) {
        this.callback = callback;
    }

    public void initializeSession(List<PlanTaskResponseDTO> taskList) {
        this.taskList = taskList;
        this.totalSessions = taskList != null ? taskList.size() : 4;
        this.currentTaskIndex = 0;
        this.completedSessionsCount = 0;
    }

    // lấy ra task hiện tại đang làm
    public PlanTaskResponseDTO getCurrentTask() {
        if (taskList != null && !taskList.isEmpty() && currentTaskIndex < taskList.size()) {
            return taskList.get(currentTaskIndex);
        }
        return null;
    }

    // Chuyển sang task tiếp theo theo flow: work -> short break -> long break
    public boolean moveToNextTask() {
        if (taskList != null && currentTaskIndex < taskList.size() - 1) {
            currentTaskIndex++;
            completedSessionsCount++;

            PlanTaskResponseDTO currentTask = getCurrentTask();

            if (callback != null) {
                callback.onTaskChanged(currentTask);
                callback.onSessionStatsUpdated(completedSessionsCount, currentTaskIndex + 1);

                // Tự động chuyển mode dựa vào task name/type
                TimerMode autoMode = detectTimerModeFromTask(currentTask);
                if (autoMode != null) {
                    callback.onModeAutoSwitch(autoMode);
                }
            }
            return true; // Has next task
        } else {
            // All tasks completed
            if (callback != null) {
                callback.onAllTasksCompleted();
            }
            resetSession();
            return false; // No more tasks
        }
    }

    // Tự động phát hiện mode dựa vào task name
    private TimerMode detectTimerModeFromTask(PlanTaskResponseDTO task) {
        if (task == null || task.getPlan_title() == null) {
            return TimerMode.FOCUS; // Default
        }

        String taskName = task.getPlan_title().toLowerCase().trim();

        // Kiểm tra theo tên task trước
        if (taskName.contains("short break") || taskName.contains("nghỉ ngắn")) {
            return TimerMode.SHORT_BREAK;
        } else if (taskName.contains("long break") || taskName.contains("nghỉ dài")) {
            return TimerMode.LONG_BREAK;
        } else if (taskName.contains("focus") || taskName.contains("work") || taskName.contains("làm việc")) {
            return TimerMode.FOCUS;
        }

        // Nếu không có tên rõ ràng, áp dụng logic Pomodoro
        // Logic: Focus (1) -> Break (2) -> Focus (3) -> Break (4) -> Focus (5) -> Long Break (6)
        // Sau 4 focus sessions (index 8) -> Long Break
        if (completedSessionsCount > 0 && completedSessionsCount % 8 == 0) {
            return TimerMode.LONG_BREAK;
        } else if (completedSessionsCount % 2 == 0) {
            return TimerMode.SHORT_BREAK;
        } else {
            return TimerMode.FOCUS;
        }
    }

    public void resetSession() {
        currentTaskIndex = 0;
        completedSessionsCount = 0;

        if (callback != null && getCurrentTask() != null) {
            callback.onTaskChanged(getCurrentTask());
            callback.onSessionStatsUpdated(completedSessionsCount, currentTaskIndex + 1);
            callback.onModeAutoSwitch(TimerMode.FOCUS);
        }
    }

    // đánh dấu session complete nhưng không chuyển sang task tiếp theo
    public void completeCurrentSession() {
        completedSessionsCount++;

        if (callback != null) {
            callback.onSessionStatsUpdated(completedSessionsCount, currentTaskIndex + 1);
        }
    }

    public void updateSessionIndicators(View[] indicators) {
        for (int i = 0; i < indicators.length; i++) {
            if (i < totalSessions) {
                indicators[i].setVisibility(View.VISIBLE);

                if (i < completedSessionsCount) {
                    // Completed session
                    indicators[i].setBackgroundResource(R.drawable.active_indicator);
                    TimerAnimationHelper.animateIndicatorCompletion(indicators[i]);
                } else if (i == currentTaskIndex) {
                    // Current session
                    indicators[i].setBackgroundResource(R.drawable.active_indicator);
                    TimerAnimationHelper.animateIndicatorCurrent(indicators[i]);
                } else {
                    // Future session
                    indicators[i].setBackgroundResource(R.drawable.inactive_indicator);
                }
            } else {
                indicators[i].setVisibility(View.GONE);
            }
        }
    }

    // hiển thị thống kê session
    //    public void updateSessionStats(TextView completedSessionsView, TextView currentSessionView) {
//    }
    public boolean isAllTasksCompleted() {
        return taskList != null && currentTaskIndex >= taskList.size() - 1;
    }

    // số session đã hoàn thành
    public int getCompletedSessionsCount() {
        return completedSessionsCount;
    }

    public int getTotalSessions() {
        return totalSessions;
    }

    public void setCompletedSessionsCount(int completedSessionsCount) {
        this.completedSessionsCount = completedSessionsCount;
    }
}