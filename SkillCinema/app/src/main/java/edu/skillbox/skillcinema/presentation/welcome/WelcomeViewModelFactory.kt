package edu.skillbox.skillcinema.presentation.welcome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class WelcomeViewModelFactory (private val welcomeViewModel: WelcomeViewModel): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WelcomeViewModel::class.java)) {
            return welcomeViewModel as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}