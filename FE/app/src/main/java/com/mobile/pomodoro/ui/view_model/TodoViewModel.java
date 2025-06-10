package com.mobile.pomodoro.ui.view_model;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.mobile.pomodoro.room.AppDatabase;
import com.mobile.pomodoro.room.DatabaseClient;
import com.mobile.pomodoro.room.entity.TodoItem;
import com.mobile.pomodoro.room.repo.SingleThreadRepo;
import com.mobile.pomodoro.room.repo.TodoRepository;

import java.util.List;

public class TodoViewModel extends ViewModel {
    private final SingleThreadRepo<TodoRepository,TodoItem> repo;
    private final MediatorLiveData<Resource<List<TodoItem>>> liveData = new MediatorLiveData<>();
    private LiveData<Resource<List<TodoItem>>> source;

    public TodoViewModel(SingleThreadRepo<TodoRepository, TodoItem> repo) {
        this.repo = repo;
    }

    public TodoViewModel(Application app){
        AppDatabase db = DatabaseClient.getInstance(app).getAppDatabase();
        this.repo = new SingleThreadRepo<>(db.todoItem());
    }

    public void loadTodos(){
        if (source != null){
            liveData.removeSource(source);
        }

        source = repo.getAll();
        liveData.addSource(source, liveData::setValue);
    }

    public LiveData<Resource<List<TodoItem>>> getLiveData(){
        return liveData;
    }

    public void refreshPlans(){
        loadTodos();
    }

    public LiveData<Resource<List<TodoItem>>> getTodoLiveData(){
        return liveData;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        repo.cleanUp();
    }
}
