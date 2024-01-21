package edu.skillbox.skillcinema.presentation.listPagePremieres

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class ListPagePremieresViewModelFactory (private val listPagePremieresViewModel: ListPagePremieresViewModel) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListPagePremieresViewModel::class.java)) {
            return listPagePremieresViewModel as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}