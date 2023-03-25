package edu.skillbox.skillcinema.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

//class ListPagePremieresViewModelFactory(val application: App) : ViewModelProvider.Factory {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        val repository = Repository()
//        return ListPagePremieresViewModel(repository, application) as T
//    }
//}

class ListPagePremieresViewModelFactory (private val listPagePremieresViewModel: ListPagePremieresViewModel) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListPagePremieresViewModel::class.java)) {
            return listPagePremieresViewModel as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}