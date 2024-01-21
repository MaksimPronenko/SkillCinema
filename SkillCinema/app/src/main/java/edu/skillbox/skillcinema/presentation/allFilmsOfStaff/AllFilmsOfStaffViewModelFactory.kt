package edu.skillbox.skillcinema.presentation.allFilmsOfStaff

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class AllFilmsOfStaffViewModelFactory (private val allFilmsOfStaffViewModel: AllFilmsOfStaffViewModel) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AllFilmsOfStaffViewModel::class.java)) {
            return allFilmsOfStaffViewModel as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}