package edu.skillbox.skillcinema.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject

//class WelcomeViewModelFactory(val application: App) : ViewModelProvider.Factory {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        return WelcomeViewModel(application) as T
//    }
//}

//class WelcomeViewModelFactory @Inject constructor(private val welcomeViewModel: WelcomeViewModel) :
//    ViewModelProvider.Factory {
//
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
//            return welcomeViewModel as T
//        }
//        throw IllegalArgumentException("Unknown class name")
//    }
//}

class WelcomeViewModelFactory (private val welcomeViewModel: WelcomeViewModel): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WelcomeViewModel::class.java)) {
            return welcomeViewModel as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}