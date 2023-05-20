package ch.hslu.mobpro.timeismoney.room

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface EntryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertEntry(entry: Entry)

    @Query("SELECT entries.*, tasks.title FROM entries LEFT JOIN tasks ON tasks.id = entries.taskId")
    fun getEntries(): LiveData<List<TaskEntry>>

    @Query("SELECT * FROM entries WHERE id = :id")
    fun getEntry(id: Long): LiveData<Entry>

    @Query("DELETE FROM entries WHERE id = :id")
    fun deleteEntry(id: Long)
}