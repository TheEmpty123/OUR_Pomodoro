package com.mobile.pomodoro.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.mobile.pomodoro.room.entity.DailyTask;
import com.mobile.pomodoro.room.entity.Plan;
import com.mobile.pomodoro.room.entity.PlanTask;
import com.mobile.pomodoro.room.entity.TodoItem;
import com.mobile.pomodoro.room.entity.User;
import com.mobile.pomodoro.room.entity.relation.PlanWithTasks;
import com.mobile.pomodoro.room.repo.DailyTaskRepository;
import com.mobile.pomodoro.room.repo.PlanRepository;
import com.mobile.pomodoro.room.repo.TodoRepository;
import com.mobile.pomodoro.room.repo.UserRepository;

@Database(entities = {User.class, TodoItem.class, Plan.class, PlanTask.class, DailyTask.class}, version = 6, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserRepository user();
    public abstract TodoRepository todoItem();
    public abstract PlanRepository plan();
    public abstract DailyTaskRepository daily();
}