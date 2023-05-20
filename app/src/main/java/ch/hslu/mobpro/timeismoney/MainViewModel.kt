package ch.hslu.mobpro.timeismoney

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ch.hslu.mobpro.timeismoney.room.*

class MainViewModel(application: Application) : ViewModel() {
    val allTasks: LiveData<List<Task>>
    private val taskRepository: TaskRepository

    val allEntries: LiveData<List<TaskEntry>>
    private val entryRepository: EntryRepository

    fun addTask(title: String) {
        taskRepository.insertTask(Task(title = title))
    }

    fun addEntry(startTime: Long, endTime: Long, taskId: Long) {
        entryRepository.insertEntry(Entry(startTime = startTime, endTime = endTime, taskId = taskId))
    }

    fun deleteEntry(entryId: Long) {
        entryRepository.deleteEntry(entryId)
    }

    init {
        val applicationDatabase = AppDatabase.getAppDatabase(application)
        val taskDao = applicationDatabase.taskDao()
        taskRepository = TaskRepository(taskDao)
        val entryDao = applicationDatabase.entryDao()
        entryRepository = EntryRepository(entryDao)

        allTasks = taskRepository.allTasks
        allEntries = entryRepository.allEntries
    }
}