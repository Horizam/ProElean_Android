package com.horizam.pro.elean.ui.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.horizam.pro.elean.data.model.response.Review
import com.horizam.pro.elean.data.model.response.ServiceReviews
import com.horizam.pro.elean.databinding.ItemReviewBinding
import com.horizam.pro.elean.databinding.ItemServicesAndGigsBinding
import com.horizam.pro.elean.ui.main.callbacks.OnItemClickListener

class ReviewsAdapter(val listener: OnItemClickListener) : ListAdapter<ServiceReviews, ReviewsAdapter.DataViewHolder>(COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val binding = ItemReviewBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return DataViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class DataViewHolder(private val binding: ItemReviewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position!=RecyclerView.NO_POSITION){
                    val review = getItem(position)
                    if (review!=null){
                        listener.onItemClick(review)
                    }
                }
            }
        }

        fun bind(review: ServiceReviews) {
            binding.apply {
                ratingBarReview.rating = review.rating.toFloat()
                tvUserName.text = review.user.name
                tvReview.text = review.description
            }
        }
    }

    companion object{
        private val COMPARATOR = object : DiffUtil.ItemCallback<ServiceReviews>(){
            override fun areItemsTheSame(oldItem: ServiceReviews, newItem: ServiceReviews): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ServiceReviews, newItem: ServiceReviews): Boolean {
                return oldItem == newItem
            }

        }
    }
}