package com.mobile.pomodoro.room.repo;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.mobile.pomodoro.room.entity.BaseEntity;
import com.mobile.pomodoro.room.entity.Plan;
import com.mobile.pomodoro.room.entity.TodoItem;

import java.util.List;

@Dao
public abstract class TodoRepository extends BaseRepository<TodoItem>{

    @Override
    @Insert(onConflict = OnConflictStrategy.ABORT)
    public abstract void insert(TodoItem item);

    @Override
    @Update
    public abstract void update(TodoItem item);

    @Override
    @Query("SELECT * FROM todo")
    public abstract  List<TodoItem> getAll();
}
