package com.mobile.pomodoro.room.repo;

import androidx.room.*;
import com.mobile.pomodoro.room.entity.User;

@Dao
public abstract class UserRepository{
    @Insert(onConflict = OnConflictStrategy.ABORT)
    public abstract void insert(User user);

    @Update
    public abstract void update(User user);

}