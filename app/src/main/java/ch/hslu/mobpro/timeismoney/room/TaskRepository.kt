package ch.hslu.mobpro.timeismoney.room

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskRepository(private val taskDao: TaskDao) {
    val allTasks: LiveData<List<Task>> = taskDao.getTasks()
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    fun insertTask(task: Task) {
        coroutineScope.launch(Dispatchers.IO) {
            taskDao.insertTask(task)
        }
    }

    fun deleteTask(task: Task) {
        coroutineScope.launch(Dispatchers.IO) {
            taskDao.deleteTask(task.id)
        }
    }
}