package com.mobile.pomodoro.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import com.mobile.pomodoro.room.entity.User;


@Database(entities = {User.class}, version = 5, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase { ;
}