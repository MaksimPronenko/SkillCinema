package edu.skillbox.skillcinema.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CollectionNameDialogViewModel : ViewModel() {

    private val _state = MutableStateFlow<CollectionNameViewModelState>(
        CollectionNameViewModelState.DataInput
    )
    val state = _state.asStateFlow()

    var newCollectionName = ""

//    private val _readyButtonIsEnabled = Channel<Boolean>()
//    val readyButtonIsEnabled = _readyButtonIsEnabled.receiveAsFlow()

    fun determineCorrectName(enteredText: String) {
        viewModelScope.launch {
            if (enteredText.isNotEmpty()) {
//                _readyButtonIsEnabled.send(true)
                _state.value = CollectionNameViewModelState.DataIsValid
            } else {
//                _readyButtonIsEnabled.send(false)
                _state.value = CollectionNameViewModelState.DataInput
            }
        }
    }
}