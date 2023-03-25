package edu.skillbox.skillcinema.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

//class ListPageTop250ViewModelFactory(val application: App) : ViewModelProvider.Factory {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        val repository = Repository()
//        return ListPageTop250ViewModel(repository, application) as T
//    }
//}

class ListPageTop250ViewModelFactory (private val listPageTop250ViewModel: ListPageTop250ViewModel) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListPageTop250ViewModel::class.java)) {
            return listPageTop250ViewModel as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}