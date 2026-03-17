package com.example.androidtest.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.androidtest.data.db.TaskEntity;
import com.example.androidtest.data.repo.TaskRepository;

public class EditTaskViewModel extends AndroidViewModel {

    private final TaskRepository repository;

    public EditTaskViewModel(@NonNull Application application) {
        super(application);
        repository = new TaskRepository(application);
    }

    public LiveData<TaskEntity> observeById(long id) {
        return repository.observeById(id);
    }

    public void insert(@NonNull String title, @NonNull String note) {
        long now = System.currentTimeMillis();
        TaskEntity entity = new TaskEntity(title, note, false, now, now, 0);
        repository.insert(entity);
    }

    public void update(@NonNull TaskEntity entity, @NonNull String title, @NonNull String note, boolean isDone) {
        entity.title = title;
        entity.note = note;
        entity.isDone = isDone;
        entity.updatedAt = System.currentTimeMillis();
        repository.update(entity);
    }
}
