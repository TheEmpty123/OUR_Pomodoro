package com.mobile.pomodoro.room.repo;

import com.mobile.pomodoro.room.entity.BaseEntity;

import java.util.List;

// Depreciated
public abstract class BaseRepository<T extends BaseEntity> {
    public abstract void insert(T item);
    public abstract void update(T item);
    public abstract List<T> getAll();
}
