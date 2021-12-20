package com.horizam.pro.elean.ui.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.horizam.pro.elean.Constants
import com.horizam.pro.elean.R
import com.horizam.pro.elean.data.model.response.Subcategory
import com.horizam.pro.elean.databinding.ItemServicesCategoryBinding
import com.horizam.pro.elean.ui.main.callbacks.OnItemClickListener

class CategoryAdapter(private val listener: OnItemClickListener) :
    PagingDataAdapter<Subcategory, CategoryAdapter.DataViewHolder>(ITEM_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val binding =
            ItemServicesCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DataViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == itemCount) Constants.DATA_ITEM else Constants.LOADING_ITEM
    }

    inner class DataViewHolder(private val binding: ItemServicesCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    if (item != null) {
                        listener.onItemClick(item)
                    }
                }
            }
        }

        fun bind(subcategory: Subcategory) {
            binding.apply {
                Glide.with(itemView)
                    .load("${Constants.BASE_URL}${subcategory.banner}")
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(R.drawable.ic_error)
                    .into(ivService)
                tvServiceName.text = subcategory.title
            }
        }
    }

    companion object {
        private val ITEM_COMPARATOR = object : DiffUtil.ItemCallback<Subcategory>() {
            override fun areItemsTheSame(oldItem: Subcategory, newItem: Subcategory) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Subcategory, newItem: Subcategory) =
                oldItem == newItem
        }
    }

}