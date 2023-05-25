package edu.skillbox.skillcinema.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class StaffViewModelFactory (private val staffViewModel: StaffViewModel) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StaffViewModel::class.java)) {
            return staffViewModel as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}