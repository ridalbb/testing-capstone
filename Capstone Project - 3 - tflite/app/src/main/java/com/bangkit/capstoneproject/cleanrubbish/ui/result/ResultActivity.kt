package com.bangkit.capstoneproject.cleanrubbish.ui.result

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bangkit.capstoneproject.cleanrubbish.R
import com.bangkit.capstoneproject.cleanrubbish.data.local.db.HistoryResult
import com.bangkit.capstoneproject.cleanrubbish.data.local.repo.HistoryRepository
import com.bangkit.capstoneproject.cleanrubbish.databinding.ActivityResultBinding
import com.bangkit.capstoneproject.cleanrubbish.helper.ImageClassifierHelper
import com.bangkit.capstoneproject.cleanrubbish.helper.ViewModelFactory
import org.tensorflow.lite.task.vision.classifier.Classifications

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding
    private lateinit var imageClassifierHelper: ImageClassifierHelper
    private lateinit var resultViewModel: ResultViewModel
    private lateinit var historyRepository: HistoryRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        historyRepository = HistoryRepository(application)
        resultViewModel = obtainViewModel(this@ResultActivity)

        setTitle()
        val imageUri = Uri.parse(intent.getStringExtra(EXTRA_IMAGE_URI))
        imageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.ivResultScan.setImageURI(it)
        }
        startClassify(imageUri)

    }

    private fun startClassify(imageUri: Uri) {
        imageClassifierHelper = ImageClassifierHelper(
            context = this,
            classifierListener = object : ImageClassifierHelper.ClassifierListener {
                override fun onError(error: String) {
                    runOnUiThread {
                        Toast.makeText(this@ResultActivity, error, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
                    runOnUiThread {
                        results?.let { it ->
                            if (it.isNotEmpty() && it[0].categories.isNotEmpty()) {
                                println(it)
                                val sortedCategories = it[0].categories.sortedByDescending { it?.score }
                                val label = sortedCategories[0].label
                                val imageUri = Uri.parse(intent.getStringExtra(EXTRA_IMAGE_URI)).toString()
                                val historyResult = HistoryResult(image = imageUri, label = label)
                                resultViewModel.insertBookmarkResult(historyResult)

                                // Display only the top label
                                binding.tvResult.text = label
                            } else {
                                binding.tvResult.text = ""
                            }
                        }
                    }
                }
            }
        )
        imageClassifierHelper.classifyStaticImage(imageUri)
    }


    private fun setTitle(){
        supportActionBar?.title = resources.getString(R.string.title_activity_result)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun obtainViewModel(activity: AppCompatActivity): ResultViewModel {
        historyRepository = HistoryRepository(application)
        val factory = ViewModelFactory.getInstance(historyRepository)
        return ViewModelProvider(activity, factory)[ResultViewModel::class.java]
    }

    companion object {
        const val EXTRA_IMAGE_URI = "extra_image_uri"
    }
}