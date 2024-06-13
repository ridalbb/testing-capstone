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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.capstoneproject.cleanrubbish.R
import com.bangkit.capstoneproject.cleanrubbish.databinding.FragmentHomeBinding
import android.Manifest
import android.content.Intent
import android.util.Log
import androidx.activity.result.PickVisualMediaRequest
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.capstoneproject.cleanrubbish.Article
import com.bangkit.capstoneproject.cleanrubbish.adapter.ArticleAdapter
import com.bangkit.capstoneproject.cleanrubbish.ui.result.ResultActivity
import com.bangkit.capstoneproject.cleanrubbish.data.utils.getImageUri

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

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

    private lateinit var rvArticle: RecyclerView
    private val list = ArrayList<Article>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this)[HomeViewModel::class.java]

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        binding.btnGallery.setOnClickListener { startGallery() }
        binding.btnCamera.setOnClickListener { startCamera() }
        binding.btnScan.setOnClickListener { uploadImage() }


        binding.rvArticle.setHasFixedSize(true)

        list.addAll(listArticle)
        showRecyclerList()
        return root
    }

    private val listArticle: ArrayList<Article>
        get() {
            val dataDescription = resources.getStringArray(R.array.data_description)
            val dataTitle = resources.getStringArray(R.array.data_title)
            val dataPhoto = resources.obtainTypedArray(R.array.data_photo)
            val listArticle = ArrayList<Article>()
            for (i in dataTitle.indices) {
                val article = Article(dataTitle[i], dataDescription[i], dataPhoto.getResourceId(i, -1))
                listArticle.add(article)
            }
            dataPhoto.recycle()
            return listArticle
        }

    private fun showRecyclerList() {
        binding.rvArticle.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        val listArticleAdapter = ArticleAdapter(list)
        binding.rvArticle.adapter = listArticleAdapter
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
        startActivity(Intent(requireActivity(), ResultActivity::class.java))
        Toast.makeText(requireActivity(), "Fitur ini belum tersedia", Toast.LENGTH_SHORT).show()
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
    }
}