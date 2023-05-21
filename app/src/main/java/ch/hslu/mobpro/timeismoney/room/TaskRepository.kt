package ch.hslu.mobpro.timeismoney.room

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskRepository(private val taskDao: TaskDao) {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    fun getAllTasks(userId: String): LiveData<List<Task>> {
        return taskDao.getTasks(userId);
    }

    fun insertTask(task: Task, userId: String) {
        task.userId = userId
        coroutineScope.launch(Dispatchers.IO) {
            taskDao.insertTask(task)
        }
    }

    fun deleteTask(task: Task, userId: String) {
        coroutineScope.launch(Dispatchers.IO) {
            taskDao.deleteTask(task.id, userId)
        }
    }

    fun deleteAllTasks(userId: String) {
        coroutineScope.launch(Dispatchers.IO) {
            taskDao.deleteAllTasks(userId)
        }
    }
}