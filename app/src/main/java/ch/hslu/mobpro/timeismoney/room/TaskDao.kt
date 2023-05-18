package ch.hslu.mobpro.timeismoney.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TaskDao {
    @Insert
    fun insertTask(task: Task)

    @Query("SELECT * FROM tasks")
    fun getTasks(): Array<Task>

    @Query("SELECT * FROM tasks WHERE id = :id")
    fun getTask(id: Long): Task
}