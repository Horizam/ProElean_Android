package com.horizam.pro.elean.ui.main.adapter

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.horizam.pro.elean.BuyerOrders
import com.horizam.pro.elean.Constants
import com.horizam.pro.elean.R
import com.horizam.pro.elean.SellerOrders
import com.horizam.pro.elean.data.model.User
import com.horizam.pro.elean.data.model.response.Notification
import com.horizam.pro.elean.data.model.response.Order
import com.horizam.pro.elean.databinding.ItemActiveOrderBinding
import com.horizam.pro.elean.ui.main.callbacks.NotificationsHandler
import com.horizam.pro.elean.ui.main.callbacks.OnItemClickListener
import com.horizam.pro.elean.ui.main.view.activities.OrderDetailsActivity
import com.horizam.pro.elean.ui.main.view.fragments.manageOrders.OrdersFragment
import com.horizam.pro.elean.ui.main.view.fragments.manageSales.SalesFragment
import com.horizam.pro.elean.utils.BaseUtils

class ActiveOrdersAdapter(val listener: OnItemClickListener) :
    ListAdapter<Order, ActiveOrdersAdapter.DataViewHolder>(COMPARATOR) {

    private var context = listener as OrdersFragment

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val binding =
            ItemActiveOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DataViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.bind(getItem(position))
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
                tvPrice.text = order.amount.toString().plus(Constants.CURRENCY)
                Glide.with(itemView)
                    .load(Constants.BASE_URL.plus(order.image))
                    .error(R.drawable.bg_splash)
                    .into(ivUser)
                when (order.status) {
                    SellerOrders.Active -> {
                        tvStatus.text = itemView.context.getString(R.string.str_active)
                        tvStatus.setTextColor(
                            ContextCompat.getColor(
                                context.requireContext(),
                                R.color.colorBlack
                            )
                        )
                        cardView.strokeColor = ContextCompat.getColor(
                            context.requireContext(),
                            R.color.colorThree
                        )
                        mainCard.strokeColor = ContextCompat.getColor(
                            context.requireContext(),
                            R.color.bg_primary
                        )
                    }
                    SellerOrders.Delivered -> {
                        tvStatus.text = itemView.context.getString(R.string.str_delivered)
                        tvStatus.setTextColor(
                            ContextCompat.getColor(
                                context.requireContext(),
                                R.color.colorBlack
                            )
                        )
                        cardView.strokeColor = ContextCompat.getColor(
                            context.requireContext(),
                            R.color.colorThree
                        )
                        mainCard.strokeColor = ContextCompat.getColor(
                            context.requireContext(),
                            R.color.bg_primary
                        )
                    }
                    SellerOrders.Revision -> {
                        tvStatus.text = itemView.context.getString(R.string.str_revision)
                        tvStatus.setTextColor(
                            ContextCompat.getColor(
                                context.requireContext(),
                                R.color.bg_primary
                            )
                        )
                        cardView.strokeColor = ContextCompat.getColor(
                            context.requireContext(),
                            R.color.colorThree
                        )
                        mainCard.strokeColor = ContextCompat.getColor(
                            context.requireContext(),
                            R.color.bg_primary
                        )
                    }
                    SellerOrders.Completed -> {
                        tvStatus.text = itemView.context.getString(R.string.str_completed)
                        tvStatus.setTextColor(
                            ContextCompat.getColor(
                                context.requireContext(),
                                R.color.colorBlack
                            )
                        )
                        cardView.strokeColor = ContextCompat.getColor(
                            context.requireContext(),
                            R.color.color_green
                        )
                        mainCard.strokeColor = ContextCompat.getColor(
                            context.requireContext(),
                            R.color.bg_primary
                        )
                    }
                    SellerOrders.Disputed -> {
                        tvStatus.text = itemView.context.getString(R.string.str_disputed)
                        tvStatus.setTextColor(
                            ContextCompat.getColor(
                                context.requireContext(),
                                R.color.colorBlack
                            )
                        )
                        cardView.strokeColor = ContextCompat.getColor(
                            context.requireContext(),
                            R.color.colorOrange
                        )
                        mainCard.strokeColor = ContextCompat.getColor(
                            context.requireContext(),
                            R.color.bg_primary
                        )
                    }
                    SellerOrders.Late -> {
                        tvStatus.text = itemView.context.getString(R.string.str_late)
                        tvStatus.setTextColor(
                            ContextCompat.getColor(
                                context.requireContext(),
                                R.color.colorBlack
                            )
                        )
                        cardView.strokeColor = ContextCompat.getColor(
                            context.requireContext(),
                            R.color.colorGolden
                        )
                        mainCard.strokeColor = ContextCompat.getColor(
                            context.requireContext(),
                            R.color.bg_primary
                        )
                    }
                    SellerOrders.Cancel -> {
                        tvStatus.text = itemView.context.getString(R.string.str_cancel)
                        tvStatus.setTextColor(
                            ContextCompat.getColor(
                                context.requireContext(),
                                R.color.colorBlack
                            )
                        )
                        cardView.strokeColor = ContextCompat.getColor(
                            context.requireContext(),
                            R.color.color_red
                        )
                        mainCard.strokeColor = ContextCompat.getColor(
                            context.requireContext(),
                            R.color.bg_primary
                        )
                    }
                }
            }
        }
    }

    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<Order>() {
            override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
                return oldItem.orderNo == newItem.orderNo
            }

            override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
                return oldItem == newItem
            }

        }
    }
}