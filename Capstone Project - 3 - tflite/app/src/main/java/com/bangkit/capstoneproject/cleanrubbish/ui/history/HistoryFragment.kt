package com.bangkit.capstoneproject.cleanrubbish.ui.history

import android.app.Application
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.capstoneproject.cleanrubbish.R
import com.bangkit.capstoneproject.cleanrubbish.adapter.HistoryAdapter
import com.bangkit.capstoneproject.cleanrubbish.data.local.db.HistoryResult
import com.bangkit.capstoneproject.cleanrubbish.data.local.repo.HistoryRepository
import com.bangkit.capstoneproject.cleanrubbish.databinding.FragmentHistoryBinding
import com.bangkit.capstoneproject.cleanrubbish.helper.ViewModelFactory
import android.Manifest
import android.content.Context
import android.util.Log
import com.bangkit.capstoneproject.cleanrubbish.ui.home.HomeFragment
import kotlin.math.log

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HistoryViewModel
    private val adapter = HistoryAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val historyRepository = HistoryRepository(requireActivity().application)
        val factory = ViewModelFactory.getInstance(historyRepository)
        viewModel = ViewModelProvider(this, factory)[HistoryViewModel::class.java]

        setRecyclerView()

        viewModel.historyResult.observe(viewLifecycleOwner) { history ->
            if (history.isNullOrEmpty()) {
                binding.tvEmptyMessage.visibility = View.VISIBLE
                binding.rvHistory.visibility = View.GONE
            } else {
                binding.tvEmptyMessage.visibility = View.GONE
                binding.rvHistory.visibility = View.VISIBLE
                val items = arrayListOf<HistoryResult>()
                history.map { historyResult ->
                    val item = HistoryResult(image = historyResult.image, label = historyResult.label)
                    items.add(item)
                }
                adapter.submitList(items)
                binding.rvHistory.adapter = adapter
            }
        }

        return root
    }

    private fun setRecyclerView() {
        val layoutManager = LinearLayoutManager(requireActivity())
        binding.rvHistory.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(requireActivity(), layoutManager.orientation)
        binding.rvHistory.addItemDecoration(itemDecoration)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


