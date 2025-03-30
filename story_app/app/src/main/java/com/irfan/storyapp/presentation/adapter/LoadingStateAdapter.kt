package com.irfan.storyapp.presentation.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.irfan.storyapp.databinding.ItemLoadingBinding

class LoadingStateAdapter(
    private val errorMessage: String,
    private val onRetryBtnClick: () -> Unit,
) :
    LoadStateAdapter<LoadingStateAdapter.LoadingStateViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState,
    ): LoadingStateViewHolder {
        val binding = ItemLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LoadingStateViewHolder(binding, onRetryBtnClick, errorMessage)
    }

    override fun onBindViewHolder(holder: LoadingStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    class LoadingStateViewHolder(
        private val binding: ItemLoadingBinding,
        private val onRetryBtnClick: () -> Unit,
        private val errorMessage: String,
    ) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.itemLoadingRetryButton.setOnClickListener {
                onRetryBtnClick()
            }
        }

        fun bind(loadState: LoadState) {
            binding.itemLoadingProgressBar.isVisible = loadState is LoadState.Loading

            if (loadState is LoadState.Error) {
                val localizedMessage = loadState.error.localizedMessage
                Log.d(TAG, "bind, LoadState.Error: $localizedMessage")
                binding.itemLoadingErrorMessage.text = errorMessage
            }

            binding.itemLoadingGroupError.isVisible = loadState is LoadState.Error
        }
    }

    companion object {
        const val TAG = "LoadingStateAdapter"
    }
}