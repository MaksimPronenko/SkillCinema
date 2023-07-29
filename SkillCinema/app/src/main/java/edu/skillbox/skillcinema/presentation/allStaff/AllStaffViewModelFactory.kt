package edu.skillbox.skillcinema.presentation.allStaff

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AllStaffViewModelFactory (private val allStaffViewModel: AllStaffViewModel) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AllStaffViewModel::class.java)) {
            return allStaffViewModel as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}