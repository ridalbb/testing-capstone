package com.bangkit.capstoneproject.cleanrubbish.data.local.repo

import android.app.Application
import androidx.lifecycle.LiveData
import com.bangkit.capstoneproject.cleanrubbish.data.local.db.HistoryResult
import com.bangkit.capstoneproject.cleanrubbish.data.local.db.HistoryResultDao
import com.bangkit.capstoneproject.cleanrubbish.data.local.db.HistoryResultRoomDatabase
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class HistoryRepository (application: Application){

    private val mHistoryResultDao: HistoryResultDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val db= HistoryResultRoomDatabase.getDatabase(application)
        mHistoryResultDao = db.HistoryResultDao()
    }

    fun insert(historyResult: HistoryResult){
        executorService.execute {
            mHistoryResultDao.insert(historyResult)
        }
    }
    fun getAllHistoryResult(): LiveData<List<HistoryResult>> = mHistoryResultDao.getAllHistoryResult()

}