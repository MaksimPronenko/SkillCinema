package edu.skillbox.skillcinema.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

//class ListPageFiltered1ViewModelFactory(val application: App) : ViewModelProvider.Factory {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        val repository = Repository()
//        return ListPageFiltered1ViewModel(repository, application) as T
//    }
//}

class ListPageFiltered1ViewModelFactory (private val listPageFiltered1ViewModel: ListPageFiltered1ViewModel) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListPageFiltered1ViewModel::class.java)) {
            return listPageFiltered1ViewModel as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}