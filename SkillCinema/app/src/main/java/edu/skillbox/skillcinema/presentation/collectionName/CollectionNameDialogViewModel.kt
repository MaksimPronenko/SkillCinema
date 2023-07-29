package edu.skillbox.skillcinema.presentation.collectionName

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CollectionNameDialogViewModel : ViewModel() {

    private val _state = MutableStateFlow<CollectionNameViewModelState>(
        CollectionNameViewModelState.DataIsEmpty
    )
    val state = _state.asStateFlow()

    var newCollectionName = ""

    fun determineCorrectName(enteredText: String) {
        viewModelScope.launch {
            if (enteredText.isEmpty()) {
                _state.value = CollectionNameViewModelState.DataIsEmpty
            } else if (enteredText.length <= 24) {
                _state.value = CollectionNameViewModelState.DataIsValid
            } else {
                _state.value = CollectionNameViewModelState.DataIsNotValid
            }
        }
    }
}