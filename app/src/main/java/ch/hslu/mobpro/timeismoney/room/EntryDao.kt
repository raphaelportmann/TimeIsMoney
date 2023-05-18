package ch.hslu.mobpro.timeismoney.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface EntryDao {
    @Insert
    fun insertEntry(entry: Entry)

    @Query("SELECT * FROM entries")
    fun getEntries(): Array<Entry>

    @Query("SELECT * FROM entries WHERE id = :id")
    fun getEntry(id: Long): Entry
}