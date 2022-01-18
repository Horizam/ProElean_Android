package com.horizam.pro.elean.ui.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.horizam.pro.elean.Constants
import com.horizam.pro.elean.R
import com.horizam.pro.elean.data.model.response.Category
import com.horizam.pro.elean.databinding.ItemServicesAndGigsBinding
import com.horizam.pro.elean.ui.main.callbacks.OnItemClickListener


class ServicesAdapter(val listener: OnItemClickListener) :
    ListAdapter<Category, ServicesAdapter.DataViewHolder>(COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val binding =
            ItemServicesAndGigsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DataViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class DataViewHolder(private val binding: ItemServicesAndGigsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val category = getItem(position)
                    if (category != null) {
                        listener.onItemClick(category)
                    }
                }
            }
        }

        fun bind(category: Category) {
            binding.apply {
//                    .load("${Constants.BASE_URL}${category.banner}")
                Glide.with(itemView)
                    .load(category.banner)
                    .centerCrop()
                    .placeholder(R.drawable.img_loading)
                    .error(R.drawable.ic_error)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(ivService)
                tvServiceName.text = category.title
            }
        }
    }

    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<Category>() {
            override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
                return oldItem == newItem
            }

        }
    }
}

