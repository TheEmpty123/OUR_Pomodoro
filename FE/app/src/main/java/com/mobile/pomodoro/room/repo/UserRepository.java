package com.mobile.pomodoro.room.repo;

import androidx.room.*;
import com.mobile.pomodoro.room.entity.User;

@Dao
public abstract class UserRepository extends BaseRepository<User>{
    @Override
    @Insert(onConflict = OnConflictStrategy.ABORT)
    public abstract void insert(User user);

    @Override
    @Update
    public abstract void update(User user);

}