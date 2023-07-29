package edu.skillbox.skillcinema.presentation.serial

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SerialViewModelFactory (private val serialViewModel: SerialViewModel) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SerialViewModel::class.java)) {
            return serialViewModel as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}