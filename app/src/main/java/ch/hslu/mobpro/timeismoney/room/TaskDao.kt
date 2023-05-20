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

    @Query("SELECT * FROM tasks")
    fun getTasks(): LiveData<List<Task>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    fun getTask(id: Long): LiveData<Task>

    @Query("DELETE FROM tasks WHERE id = :id")
    fun deleteTask(id: Long)
}