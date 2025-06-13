package com.mobile.pomodoro.utils;

import android.view.View;

import com.mobile.pomodoro.R;
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

    public boolean moveToNextTask() {
        if (taskList != null && currentTaskIndex < taskList.size() - 1) {
            currentTaskIndex++;
            completedSessionsCount++;

            if (callback != null) {
                callback.onTaskChanged(getCurrentTask());
                callback.onSessionStatsUpdated(completedSessionsCount, currentTaskIndex + 1);
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

    public void resetSession() {
        currentTaskIndex = 0;
        completedSessionsCount = 0;

        if (callback != null && getCurrentTask() != null) {
            callback.onTaskChanged(getCurrentTask());
            callback.onSessionStatsUpdated(completedSessionsCount, currentTaskIndex + 1);
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

    // tổng session
    public int getTotalSessions() {
        return totalSessions;
    }

    //  set số session đã hoàn thành
    public void setCompletedSessionsCount(int completedSessionsCount) {
        this.completedSessionsCount = completedSessionsCount;
    }
}