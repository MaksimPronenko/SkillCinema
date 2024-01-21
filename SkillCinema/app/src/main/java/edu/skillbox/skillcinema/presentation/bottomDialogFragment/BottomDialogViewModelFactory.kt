package edu.skillbox.skillcinema.presentation.bottomDialogFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class BottomDialogViewModelFactory (private val bottomDialogViewModel: BottomDialogViewModel) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BottomDialogViewModel::class.java)) {
            return bottomDialogViewModel as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}