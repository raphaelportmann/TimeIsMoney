package ch.hslu.mobpro.timeismoney.room

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EntryRepository(private val entryDao: EntryDao) {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    fun getAllEntries(userId: String): LiveData<List<TaskEntry>> {
        return entryDao.getEntries(userId)
    }
    fun getEntriesByTask(userId: String, taskId: Long): LiveData<List<TaskEntry>> {
        return entryDao.getEntriesByTask(userId, taskId)
    }

    fun insertEntry(entry: Entry, userId: String) {
        entry.userId = userId
        coroutineScope.launch(Dispatchers.IO) {
            entryDao.insertEntry(entry)
        }
    }

    fun updateEntry(entry: Entry, userId: String) {
        entry.userId = userId
        coroutineScope.launch(Dispatchers.IO) {
            entryDao.updateEntry(entry)
        }
    }

    fun deleteEntry(entryId: Long, userId: String) {
        coroutineScope.launch(Dispatchers.IO) {
            entryDao.deleteEntry(entryId, userId)
        }
    }

    fun deleteAllEntries(userId: String) {
        coroutineScope.launch(Dispatchers.IO) {
            entryDao.deleteAllEntries(userId)
        }
    }
}