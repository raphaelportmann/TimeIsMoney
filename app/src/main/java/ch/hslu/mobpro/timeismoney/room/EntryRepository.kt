package ch.hslu.mobpro.timeismoney.room

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EntryRepository(private val entryDao: EntryDao) {
    val allEntries: LiveData<List<TaskEntry>> = entryDao.getEntries()
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    fun insertEntry(entry: Entry) {
        coroutineScope.launch(Dispatchers.IO) {
            entryDao.insertEntry(entry)
        }
    }

    fun deleteEntry(entryId: Long) {
        coroutineScope.launch(Dispatchers.IO) {
            entryDao.deleteEntry(entryId)
        }
    }
}