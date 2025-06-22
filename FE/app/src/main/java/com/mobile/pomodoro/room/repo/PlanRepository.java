package com.mobile.pomodoro.room.repo;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.mobile.pomodoro.room.entity.Plan;
import com.mobile.pomodoro.room.entity.PlanTask;
import com.mobile.pomodoro.room.entity.relation.PlanWithTasks;

import java.util.List;

@Dao
public abstract class PlanRepository{
    @Transaction
    @Query("SELECT * FROM plan_task")
    public abstract List<PlanWithTasks> getAll();

    @Transaction
    @Query("SELECT * FROM 'plan' ORDER BY id DESC LIMIT 1;")
    public abstract List<Plan> getRecentPlant();


    @Insert(onConflict = OnConflictStrategy.ABORT)
    public abstract void insert(Plan plan);

    @Insert(onConflict = OnConflictStrategy.ABORT)
    public abstract void insert(PlanTask planTask);
}
