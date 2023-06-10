package edu.skillbox.skillcinema.presentation

import androidx.lifecycle.AndroidViewModel
import edu.skillbox.skillcinema.App
import kotlinx.coroutines.*

private const val TAG = "Welcome"

class WelcomeViewModel(
    application: App
) : AndroidViewModel(application) {

//class WelcomeViewModel @Inject constructor(
//    application: App
//) : AndroidViewModel(application) {

//    class WelcomeViewModel @Inject constructor() : ViewModel() {

    var timeToChangeWelcomeScreen = 2000L

    val changeScreenScope = CoroutineScope(Dispatchers.IO)

    suspend fun countdownToChangeScreen() {
        delay(timeToChangeWelcomeScreen)
    }

//    fun countdownToChangeScreen() {
//        changeScreenScope.launch {
//            delay(timeToChangeWelcomeScreen)
//        }
//    }

    override fun onCleared() {
        super.onCleared()
        changeScreenScope.cancel()
    }
}