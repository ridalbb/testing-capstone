package com.bangkit.capstoneproject.cleanrubbish.data.local.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface HistoryResultDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(historyResult: HistoryResult)

    @Delete
    fun delete(historyResult: HistoryResult)

    @Query("SELECT * from HistoryResult")
    fun getAllHistoryResult(): LiveData<List<HistoryResult>>

    @Query("SELECT * FROM HistoryResult WHERE image = :image")
    fun getBookmarkResultByImage(image: String): LiveData<HistoryResult>

}