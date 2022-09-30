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
import com.horizam.pro.elean.data.model.response.ServiceDetail
import com.horizam.pro.elean.databinding.ItemManageServiceBinding
import com.horizam.pro.elean.ui.main.callbacks.ManageServiceHandler

class ManageServicesAdapter(val listener: ManageServiceHandler) :
    ListAdapter<ServiceDetail, ManageServicesAdapter.DataViewHolder>(COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val binding =
            ItemManageServiceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DataViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class DataViewHolder(private val binding: ItemManageServiceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val item = fetchItem()
                if (item != null) {
                    listener.onItemClick(item)
                }
            }
            binding.ivClose.setOnClickListener {
                val item = fetchItem()
                if (item != null) {
                    listener.removeService(item)
                }
            }
        }

        private fun fetchItem(): ServiceDetail? {
            val position = bindingAdapterPosition
            return if (position != RecyclerView.NO_POSITION) {
                getItem(position)
            } else {
                null
            }
        }

        fun bind(userService: ServiceDetail) {
            binding.apply {
                if (userService.service_media.isNotEmpty()) {
                    Glide.with(itemView)
                        .load("${Constants.BASE_URL}${userService.service_media[0].media}")
                        .centerCrop()
                        .placeholder(R.drawable.img_loading)
                        .error(R.drawable.bg_splash)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(ivMain)
                }
                tvServiceTitle.text = userService.s_description
                tvDescriptionGigsUser.text = userService.description
                tvClick.text = userService.total_clicks.toString()
                tvOrder.text = userService.total_orders.toString()
                tvPrice.text = "${userService.price}${Constants.CURRENCY}"
            }
        }
    }


    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<ServiceDetail>() {
            override fun areItemsTheSame(oldItem: ServiceDetail, newItem: ServiceDetail): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: ServiceDetail,
                newItem: ServiceDetail
            ): Boolean {
                return oldItem == newItem
            }

        }
    }
}