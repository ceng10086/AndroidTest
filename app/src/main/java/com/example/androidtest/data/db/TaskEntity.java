package com.example.androidtest.data.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "tasks",
    indices = {
        @Index(value = {"updated_at"})
    }
)
public class TaskEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public long id;

    @NonNull
    @ColumnInfo(name = "title")
    public String title;

    @NonNull
    @ColumnInfo(name = "note")
    public String note;

    @ColumnInfo(name = "is_done")
    public boolean isDone;

    @ColumnInfo(name = "created_at")
    public long createdAt;

    @ColumnInfo(name = "updated_at")
    public long updatedAt;

    @ColumnInfo(name = "priority", defaultValue = "0")
    public int priority;

    public TaskEntity() {
        this.title = "";
        this.note = "";
        this.isDone = false;
        this.createdAt = 0L;
        this.updatedAt = 0L;
        this.priority = 0;
    }

    @Ignore
    public TaskEntity(@NonNull String title,
                      @NonNull String note,
                      boolean isDone,
                      long createdAt,
                      long updatedAt,
                      int priority) {
        this.title = title;
        this.note = note;
        this.isDone = isDone;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.priority = priority;
    }
}
