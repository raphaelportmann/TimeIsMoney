package ch.hslu.mobpro.timeismoney

import android.app.Application
import androidx.lifecycle.ViewModelProvider

class MainViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    @SuppressWarnings("unchecked")
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(application) as T
    }

}