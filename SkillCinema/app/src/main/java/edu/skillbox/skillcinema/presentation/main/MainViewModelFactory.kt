package edu.skillbox.skillcinema.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class MainViewModelFactory (private val mainViewModel: MainViewModel): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return mainViewModel as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}