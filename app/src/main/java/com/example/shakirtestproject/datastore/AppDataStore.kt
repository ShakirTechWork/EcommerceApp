package com.example.shakirtestproject.datastore

import android.content.Context
import androidx.datastore.preferences.clear
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.preferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AppDataStore(context: Context) {

    // Create the dataStore and give it a name same as shared preferences
    private val dataStore = context.createDataStore(name = "user_prefs")

    // Create some keys we will use them to store and retrieve the data
    companion object {
        val USER_NAME_KEY = preferencesKey<String>("USER_NAME")
        val USER_PASSWORD_KEY = preferencesKey<String>("USER_PASSWORD")
        val USER_TOKEN_KEY = preferencesKey<String>("USER_TOKEN")
    }

    // Store user data
    // refer to the data store and using edit
    // we can store values using the keys
    suspend fun storeUser(name: String, password: String, token: String) {
        dataStore.edit {
            // here it refers to the preferences we are editing
            it[USER_NAME_KEY] = name
            it[USER_PASSWORD_KEY] = password
            it[USER_TOKEN_KEY] = token
        }
    }

    suspend fun logoutUser() {
        dataStore.edit {
            // Clear the user's data from the data store
            it.clear()
        }
    }

    // Create a token flow to retrieve token from the preferences
    val userTokenFlow: Flow<String> = dataStore.data.map {
        it[USER_TOKEN_KEY] ?: ""
    }


}