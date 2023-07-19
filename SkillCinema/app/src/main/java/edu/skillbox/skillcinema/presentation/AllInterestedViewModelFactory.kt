package edu.skillbox.skillcinema.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AllInterestedViewModelFactory (private val allInterestedViewModel: AllInterestedViewModel) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AllInterestedViewModel::class.java)) {
            return allInterestedViewModel as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}