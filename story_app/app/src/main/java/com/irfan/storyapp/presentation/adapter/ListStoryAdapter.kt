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
    private val onTap: (story: StoryEntity, bindingItem: ItemRowStoryBinding) -> Unit,
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

        val id = story?.id ?: position.toString()
        holder.binding.itemRowStoryImgContent.transitionName =
            "detail_story_img_story_transition_$id"
        holder.binding.itemRowStoryTvTitle.transitionName = "detail_story_tv_title_transition_$id"
        holder.binding.itemRowStoryTvDescription.transitionName =
            "detail_story_tv_deskripsi_transition_$id"

        story?.let { holder.bind(it, position) }
    }

    inner class ListViewHolder(val binding: ItemRowStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(story: StoryEntity, position: Int) {
            binding.root.setOnClickListener {
                onTap(story, binding)
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