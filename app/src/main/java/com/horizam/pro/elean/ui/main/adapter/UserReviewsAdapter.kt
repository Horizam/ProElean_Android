package com.horizam.pro.elean.ui.main.adapter

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
import com.horizam.pro.elean.data.model.User
import com.horizam.pro.elean.data.model.response.UserReview
import com.horizam.pro.elean.data.model.response.User_services
import com.horizam.pro.elean.databinding.ItemUserGigBinding
import com.horizam.pro.elean.databinding.ItemUserReviewBinding
import com.horizam.pro.elean.ui.main.callbacks.OnItemClickListener

class UserReviewsAdapter(val listener: OnItemClickListener) : ListAdapter<UserReview, UserReviewsAdapter.DataViewHolder>(COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val binding = ItemUserReviewBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return DataViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class DataViewHolder(private val binding: ItemUserReviewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION){
                    val review = getItem(position)
                    if (review != null){
                        listener.onItemClick(review)
                    }
                }
            }
        }

        fun bind(review : UserReview) {
            binding.apply {
                Glide.with(itemView)
                    .load(Constants.BASE_URL.plus(review.profile))
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .error(R.drawable.ic_error)
                    .into(ivReview)
                tvNameReview.text = review.user_name
                tvDescReview.text = review.comment
                ratingBarReview.rating = review.rating.toFloat()
            }
        }
    }

    companion object{
        private val COMPARATOR = object : DiffUtil.ItemCallback<UserReview>(){
            override fun areItemsTheSame(oldItem: UserReview, newItem: UserReview): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: UserReview, newItem: UserReview): Boolean {
                return oldItem == newItem
            }

        }
    }
}