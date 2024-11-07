package com.example.tasky.agenda.agenda_data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.tasky.agenda.agenda_data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :taskId LIMIT 1")
    suspend fun getTaskById(taskId: String): TaskEntity

    @Query("SELECT EXISTS(SELECT 1 FROM tasks WHERE id = :taskId)")
    suspend fun existsById(taskId: String): Boolean

    @Upsert
    suspend fun upsertTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)
}