package edu.skillbox.skillcinema.presentation.serialContent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class SerialContentViewModelFactory (private val serialContentViewModel: SerialContentViewModel) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SerialContentViewModel::class.java)) {
            return serialContentViewModel as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}