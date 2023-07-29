package edu.skillbox.skillcinema.presentation.listPageGallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ListPageGalleryViewModelFactory (private val listPageGalleryViewModel: ListPageGalleryViewModel) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListPageGalleryViewModel::class.java)) {
            return listPageGalleryViewModel as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}