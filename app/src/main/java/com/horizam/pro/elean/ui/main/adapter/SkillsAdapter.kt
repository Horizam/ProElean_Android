package com.horizam.pro.elean.ui.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.horizam.pro.elean.Constants
import com.horizam.pro.elean.R
import com.horizam.pro.elean.data.model.User
import com.horizam.pro.elean.data.model.response.Category
import com.horizam.pro.elean.databinding.ItemServicesAndGigsBinding
import com.horizam.pro.elean.databinding.ItemSkillBinding
import com.horizam.pro.elean.ui.main.callbacks.OnItemClickListener

class SkillsAdapter(val listener: OnItemClickListener) : ListAdapter<String,SkillsAdapter.DataViewHolder>(COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val binding = ItemSkillBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return DataViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class DataViewHolder(private val binding: ItemSkillBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position!=RecyclerView.NO_POSITION){
                    val skill = getItem(position)
                    if (skill!=null){
                        listener.onItemClick(skill)
                    }
                }
            }
        }

        fun bind(skill: String) {
            binding.apply {
                tvSkillUser.text = skill
            }
        }
    }

    companion object{
        private val COMPARATOR = object : DiffUtil.ItemCallback<String>(){
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }

        }
    }
}

