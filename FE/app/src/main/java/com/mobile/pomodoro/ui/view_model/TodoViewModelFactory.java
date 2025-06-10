package com.mobile.pomodoro.ui.view_model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.mobile.pomodoro.room.entity.TodoItem;
import com.mobile.pomodoro.room.repo.SingleThreadRepo;
import com.mobile.pomodoro.room.repo.TodoRepository;

public class TodoViewModelFactory  implements ViewModelProvider.Factory {
    private final Application app;
    private final SingleThreadRepo<TodoRepository, TodoItem> repo;

    public TodoViewModelFactory(SingleThreadRepo<TodoRepository, TodoItem> repo){
        this.repo = repo;
        this.app = null;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(repo.getClass())){
            if (app != null){
                return (T) new TodoViewModel(app);
            } else {
                return (T) new TodoViewModel(repo);
            }
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
