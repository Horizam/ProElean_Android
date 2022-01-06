package com.horizam.pro.elean.ui.main.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.horizam.pro.elean.Constants
import com.horizam.pro.elean.R
import com.horizam.pro.elean.data.model.response.ServiceDetail
import com.horizam.pro.elean.databinding.ItemUserGigBinding
import com.horizam.pro.elean.ui.main.callbacks.FavouriteHandler
import com.horizam.pro.elean.ui.main.callbacks.OnItemClickListener

class UserGigsAdapter(
    val listener: OnItemClickListener,
    val favouriteHandler: FavouriteHandler,
    val userID: String = ""
) :
    ListAdapter<ServiceDetail, UserGigsAdapter.DataViewHolder>(COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val binding = ItemUserGigBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DataViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class DataViewHolder(private val binding: ItemUserGigBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val service = getItem(position)
                    if (service != null) {
                        listener.onItemClick(service)
                    }
                }
            }

            binding.ivHeartGigsUser.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    if (item != null) {
                        favouriteHandler.addRemoveWishList(item)
                    }
                }
            }
        }

        fun bind(service: ServiceDetail) {
            binding.apply {
                if (userID == service.service_user.id) {
                    ivHeartGigsUser.visibility = View.GONE
                } else {
                    ivHeartGigsUser.visibility = View.VISIBLE
                }
                tvDescriptionGigsUser.text = service.s_description
                tvUserRating.text = service.service_rating.toString()
                tvRatingNumber.text = "(".plus(service.total_reviews).plus(")")
                tvPriceGigsUser.text = service.price.toString().plus(Constants.CURRENCY)
                val imageResource: Int = if (service.favourite == 0) {
                    R.drawable.ic_not_liked
                } else {
                    R.drawable.ic_liked
                }
                if (service.service_media.isNotEmpty()) {
                    val image = service.service_media[Constants.STARTING_ARRAY_INDEX].media
                    setImage("${Constants.BASE_URL}${image}", ivMain)
                }
                setImage(imageResource, ivHeartGigsUser)
            }
        }

        private fun <T> setImage(source: T, imageView: ImageView) {
            Glide.with(itemView)
                .load(source)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .error(R.drawable.ic_error)
                .into(imageView)
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