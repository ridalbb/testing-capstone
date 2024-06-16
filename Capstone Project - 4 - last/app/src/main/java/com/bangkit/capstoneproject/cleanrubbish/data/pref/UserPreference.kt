package com.bangkit.capstoneproject.cleanrubbish.data.pref

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import androidx.datastore.preferences.core.Preferences



class UserPreference private constructor(private val dataStore: DataStore<Preferences>){

    suspend fun saveSession(user: UserModel){
        dataStore.edit{ preferences ->
            preferences[NAME_KEY] = user.name
        }
    }

    fun getSession(): Flow<UserModel>{
        return dataStore.data.map { preferences ->
            UserModel(
                preferences[NAME_KEY] ?: ""
            )
        }
    }
    companion object {
        @Volatile
        private var INSTANCE: UserPreference? = null

        private val NAME_KEY = stringPreferencesKey("name")

        fun getInstance(dataStore: DataStore<Preferences>): UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}