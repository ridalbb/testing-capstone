package com.bangkit.capstoneproject.cleanrubbish.data.local.repo

import com.bangkit.capstoneproject.cleanrubbish.data.pref.UserModel
import com.bangkit.capstoneproject.cleanrubbish.data.pref.UserPreference
import kotlinx.coroutines.flow.Flow

class UserRepository private constructor(
    private val userPreference: UserPreference,
) {

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }



    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            userPreference: UserPreference,
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference)
            }.also { instance = it }
    }
}