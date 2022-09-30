package com.horizam.pro.elean.ui.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.horizam.pro.elean.Constants
import com.horizam.pro.elean.R
import com.horizam.pro.elean.data.model.response.Order
import com.horizam.pro.elean.databinding.ItemActiveOrderBinding
import com.horizam.pro.elean.ui.main.callbacks.OnItemClickListener
import com.horizam.pro.elean.utils.BaseUtils

class DisputedOrdersAdapter(val listener: OnItemClickListener) :
    PagingDataAdapter<Order, DisputedOrdersAdapter.DataViewHolder>(COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val binding =
            ItemActiveOrderBinding.inflate(LayoutInflater.from(parent.context),
                parent, false)
        return DataViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }
    override fun getItemViewType(position: Int): Int {
        return if (position == itemCount) Constants.DATA_ITEM else Constants.LOADING_ITEM
    }

    inner class DataViewHolder(private val binding: ItemActiveOrderBinding) :
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

        fun bind(order: Order) {
            binding.apply {
                tvUserName.text = order.username
                tvDate.text = BaseUtils.utcToLocal(order.created_at)
                tvDescription.text = order.description
                tvPrice.text = order.amount.toString().plus(order.currency)
                tvStatus.text = itemView.context.getString(R.string.str_disputed)
                Glide.with(itemView)
                    .load(Constants.BASE_URL.plus(order.image))
                    .error(R.drawable.bg_splash)
                    .into(ivUser)
            }
        }
    }

    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<Order>() {
            override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
                return oldItem == newItem
            }

        }
    }
}