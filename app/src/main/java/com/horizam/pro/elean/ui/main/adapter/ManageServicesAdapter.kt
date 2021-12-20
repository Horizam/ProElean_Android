package com.horizam.pro.elean.ui.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.horizam.pro.elean.Constants
import com.horizam.pro.elean.R
import com.horizam.pro.elean.data.model.User
import com.horizam.pro.elean.data.model.response.FeaturedGig
import com.horizam.pro.elean.data.model.response.SavedGig
import com.horizam.pro.elean.data.model.response.User_services
import com.horizam.pro.elean.databinding.ItemManageServiceBinding
import com.horizam.pro.elean.databinding.ItemServicesAndGigsBinding
import com.horizam.pro.elean.ui.main.callbacks.ManageServiceHandler
import com.horizam.pro.elean.ui.main.callbacks.OnItemClickListener

class ManageServicesAdapter(val listener: ManageServiceHandler) :
    ListAdapter<User_services, ManageServicesAdapter.DataViewHolder>(COMPARATOR) {

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

        private fun fetchItem(): User_services? {
            val position = bindingAdapterPosition
            return if (position != RecyclerView.NO_POSITION) {
                getItem(position)
            } else {
                null
            }
        }

        fun bind(userService: User_services) {
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
                tvServiceTitle.text = userService.s_description
                tvDescriptionGigsUser.text = userService.description
                tvClick.text = userService.total_clicks.toString()
                tvOrder.text = userService.orders.toString()
            }
        }
    }


    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<User_services>() {
            override fun areItemsTheSame(oldItem: User_services, newItem: User_services): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: User_services,
                newItem: User_services
            ): Boolean {
                return oldItem == newItem
            }

        }
    }
}