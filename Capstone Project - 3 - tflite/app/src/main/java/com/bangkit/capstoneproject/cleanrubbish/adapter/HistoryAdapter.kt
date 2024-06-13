package com.bangkit.capstoneproject.cleanrubbish.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.capstoneproject.cleanrubbish.data.local.db.HistoryResult
import com.bangkit.capstoneproject.cleanrubbish.databinding.ItemHistoryBinding
import com.bangkit.capstoneproject.cleanrubbish.ui.result.ResultActivity
import com.bangkit.capstoneproject.cleanrubbish.ui.result.ResultViewModel
import com.bumptech.glide.Glide

class HistoryAdapter: ListAdapter<HistoryResult, HistoryAdapter.MyViewHolder>(DIFF_CALLBACK){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val history = getItem(position)
        holder.bind(history)

        holder.itemView.setOnClickListener {
            val intentDetail = Intent(holder.itemView.context, ResultActivity::class.java)

            intentDetail.putExtra(ResultActivity.EXTRA_IMAGE_URI, history.image)
            intentDetail.putExtra(ResultViewModel.KEY_LABEL, history.label)

            holder.itemView.context.startActivity(intentDetail)
        }

    }

    class MyViewHolder(private val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(historyResult: HistoryResult) {
            binding.tvHistoryTitle.text = historyResult.label
            Glide.with(itemView.context)
                .load(historyResult.image)
                .into(binding.ivHistoryPhoto)
        }
    }
    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<HistoryResult>() {
            override fun areItemsTheSame(oldItem: HistoryResult, newItem: HistoryResult): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: HistoryResult, newItem: HistoryResult): Boolean {
                return oldItem == newItem
            }
        }
    }

}