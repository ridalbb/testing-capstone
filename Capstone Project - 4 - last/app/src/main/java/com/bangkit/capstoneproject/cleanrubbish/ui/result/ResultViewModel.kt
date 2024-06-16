package com.bangkit.capstoneproject.cleanrubbish.ui.result

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.capstoneproject.cleanrubbish.data.local.db.HistoryResult
import com.bangkit.capstoneproject.cleanrubbish.data.local.repo.HistoryRepository
import kotlinx.coroutines.launch

class ResultViewModel(private val historyRepository: HistoryRepository): ViewModel(){

    fun insertBookmarkResult(historyResult: HistoryResult){
        viewModelScope.launch {
            historyRepository.insert(historyResult)
        }
    }

    companion object{
        const val KEY_LABEL = "key_label"
    }
}