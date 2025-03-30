package com.irfan.storyapp.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.irfan.storyapp.databinding.ItemRowStoryBinding
import com.irfan.storyapp.domain.entity.story.StoryEntity

class ListStoryAdapter(
    private val onItemClick: (StoryEntity) -> Unit,
) : PagingDataAdapter<StoryEntity, ListStoryAdapter.ListViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding =
            ItemRowStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val story = getItem(position)
        holder.binding.itemRowStoryTvTitle.text = story?.name ?: ""
        holder.binding.itemRowStoryTvDescription.text = story?.description ?: ""
        Glide.with(holder.itemView.context)
            .load(story?.photoUrl)
            .into(holder.binding.itemRowStoryImgContent)

        story?.let { holder.bind(it) }
    }

    inner class ListViewHolder(val binding: ItemRowStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
            
        fun bind(story: StoryEntity) {
            binding.root.setOnClickListener {
                onItemClick(story)
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryEntity>() {
            override fun areItemsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}