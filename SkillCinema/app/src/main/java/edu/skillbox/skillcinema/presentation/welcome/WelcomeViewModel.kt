package edu.skillbox.skillcinema.presentation.welcome

import androidx.lifecycle.AndroidViewModel
import edu.skillbox.skillcinema.App
import kotlinx.coroutines.*

class WelcomeViewModel(
    application: App
) : AndroidViewModel(application) {

    var timeToChangeWelcomeScreen = 2000L

    val changeScreenScope = CoroutineScope(Dispatchers.IO)

    suspend fun countdownToChangeScreen() {
        delay(timeToChangeWelcomeScreen)
    }

    override fun onCleared() {
        super.onCleared()
        changeScreenScope.cancel()
    }
}