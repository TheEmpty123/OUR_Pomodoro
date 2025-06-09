package com.mobile.pomodoro.room.repo;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.mobile.pomodoro.room.entity.TodoItem;

import java.util.List;

@Dao
public interface TodoRepository {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    void insert(TodoItem item);

    @Update
    void update(TodoItem item);

    @Query("SELECT * FROM todo")
    List<TodoItem> getAllTodo();
}
