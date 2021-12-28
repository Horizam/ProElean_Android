package com.horizam.pro.elean.ui.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.horizam.pro.elean.Constants
import com.horizam.pro.elean.R
import com.horizam.pro.elean.data.model.User
import com.horizam.pro.elean.data.model.response.ServiceDetail
import com.horizam.pro.elean.data.model.response.User_services
import com.horizam.pro.elean.databinding.ItemManageServiceBinding
import com.horizam.pro.elean.databinding.ItemSellerServicesBinding
import com.horizam.pro.elean.ui.main.callbacks.ManageServiceHandler
import com.horizam.pro.elean.ui.main.callbacks.OnItemClickListener
import com.horizam.pro.elean.ui.main.view.fragments.CustomOrderBottomSheet

class SellerServicesAdapter(val listener: OnItemClickListener) :
    ListAdapter<ServiceDetail, SellerServicesAdapter.DataViewHolder>(COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val binding =
            ItemSellerServicesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DataViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class DataViewHolder(private val binding: ItemSellerServicesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val item = fetchItem()
                if (item != null) {
                    listener.onItemClick(item)
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
                if (!userService.service_media.isNullOrEmpty()) {
                    Glide.with(itemView)
                        .load("${Constants.BASE_URL}${userService.service_media[0].media}")
                        .centerCrop()
                        .placeholder(R.drawable.img_loading)
                        .error(R.drawable.bg_splash)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(ivMain)
                }
                tvDetails.text = userService.description
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