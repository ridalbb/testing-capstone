package com.bangkit.capstoneproject.cleanrubbish.ui.home

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bangkit.capstoneproject.cleanrubbish.R
import com.bangkit.capstoneproject.cleanrubbish.databinding.FragmentHomeBinding
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.activity.result.PickVisualMediaRequest
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.capstoneproject.cleanrubbish.adapter.ArticleAdapter
import com.bangkit.capstoneproject.cleanrubbish.data.local.db.HistoryResult
import com.bangkit.capstoneproject.cleanrubbish.data.remote.response.PredictResponse
import com.bangkit.capstoneproject.cleanrubbish.data.remote.retrofit.ApiConfig
import com.bangkit.capstoneproject.cleanrubbish.ui.result.ResultActivity
import com.bangkit.capstoneproject.cleanrubbish.data.utils.getImageUri
import com.bangkit.capstoneproject.cleanrubbish.data.utils.reduceFileImage
import com.bangkit.capstoneproject.cleanrubbish.data.utils.uriToFile
import com.bangkit.capstoneproject.cleanrubbish.helper.HomeViewModelFactory
import com.google.gson.Gson
import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ArticleAdapter // Inisialisasi adapter

    private val viewModel: HomeViewModel by viewModels {
        val dataTitle = resources.getStringArray(R.array.data_title)
        val dataDescription = resources.getStringArray(R.array.data_description)
        val dataPhoto = resources.obtainTypedArray(R.array.data_photo)
        HomeViewModelFactory(dataTitle, dataDescription, dataPhoto)
    }

    private var currentImageUri: Uri? = null


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        showLoading(false)

        binding.btnGallery.setOnClickListener { startGallery() }
        binding.btnCamera.setOnClickListener { startCamera() }
        binding.btnScan.setOnClickListener { uploadImage() }

        binding.rvArticle.setHasFixedSize(true)
        binding.rvArticle.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)

        // Inisialisasi adapter setelah mendapatkan data dari ViewModel
        viewModel.articles.observe(viewLifecycleOwner) { articles ->
            // Gunakan data dari ViewModel untuk membuat adapter
            adapter = ArticleAdapter(articles)
            // Set adapter ke RecyclerView
            binding.rvArticle.adapter = adapter
        }

        return root
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }
    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val destinationUri = Uri.fromFile(createTempFile("image", ".jpeg"))
            UCrop.of(uri, destinationUri)
                .withAspectRatio(16f, 16f)
                .withMaxResultSize(1000, 1000)
                .start(requireActivity(), this)
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(requireActivity())
        launcherIntentCamera.launch(currentImageUri)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            val destinationUri = Uri.fromFile(createTempFile("image", ".jpeg"))
            currentImageUri?.let {
                UCrop.of(it, destinationUri)
                    .withAspectRatio(16f, 16f)
                    .withMaxResultSize(1000, 1000)
                    .start(requireActivity(), this)
            }
        }
    }
    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.ivMainTrash.setImageURI(it)
        }
    }

    private fun uploadImage() {
        currentImageUri?.let {
            val intent = Intent(requireActivity(), ResultActivity::class.java)
            intent.putExtra(EXTRA_IMAGE_URI, currentImageUri.toString())
            startActivity(intent)
        }?: showToast(getString(R.string.empty_img_warning))
    }

    private fun startClassify(imageUri: Uri){
        /*
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, requireActivity()).reduceFileImage()
            Log.d("Image Classification File", "showImage: ${imageFile.path}")
            showLoading(true)
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
                    if (!label.isNullOrEmpty()) {
                        // Kirim hasil klasifikasi ke ResultActivity
                        val intent = Intent(requireActivity(), ResultActivity::class.java)
                        intent.putExtra(EXTRA_LABEL, label)
                        intent.putExtra(EXTRA_IMAGE_URI, imageUri.toString())
                        startActivity(intent)
                    } else {
                        showToast("Terjadi Kesalahan")
                    }
                    showLoading(false)
                } catch (e: HttpException) {
                    val errorBody = e.response()?.errorBody()?.string()
                    val errorResponse = Gson().fromJson(errorBody, PredictResponse::class.java)
                    showToast(errorResponse.message.toString())
                    showLoading(false)
                }
            }
        } ?: showToast(getString(R.string.empty_img_warning))
        */

    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val resultUri: Uri? = UCrop.getOutput(data!!)
            if (resultUri != null) {
                currentImageUri = resultUri
                showImage()
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError: Throwable? = UCrop.getError(data!!)
            Toast.makeText(requireActivity(), "Error during cropping: ${cropError?.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
    }
    private fun showLoading(isLoading: Boolean) {
        binding.pbHome.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    override fun onResume() {
        super.onResume()

        binding.rvArticle.scrollToPosition(0)
    }

    companion object {
        const val KEY_DETAIL = "key_detail"
        const val EXTRA_LABEL = "extra_label"
        const val EXTRA_IMAGE_URI = "extra_image_uri"
    }
}