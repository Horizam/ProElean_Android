package com.horizam.pro.elean.ui.main.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.horizam.pro.elean.Constants
import com.horizam.pro.elean.R
import com.horizam.pro.elean.data.model.User
import com.horizam.pro.elean.data.model.response.BuyerRequest
import com.horizam.pro.elean.data.model.response.SavedGig
import com.horizam.pro.elean.data.model.response.ServiceDetail
import com.horizam.pro.elean.databinding.ItemBuyerRequestBinding
import com.horizam.pro.elean.databinding.ItemGigsBinding
import com.horizam.pro.elean.ui.main.callbacks.BuyerRequestsHandler
import com.horizam.pro.elean.ui.main.callbacks.OnItemClickListener
import com.horizam.pro.elean.ui.main.callbacks.SavedGigsHandler

class SavedAdapter(
    private val savedGigsHandler: SavedGigsHandler
) :
    PagingDataAdapter<ServiceDetail, SavedAdapter.DataViewHolder>(ITEM_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val binding =
            ItemGigsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DataViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }

    inner class DataViewHolder(private val binding: ItemGigsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val item = fetchItem()
                if (item != null) {
                    savedGigsHandler.onItemClick(item)
                }
            }
            binding.ivFavorite.setOnClickListener {
                val item = fetchItem()
                if (item != null) {
                    savedGigsHandler.addRemoveWishList(item)
                }
            }
            binding.btnContactSeller.setOnClickListener {
                val item = fetchItem()
                if (item != null) {
                    savedGigsHandler.contactSeller(item)
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

        fun bind(serviceDetail: ServiceDetail) {
            binding.apply {
                try {
                    tvTitleGig.text = serviceDetail.s_description
                    tvDescriptionGig.text = serviceDetail.description
                    ratingGig.rating = serviceDetail.service_rating.toFloat()
                    Glide.with(itemView)
                        .load(Constants.BASE_URL.plus(serviceDetail.service_user.image))
                        .error(R.drawable.img_profile)
                        .into(ivProfile)
                    Glide.with(itemView)
                        .load(Constants.BASE_URL.plus(serviceDetail.service_media[0]))
                        .error(R.drawable.bg_splash)
                        .into(ivMain)
                    Glide.with(itemView)
                        .load(R.drawable.ic_liked)
                        .error(R.drawable.ic_liked)
                        .into(ivFavorite)
                } catch (exception: Exception) {
                    Log.i(BuyerRequestsAdapter::class.java.simpleName, exception.message.toString())
                }
            }
        }

    }

    companion object {
        private val ITEM_COMPARATOR = object : DiffUtil.ItemCallback<ServiceDetail>() {
            override fun areItemsTheSame(oldItem: ServiceDetail, newItem: ServiceDetail) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: ServiceDetail, newItem: ServiceDetail) =
                oldItem == newItem
        }
    }

}