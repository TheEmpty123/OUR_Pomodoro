package com.mobile.pomodoro.room.repo;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.mobile.pomodoro.room.entity.DailyTask;

import java.util.List;

@Dao
public abstract class DailyTaskRepository {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    public abstract void insert(DailyTask dailyTask);

    @Transaction
    @Query("SELECT * FROM daily_task")
    public abstract List<DailyTask> getAll();

    @Update
    public abstract void update(DailyTask dailyTask);
}
