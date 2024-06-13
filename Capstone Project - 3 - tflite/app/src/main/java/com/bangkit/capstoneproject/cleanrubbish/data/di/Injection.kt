package com.bangkit.capstoneproject.cleanrubbish.data.di

//import android.content.Context
//import com.bangkit.capstoneproject.cleanrubbish.data.pref.UserPreference
//import com.bangkit.capstoneproject.cleanrubbish.data.pref.dataStore
//import com.bangkit.capstoneproject.cleanrubbish.data.repo.Repository
//import kotlinx.coroutines.flow.first
//import kotlinx.coroutines.runBlocking

//object Injection {
//
//    fun provideRepository(context: Context): Repository {
//
//        val pref = UserPreference.getInstance(context.dataStore)
//
//        val user = runBlocking {
//            pref.getSession().first()
//        }
//
//        return Repository.getInstance(pref)
//    }
//}