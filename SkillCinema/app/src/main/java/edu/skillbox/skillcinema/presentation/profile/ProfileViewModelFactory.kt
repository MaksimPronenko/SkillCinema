package edu.skillbox.skillcinema.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class ProfileViewModelFactory (private val profileViewModel: ProfileViewModel) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return profileViewModel as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}