package com.example.androidtest.ui.viewmodel;

import android.app.Application;
import android.content.ContentResolver;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.androidtest.data.db.TaskEntity;
import com.example.androidtest.data.repo.TaskRepository;

import java.util.List;

public class TaskListViewModel extends AndroidViewModel {

    private final TaskRepository repository;
    private final LiveData<List<TaskEntity>> tasks;

    public TaskListViewModel(@NonNull Application application) {
        super(application);
        repository = new TaskRepository(application);
        tasks = repository.observeAll();
    }

    public LiveData<List<TaskEntity>> getTasks() {
        return tasks;
    }

    public void toggleDone(@NonNull TaskEntity entity, boolean isDone) {
        entity.isDone = isDone;
        entity.updatedAt = System.currentTimeMillis();
        repository.update(entity);
    }

    public void delete(@NonNull TaskEntity entity) {
        repository.delete(entity);
    }

    public void insert(@NonNull TaskEntity entity) {
        repository.insert(entity);
    }

    public void exportToJson(@NonNull ContentResolver resolver, @NonNull Uri uri, @NonNull TaskRepository.ResultCallback callback) {
        repository.exportToJson(resolver, uri, callback);
    }

    public void importFromJsonOverwrite(@NonNull ContentResolver resolver, @NonNull Uri uri, @NonNull TaskRepository.ResultCallback callback) {
        repository.importFromJsonOverwrite(resolver, uri, callback);
    }
}
