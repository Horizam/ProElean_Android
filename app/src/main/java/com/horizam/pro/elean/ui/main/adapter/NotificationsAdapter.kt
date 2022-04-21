package com.horizam.pro.elean.ui.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.horizam.pro.elean.Constants
import com.horizam.pro.elean.R
import com.horizam.pro.elean.data.model.User
import com.horizam.pro.elean.data.model.response.Notification
import com.horizam.pro.elean.data.model.response.Review
import com.horizam.pro.elean.databinding.ItemNotificationBinding
import com.horizam.pro.elean.databinding.ItemReviewBinding
import com.horizam.pro.elean.ui.main.callbacks.NotificationsHandler
import com.horizam.pro.elean.ui.main.callbacks.OnItemClickListener

class NotificationsAdapter(val listener: NotificationsHandler) :
    ListAdapter<Notification, NotificationsAdapter.DataViewHolder>(COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val binding =
            ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DataViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class DataViewHolder(private val binding: ItemNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val review = getItem(position)
                    if (review != null) {
                        listener.onItemClick(review)
                    }
                }
            }
        }

        fun bind(notification: Notification) {
            binding.apply {
                tvNotificationTitle.text = notification.name
                tvNotification.text = notification.body
                tvNotificationDate.text = notification.created_at
//                val pair: Pair<Int, Int> = getImageAndColor(notification.type)
//                tvNotificationTitle.setTextColor(ContextCompat.getColor(itemView.context,pair.first))
                Glide.with(itemView)
                    .load(Constants.BASE_URL + notification.sender_pic)
                    .error(R.drawable.bg_splash)
                    .into(ivNotification)
            }
        }

        private fun getImageAndColor(type: Int): Pair<Int, Int> {
            val color: Int
            val imageResource: Int
            when (type) {
                Constants.NOTIFICATION_TYPE_INFO -> {
                    color = R.color.colorLightBlue
                    imageResource = R.drawable.ic_info
                }
                Constants.NOTIFICATION_TYPE_SUCCESS -> {
                    color = R.color.colorGreenStatus
                    imageResource = R.drawable.ic_success
                }
                Constants.NOTIFICATION_TYPE_WARNING -> {
                    color = R.color.colorOrange
                    imageResource = R.drawable.ic_warning
                }
                Constants.NOTIFICATION_TYPE_ERROR -> {
                    color = R.color.colorAccent
                    imageResource = R.drawable.ic_error
                }
                else -> {
                    color = R.color.colorLightBlue
                    imageResource = R.drawable.ic_info
                }
            }
            return Pair(color, imageResource)
        }
    }

    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<Notification>() {
            override fun areItemsTheSame(oldItem: Notification, newItem: Notification): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Notification, newItem: Notification): Boolean {
                return oldItem == newItem
            }

        }
    }
}