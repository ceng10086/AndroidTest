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
import com.example.androidtest.util.TaskValidator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

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
            OutputStream os = null;
            try {
                os = resolver.openOutputStream(uri);
                if (os == null) {
                    throw new IllegalStateException("openOutputStream returned null");
                }

                try (OutputStreamWriter writer = new OutputStreamWriter(os, StandardCharsets.UTF_8)) {
                    List<TaskEntity> tasks = taskDao.getAllOnce();
                    ExportPayload payload = ExportPayload.from(tasks);
                    gson.toJson(payload, writer);
                    writer.flush();
                }
                mainHandler.post(callback::onSuccess);
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError(e));
            } finally {
                if (os != null) {
                    try {
                        os.close();
                    } catch (Exception ignored) {
                    }
                }
            }
        });
    }

    public void importFromJsonOverwrite(@NonNull ContentResolver resolver, @NonNull Uri uri, @NonNull ResultCallback callback) {
        writeExecutor.execute(() -> {
            InputStream is = null;
            try {
                is = resolver.openInputStream(uri);
                if (is == null) {
                    throw new IllegalStateException("openInputStream returned null");
                }

                ExportPayload payload;
                try (InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                    payload = gson.fromJson(reader, ExportPayload.class);
                }
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

                    String normalizedTitle = TaskValidator.normalizeTitle(t.title);
                    String normalizedNote = TaskValidator.normalizeNote(t.note);
                    if (!TaskValidator.isValidTitle(normalizedTitle) || !TaskValidator.isValidNote(normalizedNote)) {
                        continue;
                    }

                    TaskEntity e = new TaskEntity();
                    e.id = t.id;
                    e.title = normalizedTitle;
                    e.note = normalizedNote;
                    e.isDone = t.isDone;
                    e.createdAt = t.createdAt;
                    e.updatedAt = t.updatedAt;
                    e.priority = TaskValidator.clampPriority(t.priority);
                    entities.add(e);
                }

                if (!payload.tasks.isEmpty() && entities.isEmpty()) {
                    throw new IllegalArgumentException("No valid tasks");
                }

                database.runInTransaction(() -> {
                    taskDao.deleteAll();
                    taskDao.insertAll(entities);
                });

                mainHandler.post(callback::onSuccess);
            } catch (JsonParseException e) {
                mainHandler.post(() -> callback.onError(e));
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError(e));
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (Exception ignored) {
                    }
                }
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
                    t.priority = e.priority;
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
        int priority;
    }
}
