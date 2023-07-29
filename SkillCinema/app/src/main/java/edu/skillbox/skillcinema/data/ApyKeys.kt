package edu.skillbox.skillcinema.data

import android.util.Log

private const val TAG = "ApyKeysObject"

const val API_KEY_1 = "ce6f81de-e746-4a8b-8a79-4a7fe451b75d"
const val API_KEY_2 = "a8429c6b-2971-443a-9a72-59932af2324f"
const val API_KEY_3 = "ca8d9204-5d12-4fcd-bdfc-194f72a55394"
const val API_KEY_4 = "3a8ba0d1-8a76-4d8b-90e6-b3d00ed195c5"

object ApyKeys {
    // Набор ключей API. Каждый на 500 запросов.
    private val apiKeysList = listOf(API_KEY_1, API_KEY_2, API_KEY_3, API_KEY_4)

    // Номер текущего ключа.
    private var currentApiKeyNumber = 0

    // Активный ключ.
    var currentApiKey = apiKeysList[0]

    // Переключение ключей API.
    fun changeApiKey() {
        currentApiKeyNumber = if (currentApiKeyNumber == 3) 0 else currentApiKeyNumber + 1
        currentApiKey = apiKeysList[currentApiKeyNumber]
        Log.d(TAG, "changeApiKey(): currentApiKey = $currentApiKey")
    }
}