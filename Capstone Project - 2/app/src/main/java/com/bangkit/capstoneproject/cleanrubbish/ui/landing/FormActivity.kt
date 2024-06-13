package com.bangkit.capstoneproject.cleanrubbish.ui.landing

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import com.bangkit.capstoneproject.cleanrubbish.MainActivity
import com.bangkit.capstoneproject.cleanrubbish.R
import com.bangkit.capstoneproject.cleanrubbish.databinding.ActivityFormBinding
import com.bangkit.capstoneproject.cleanrubbish.databinding.ActivityLandingSecondBinding

class FormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFormBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        binding.btnNext.setOnClickListener { moveToMainActivity() }
    }
    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun moveToMainActivity(){
        startActivity(Intent(this, MainActivity::class.java))
    }
}