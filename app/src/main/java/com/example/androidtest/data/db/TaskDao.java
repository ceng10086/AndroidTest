package com.example.androidtest.data.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TaskDao {

    @Query("SELECT * FROM tasks ORDER BY updated_at DESC")
    LiveData<List<TaskEntity>> observeAll();

    @Query("SELECT * FROM tasks ORDER BY updated_at DESC")
    List<TaskEntity> getAllOnce();

    @Query("SELECT * FROM tasks WHERE id = :id LIMIT 1")
    LiveData<TaskEntity> observeById(long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(TaskEntity entity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<TaskEntity> entities);

    @Update
    int update(TaskEntity entity);

    @Delete
    int delete(TaskEntity entity);

    @Query("DELETE FROM tasks WHERE id = :id")
    int deleteById(long id);

    @Query("DELETE FROM tasks")
    int deleteAll();
}
