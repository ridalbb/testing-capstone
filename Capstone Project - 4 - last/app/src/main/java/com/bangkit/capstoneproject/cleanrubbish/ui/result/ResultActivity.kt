package com.bangkit.capstoneproject.cleanrubbish.ui.result

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bangkit.capstoneproject.cleanrubbish.R
import com.bangkit.capstoneproject.cleanrubbish.data.local.db.HistoryResult
import com.bangkit.capstoneproject.cleanrubbish.data.local.repo.HistoryRepository
import com.bangkit.capstoneproject.cleanrubbish.data.remote.response.PredictResponse
import com.bangkit.capstoneproject.cleanrubbish.data.remote.retrofit.ApiConfig
import com.bangkit.capstoneproject.cleanrubbish.data.utils.reduceFileImage
import com.bangkit.capstoneproject.cleanrubbish.data.utils.uriToFile
import com.bangkit.capstoneproject.cleanrubbish.databinding.ActivityResultBinding
import com.bangkit.capstoneproject.cleanrubbish.helper.ImageClassifierHelper
import com.bangkit.capstoneproject.cleanrubbish.helper.ViewModelFactory
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.tensorflow.lite.task.vision.classifier.Classifications
import retrofit2.HttpException

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding
    private lateinit var imageClassifierHelper: ImageClassifierHelper
    private lateinit var resultViewModel: ResultViewModel
    private lateinit var historyRepository: HistoryRepository
    private var currentImageUri: Uri? = null

    @RequiresApi(Build.VERSION_CODES.Q)
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

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun startClassify(imageUri: Uri) {
        val imageFile = uriToFile(imageUri, this).reduceFileImage()
        Log.d("Image Classification File", "showImage: ${imageFile.path}")

        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "image",
            imageFile.name,
            requestImageFile
        )

        lifecycleScope.launch {
            try {
                val apiService = ApiConfig.getApiService()
                val successResponse = apiService.uploadImage(multipartBody)

                val label = successResponse.data?.result
                val historyResult = label?.let { HistoryResult(image = imageUri.toString(), label = it) }

                if (historyResult != null) {
                    resultViewModel.insertBookmarkResult(historyResult)
                }

                if (!label.isNullOrEmpty()) {
                    binding.tvResult.text = label
                } else {
                    binding.tvResult.text = "Terjadi Kesalahan"
                }

                when (label) {
                    "Organic" -> {
                        binding.ivTypeTrashCan.setImageResource(R.drawable.organic_trash_can)
                        Log.d("ImageClassifierHelper", "Set image to organic_trash_can")
                    }
                    else -> {
                        binding.ivTypeTrashCan.setImageResource(R.drawable.non_organic_trash_can)
                        Log.d("ImageClassifierHelper", "Set image to non_organic_trash_can")
                    }
                }
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, PredictResponse::class.java)
                showToast(errorResponse.message.toString())
                Log.e(TAG, "HTTP Exception: ${e.message()}")
            } catch (e: Exception) {
                showToast("An error occurred: ${e.message}")
                Log.e(TAG, "Exception: ${e.message}")
            }
        }
    }

    private fun setTitle() {
        supportActionBar?.title = resources.getString(R.string.title_activity_result)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun obtainViewModel(activity: AppCompatActivity): ResultViewModel {
        historyRepository = HistoryRepository(application)
        val factory = ViewModelFactory.getInstance(historyRepository)
        return ViewModelProvider(activity, factory)[ResultViewModel::class.java]
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

//    private fun showLoading(isLoading: Boolean) {
//        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
//    }

    companion object {
        const val TAG = "ResultActivity"
        const val EXTRA_IMAGE_URI = "extra_image_uri"
        const val EXTRA_LABEL = "extra_label"
    }
}
