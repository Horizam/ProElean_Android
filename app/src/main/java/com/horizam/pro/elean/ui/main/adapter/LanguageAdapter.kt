package com.horizam.pro.elean.ui.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.horizam.pro.elean.databinding.ItemSkillBinding
import com.horizam.pro.elean.ui.main.callbacks.OnItemClickListener

class LanguageAdapter(val listener: OnItemClickListener) : ListAdapter<String, LanguageAdapter.DataViewHolder>(COMPARATOR) {

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
                if (position!= RecyclerView.NO_POSITION){
                    val lang = getItem(position)
                    if (lang!=null){
                        listener.onItemClick(lang)
                    }
                }
            }
        }

        fun bind(lang: String) {
            binding.apply {
                tvSkillUser.text = lang
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

