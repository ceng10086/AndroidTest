package com.example.androidtest.data.repo;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.example.androidtest.data.db.AppDatabase;
import com.example.androidtest.data.db.TaskDao;
import com.example.androidtest.data.db.TaskEntity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TaskRepository {

    public interface ResultCallback {
        void onSuccess();

        void onError(@NonNull Exception e);
    }

    private static final int EXPORT_VERSION = 1;

    private final AppDatabase database;
    private final TaskDao taskDao;
    private final Executor writeExecutor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public TaskRepository(@NonNull Context context) {
        this.database = AppDatabase.getInstance(context);
        this.taskDao = database.taskDao();
    }

    public LiveData<List<TaskEntity>> observeAll() {
        return taskDao.observeAll();
    }

    public LiveData<TaskEntity> observeById(long id) {
        return taskDao.observeById(id);
    }

    public void insert(@NonNull TaskEntity entity) {
        writeExecutor.execute(() -> taskDao.insert(entity));
    }

    public void update(@NonNull TaskEntity entity) {
        writeExecutor.execute(() -> taskDao.update(entity));
    }

    public void delete(@NonNull TaskEntity entity) {
        writeExecutor.execute(() -> taskDao.delete(entity));
    }

    public void deleteById(long id) {
        writeExecutor.execute(() -> taskDao.deleteById(id));
    }

    public void exportToJson(@NonNull ContentResolver resolver, @NonNull Uri uri, @NonNull ResultCallback callback) {
        writeExecutor.execute(() -> {
            try (OutputStream os = resolver.openOutputStream(uri);
                 OutputStreamWriter writer = new OutputStreamWriter(os, StandardCharsets.UTF_8)) {
                if (os == null) {
                    throw new IllegalStateException("openOutputStream returned null");
                }

                List<TaskEntity> tasks = taskDao.getAllOnce();
                ExportPayload payload = ExportPayload.from(tasks);
                gson.toJson(payload, writer);
                writer.flush();

                mainHandler.post(callback::onSuccess);
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError(e));
            }
        });
    }

    public void importFromJsonOverwrite(@NonNull ContentResolver resolver, @NonNull Uri uri, @NonNull ResultCallback callback) {
        writeExecutor.execute(() -> {
            try (InputStream is = resolver.openInputStream(uri);
                 InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                if (is == null) {
                    throw new IllegalStateException("openInputStream returned null");
                }

                ExportPayload payload = gson.fromJson(reader, ExportPayload.class);
                if (payload == null || payload.tasks == null) {
                    throw new IllegalArgumentException("Invalid JSON payload");
                }
                if (payload.version != EXPORT_VERSION) {
                    throw new IllegalArgumentException("Unsupported version: " + payload.version);
                }

                List<TaskEntity> entities = new ArrayList<>();
                for (ExportTask t : payload.tasks) {
                    if (t == null || t.title == null) {
                        continue;
                    }
                    TaskEntity e = new TaskEntity();
                    e.id = t.id;
                    e.title = t.title;
                    e.note = t.note == null ? "" : t.note;
                    e.isDone = t.isDone;
                    e.createdAt = t.createdAt;
                    e.updatedAt = t.updatedAt;
                    entities.add(e);
                }

                database.runInTransaction(() -> {
                    taskDao.deleteAll();
                    taskDao.insertAll(entities);
                });

                mainHandler.post(callback::onSuccess);
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError(e));
            }
        });
    }

    private static class ExportPayload {
        int version;
        long exportedAt;
        List<ExportTask> tasks;

        static ExportPayload from(List<TaskEntity> entities) {
            ExportPayload payload = new ExportPayload();
            payload.version = EXPORT_VERSION;
            payload.exportedAt = System.currentTimeMillis();
            payload.tasks = new ArrayList<>();
            if (entities != null) {
                for (TaskEntity e : entities) {
                    if (e == null) {
                        continue;
                    }
                    ExportTask t = new ExportTask();
                    t.id = e.id;
                    t.title = e.title;
                    t.note = e.note;
                    t.isDone = e.isDone;
                    t.createdAt = e.createdAt;
                    t.updatedAt = e.updatedAt;
                    payload.tasks.add(t);
                }
            }
            return payload;
        }
    }

    private static class ExportTask {
        long id;
        String title;
        String note;
        boolean isDone;
        long createdAt;
        long updatedAt;
    }
}
