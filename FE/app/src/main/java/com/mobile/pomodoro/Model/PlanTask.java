package com.mobile.pomodoro.Model;

public class PlanTask {
    private long id;
    private long planId;
    private String planName;
    private double duration;
    private int taskOrder;
    private int shortBreak;
    private int longBreak;

    public PlanTask(String planName, double duration, int taskOrder, int shortBreak, int longBreak) {
        this.planName = planName;
        this.duration = duration;
        this.taskOrder = taskOrder;
        this.shortBreak = shortBreak;
        this.longBreak = longBreak;
    }

    public PlanTask(String planName, double duration,  int shortBreak, int longBreak) {
        this.duration = duration;
        this.planName = planName;
        this.shortBreak = shortBreak;
        this.longBreak = longBreak;
    }

    public String getPlanName() { return planName; }

    public double getDuration() { return duration; }

    public int getTaskOrder() { return taskOrder; }

    public int getShortBreak() { return shortBreak; }

    public int getLongBreak() { return longBreak; }

    public void setId(long id) {
        this.id = id;
    }

    public void setPlanId(long planId) {
        this.planId = planId;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public void setTaskOrder(int taskOrder) {
        this.taskOrder = taskOrder;
    }

    public void setShortBreak(int shortBreak) {
        this.shortBreak = shortBreak;
    }

    public void setLongBreak(int longBreak) {
        this.longBreak = longBreak;
    }
}

