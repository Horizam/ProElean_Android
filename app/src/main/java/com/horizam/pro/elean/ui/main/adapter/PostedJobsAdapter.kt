package com.horizam.pro.elean.ui.main.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.horizam.pro.elean.Constants
import com.horizam.pro.elean.R
import com.horizam.pro.elean.data.model.User
import com.horizam.pro.elean.data.model.response.PostedJob
import com.horizam.pro.elean.databinding.ItemGigsBinding
import com.horizam.pro.elean.databinding.ItemPostedJobBinding
import com.horizam.pro.elean.ui.main.callbacks.OnItemClickListener
import com.horizam.pro.elean.ui.main.callbacks.PostedJobsHandler

class PostedJobsAdapter(
    private val listener: OnItemClickListener,private val jobsListener:PostedJobsHandler) :
    PagingDataAdapter<PostedJob, PostedJobsAdapter.DataViewHolder>(ITEM_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val binding =
            ItemPostedJobBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DataViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }

    inner class DataViewHolder(private val binding: ItemPostedJobBinding) :
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
                    jobsListener.deleteItem(item)
                }
            }
            binding.cardViewOffers.setOnClickListener {
                val item = fetchItem()
                if (item != null) {
                    jobsListener.viewOffers(item)
                }
            }
        }

        private fun fetchItem(): PostedJob? {
            val position = bindingAdapterPosition
            return if (position != RecyclerView.NO_POSITION) {
                getItem(position)
            } else {
                null
            }
        }

        fun bind(job: PostedJob) {
            binding.apply {
                tvDescription.text = job.description
//                val (status,color) = getStatusString(job.status)
//                tvStatus.text = status
//                cardViewStatus.setCardBackgroundColor(color)
                tvDuration.text = job.delivery_time
                tvBudgetList.text = job.budget.toString().plus(Constants.CURRENCY)
                tvDate.text = job.created_at
                if (job.total_offers > 0) {
                    tvOffers.text = itemView.context.getString(R.string.str_review_offers)
                        .plus(" ").plus(job.total_offers.toString())
                } else {
                    tvOffers.text = itemView.context.getString(R.string.str_no_offers_yet)
                }
            }
        }

        private fun getStatusString(status: Int): Pair<String,Int>{
             return when (status) {
                0 -> {
                    Pair(Constants.PENDING_STATUS,ContextCompat.getColor(itemView.context,R.color.colorOrangeStatus))
                }
                1 -> {
                    Pair(Constants.ACTIVE_STATUS,ContextCompat.getColor(itemView.context,R.color.colorGreenStatus))
                }
                2 -> {
                    Pair(Constants.UNAPPROVED_STATUS,ContextCompat.getColor(itemView.context,R.color.colorRedStatus))
                }
                else -> {
                    Pair(Constants.PENDING_STATUS,ContextCompat.getColor(itemView.context,R.color.colorOrangeStatus))
                }
            }
        }
    }

    companion object {
        private val ITEM_COMPARATOR = object : DiffUtil.ItemCallback<PostedJob>() {
            override fun areItemsTheSame(oldItem: PostedJob, newItem: PostedJob) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: PostedJob, newItem: PostedJob) =
                oldItem == newItem
        }
    }

}