package com.mobile.pomodoro.room.repo;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.mobile.pomodoro.room.entity.relation.PlanWithTasks;

import java.util.List;

@Dao
public abstract class PlanRepository{
    @Transaction
    @Query("SELECT * FROM plan_task")
    public abstract List<PlanWithTasks> getAll();
}
