package ch.hslu.mobpro.timeismoney

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ch.hslu.mobpro.timeismoney.room.*

class MainViewModel(application: Application, private var userId: String) : ViewModel() {
    var allTasks: LiveData<List<Task>>
    private val taskRepository: TaskRepository

    var allEntries: LiveData<List<TaskEntry>>
    private val entryRepository: EntryRepository

    fun addTask(title: String) {
        taskRepository.insertTask(Task(title = title), userId)
    }

    fun deleteAllTasks() {
        taskRepository.deleteAllTasks(userId)
    }

    fun addEntry(startTime: Long, endTime: Long, taskId: Long) {
        entryRepository.insertEntry(Entry(startTime = startTime, endTime = endTime, taskId = taskId), userId)
    }

    fun updateEntry(entry: Entry) {
        entryRepository.updateEntry(entry, userId)
    }

    fun getEntriesByTask(taskId: Long): LiveData<List<TaskEntry>> {
        return entryRepository.getEntriesByTask(userId, taskId)
    }

    fun deleteEntry(entryId: Long) {
        entryRepository.deleteEntry(entryId, userId)
    }

    fun deleteAllEntries() {
        entryRepository.deleteAllEntries(userId)
    }

    fun setUserId(uId: String) {
        userId = uId

        allTasks = taskRepository.getAllTasks(uId)
        allEntries = entryRepository.getAllEntries(uId)
    }

    init {
        val applicationDatabase = AppDatabase.getAppDatabase(application)
        val taskDao = applicationDatabase.taskDao()
        taskRepository = TaskRepository(taskDao)
        val entryDao = applicationDatabase.entryDao()
        entryRepository = EntryRepository(entryDao)

        allTasks = taskRepository.getAllTasks(userId)
        allEntries = entryRepository.getAllEntries(userId)
    }
}