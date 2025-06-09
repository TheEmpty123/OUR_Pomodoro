package com.mobile.pomodoro.room.repo;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.mobile.pomodoro.room.entity.relation.PlanWithTasks;

@Dao
public interface PlanRepository {
    @Transaction
    @Query("SELECT * FROM plan_task")
    PlanWithTasks getAllPlanWithTasks();
}
