package com.horizam.pro.elean.ui.main.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.horizam.pro.elean.Constants
import com.horizam.pro.elean.R
import com.horizam.pro.elean.data.model.User
import com.horizam.pro.elean.data.model.response.Offer
import com.horizam.pro.elean.data.model.response.PostedJob
import com.horizam.pro.elean.databinding.ItemPostedJobBinding
import com.horizam.pro.elean.databinding.ItemViewOfferBinding
import com.horizam.pro.elean.ui.main.callbacks.OnItemClickListener
import com.horizam.pro.elean.ui.main.callbacks.PostedJobsHandler
import com.horizam.pro.elean.ui.main.callbacks.ViewOffersHandler
import java.lang.Exception

class ViewOfferAdapter(
    private val listener: OnItemClickListener,
    private val offersListener: ViewOffersHandler
) :
    PagingDataAdapter<Offer, ViewOfferAdapter.DataViewHolder>(ITEM_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val binding =
            ItemViewOfferBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DataViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }

    inner class DataViewHolder(private val binding: ItemViewOfferBinding) :
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
                    offersListener.deleteItem(item)
                }
            }
            binding.ivUser.setOnClickListener {
                val item = fetchItem()
                if (item != null) {
                    offersListener.viewProfile(item)
                }
            }
            binding.btnAskQuestion.setOnClickListener {
                val item = fetchItem()
                if (item != null) {
                    offersListener.askQuestion(item)
                }
            }
            binding.btnOrder.setOnClickListener {
                val item = fetchItem()
                if (item != null) {
                    offersListener.order(item)
                }
            }
        }

        private fun fetchItem(): Offer? {
            val position = bindingAdapterPosition
            return if (position != RecyclerView.NO_POSITION) {
                getItem(position)
            } else {
                null
            }
        }

        fun bind(offer: Offer) {
            binding.apply {
                try {
                    if (offer.description.isEmpty()) {
                        tvDescription.text = itemView.context
                            .getString(R.string.str_desc_not_available)
                    } else {
                        tvDescription.text = offer.description
                    }
                    tvUserName.text = offer.profile.name
                    ratingBar.rating = offer.profile.user_rating.toFloat()
                    btnOrder.text = itemView.context.getString(R.string.str_order_title)
                        .plus("(${offer.price}${Constants.CURRENCY})")
                    Glide.with(itemView)
                        .load(Constants.BASE_URL.plus(offer.profile.image))
                        .error(R.drawable.img_profile)
                        .into(ivUser)
                } catch (exception: Exception) {
                    Log.i("Exception", exception.message.toString())
                }
            }
        }
    }

    companion object {
        private val ITEM_COMPARATOR = object : DiffUtil.ItemCallback<Offer>() {
            override fun areItemsTheSame(oldItem: Offer, newItem: Offer) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Offer, newItem: Offer) =
                oldItem == newItem
        }
    }

}