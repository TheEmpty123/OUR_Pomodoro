package com.mobile.pomodoro.Model;

import java.util.List;

public class Plan {
    private long id;
    private long userId;
    private String title;

    private List<PlanTask> plantasks;

    public Plan(long id, long userId, String title, List<PlanTask> plantasks) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.plantasks=plantasks;
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
}
