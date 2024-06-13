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
import android.content.Intent
import android.util.Log
import androidx.activity.result.PickVisualMediaRequest
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.capstoneproject.cleanrubbish.adapter.ArticleAdapter
import com.bangkit.capstoneproject.cleanrubbish.ui.result.ResultActivity
import com.bangkit.capstoneproject.cleanrubbish.data.utils.getImageUri
import com.bangkit.capstoneproject.cleanrubbish.helper.HomeViewModelFactory

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
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(requireActivity(), "Permission request granted", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(requireActivity(), "Permission request denied", Toast.LENGTH_LONG).show()
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            requireActivity(),
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        binding.btnGallery.setOnClickListener { startGallery() }
        binding.btnCamera.setOnClickListener { startCamera() }
        binding.btnScan.setOnClickListener {
            currentImageUri?.let {
                uploadImage()
            } ?: run {
                showToast(getString(R.string.empty_img_warning))
            }
        }

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



    private fun startGallery(){
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }
    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
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
            showImage()
        }
    }
    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.ivMainTrash.setImageURI(it)
        }
    }

    private fun uploadImage() {
        val intent = Intent(requireActivity(), ResultActivity::class.java)
        intent.putExtra(EXTRA_IMAGE_URI, currentImageUri.toString())
        startActivity(intent)
//        Toast.makeText(requireActivity(), "Fitur ini belum tersedia", Toast.LENGTH_SHORT).show()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
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
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
        const val KEY_DETAIL = "key_detail"
        const val EXTRA_IMAGE_URI = "extra_image_uri"
    }
}