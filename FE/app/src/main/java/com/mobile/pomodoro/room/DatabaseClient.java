package com.mobile.pomodoro.room;

import android.content.Context;
import androidx.room.Room;
import lombok.Getter;

public class DatabaseClient {
    private Context context;
    private static DatabaseClient instance;
    @Getter
    private AppDatabase appDatabase;

    private DatabaseClient(Context context) {
        this.context = context;
        appDatabase = Room.databaseBuilder(context, AppDatabase.class, "data")
                .fallbackToDestructiveMigration()
                .build();
    }

    public static synchronized DatabaseClient getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseClient(context);
        }
        return instance;
    }
}