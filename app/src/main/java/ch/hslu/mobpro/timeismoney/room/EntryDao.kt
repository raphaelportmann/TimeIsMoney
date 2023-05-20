package ch.hslu.mobpro.timeismoney.room

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface EntryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertEntry(entry: Entry)

    @Query("SELECT entries.*, tasks.title FROM entries LEFT JOIN tasks ON tasks.id = entries.taskId WHERE entries.userId = :userId")
    fun getEntries(userId: String): LiveData<List<TaskEntry>>

    @Query("SELECT entries.*, tasks.title FROM entries LEFT JOIN tasks ON tasks.id = entries.taskId WHERE entries.userId = :userId AND entries.taskId = :taskId")
    fun getEntriesByTask(userId: String, taskId: Long): LiveData<List<TaskEntry>>

    @Query("SELECT * FROM entries WHERE id = :id AND userId = :userId")
    fun getEntry(id: Long, userId: String): LiveData<Entry>

    @Query("DELETE FROM entries WHERE id = :id AND userId = :userId")
    fun deleteEntry(id: Long, userId: String)
}