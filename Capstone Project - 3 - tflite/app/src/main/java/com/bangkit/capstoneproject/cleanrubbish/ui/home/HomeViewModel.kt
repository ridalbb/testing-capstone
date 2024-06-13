package com.bangkit.capstoneproject.cleanrubbish.ui.home

import android.app.Application
import android.content.res.TypedArray
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bangkit.capstoneproject.cleanrubbish.Article
import com.bangkit.capstoneproject.cleanrubbish.R

class HomeViewModel(private val dataTitle: Array<String>, private val dataDescription: Array<String>, private val dataPhoto: TypedArray) : ViewModel() {
    private val _articles = MutableLiveData<List<Article>>()
    val articles: LiveData<List<Article>> get() = _articles

    init {
        // Inisialisasi data artikel
        val listArticle = ArrayList<Article>()
        for (i in dataTitle.indices) {
            val article = Article(dataTitle[i], dataDescription[i], dataPhoto.getResourceId(i, -1))
            listArticle.add(article)
        }
        dataPhoto.recycle()

        // Set data artikel ke LiveData
        _articles.value = listArticle
    }
}