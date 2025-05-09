package com.mobile.pomodoro.entity;

import java.util.List;

public class Plan {
    private long id;
    private long userId;
    private String title;

    private List<PlanTask> plantasks;

    private long sBreakDuration;
    private long  lBreakDuration;

    public Plan(long id, long userId, String title, List<PlanTask> plantasks) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.plantasks=plantasks;
    }

    public Plan() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<PlanTask> getPlantasks() {
        return plantasks;
    }

    public void setPlantasks(List<PlanTask> plantasks) {
        this.plantasks = plantasks;
    }

    public long getsBreakDuration() {
        return sBreakDuration;
    }

    public void setsBreakDuration(long sBreakDuration) {
        this.sBreakDuration = sBreakDuration;
    }

    public long getlBreakDuration() {
        return lBreakDuration;
    }

    public void setlBreakDuration(long lBreakDuration) {
        this.lBreakDuration = lBreakDuration;
    }
}
