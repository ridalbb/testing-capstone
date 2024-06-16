package com.bangkit.capstoneproject.cleanrubbish.ui.detail

import android.graphics.text.LineBreaker
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Layout
import androidx.annotation.RequiresApi
import com.bangkit.capstoneproject.cleanrubbish.Article
import com.bangkit.capstoneproject.cleanrubbish.R
import com.bangkit.capstoneproject.cleanrubbish.databinding.ActivityDetailArticleBinding
import com.bangkit.capstoneproject.cleanrubbish.ui.home.HomeFragment.Companion.KEY_DETAIL

class DetailArticleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailArticleBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val detail = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra(KEY_DETAIL, Article::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(KEY_DETAIL)
        }

        @RequiresApi(Build.VERSION_CODES.Q)
        if (Build.VERSION.SDK_INT >= 26) {
            binding.tvDetailArticleDescription.justificationMode = LineBreaker.JUSTIFICATION_MODE_INTER_WORD
        }


        if (detail != null){
            with(binding){
                ivDetailArticle.setImageResource(detail.photo)
                tvDetailArticleTitle.text = detail.title
                tvDetailArticleDescription.text = detail.description
            }
        }
    }
}