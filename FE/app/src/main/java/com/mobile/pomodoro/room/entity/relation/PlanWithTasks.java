package com.mobile.pomodoro.room.entity.relation;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.mobile.pomodoro.room.entity.Plan;
import com.mobile.pomodoro.room.entity.PlanTask;

import java.util.List;

public class PlanWithTasks {
    @Embedded
    public Plan plan;

    @Relation(
            parentColumn = "id",
            entityColumn = "plan_id"
    )
    public List<PlanTask> tasks;
}
