package edu.skillbox.skillcinema.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ImagePagerViewModelFactory (private val imagePagerViewModel: ImagePagerViewModel) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ImagePagerViewModel::class.java)) {
            return imagePagerViewModel as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}