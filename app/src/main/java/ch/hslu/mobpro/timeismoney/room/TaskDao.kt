package ch.hslu.mobpro.timeismoney.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertTask(task: Task)

    @Query("SELECT * FROM tasks WHERE userId = :userId")
    fun getTasks(userId: String): LiveData<List<Task>>

    @Query("SELECT * FROM tasks WHERE id = :id AND userId = :userId")
    fun getTask(id: Long, userId: String): LiveData<Task>

    @Query("DELETE FROM tasks WHERE id = :id AND userId = :userId")
    fun deleteTask(id: Long, userId: String)

    @Query("DELETE FROM tasks WHERE userId = :userId")
    fun deleteAllTasks(userId: String)
}