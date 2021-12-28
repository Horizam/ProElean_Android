package com.horizam.pro.elean.ui.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.vipulasri.timelineview.TimelineView
import com.horizam.pro.elean.data.model.response.Action
import com.horizam.pro.elean.databinding.ItemTimelineBinding
import com.horizam.pro.elean.ui.main.callbacks.OnItemClickListener
import com.horizam.pro.elean.utils.BaseUtils


class OrderTimelineAdapter(val listener: OnItemClickListener) :
    ListAdapter<Action, OrderTimelineAdapter.DataViewHolder>(COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val binding =
            ItemTimelineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DataViewHolder(binding,viewType)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        return TimelineView.getTimeLineViewType(position, itemCount)
    }

    inner class DataViewHolder(private val binding: ItemTimelineBinding, viewType: Int) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.timeline.initLine(viewType)
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

        fun bind(action: Action) {
            binding.apply {
                textTimelineDate.text = BaseUtils.utcToLocal(action.created_at)
                textTimelineTitle.text = action.description
            }
        }
    }

    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<Action>() {
            override fun areItemsTheSame(oldItem: Action, newItem: Action): Boolean {
                return oldItem.created_at == newItem.created_at
            }

            override fun areContentsTheSame(oldItem: Action, newItem: Action): Boolean {
                return oldItem == newItem
            }

        }
    }
}