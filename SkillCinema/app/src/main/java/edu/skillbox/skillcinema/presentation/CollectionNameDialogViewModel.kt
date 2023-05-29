package edu.skillbox.skillcinema.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class CollectionNameDialogViewModel : ViewModel() {

    private val _state = MutableStateFlow<CollectionNameViewModelState>(
        CollectionNameViewModelState.DataIsEmpty
    )
    val state = _state.asStateFlow()

//    private val _correctedInput = Channel<String>()
//    val correctedInput = _correctedInput.receiveAsFlow()

    var newCollectionName = ""

//    private val _readyButtonIsEnabled = Channel<Boolean>()
//    val readyButtonIsEnabled = _readyButtonIsEnabled.receiveAsFlow()

    fun determineCorrectName(enteredText: String) {
        viewModelScope.launch {
            if (enteredText.isEmpty()) {
//                _readyButtonIsEnabled.send(true)
                _state.value = CollectionNameViewModelState.DataIsEmpty
            } else if (enteredText.length <= 24) {
//                _readyButtonIsEnabled.send(false)
                _state.value = CollectionNameViewModelState.DataIsValid
            } else {
                _state.value = CollectionNameViewModelState.DataIsNotValid
//                if (enteredText.length > 40) {
//                    _correctedInput.send(
//                        enteredText.substring(
//                            startIndex = 0,
//                            endIndex = 40
//                        )
//                    )
//                }
            }
        }
    }

//    fun determineSearchAbility(searchedText: String) {
//        viewModelScope.launch {
//            // Эта функция вызывается только из состояния State.Search, поэтому его не задаю снова,
//            // чтобы не задавать (и не изменять) showLastResult и searchText.
//            _searchButtonIsEnabled.send(searchedText.length >= 3 && state.value != State.Loading)
//        }
//    }
}