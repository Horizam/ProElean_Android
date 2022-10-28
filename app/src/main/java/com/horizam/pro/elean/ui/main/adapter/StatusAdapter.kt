package com.geeksforgeeks.horizontalrecyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.horizam.pro.elean.databinding.ItemStatusBinding

class StatusAdapter : RecyclerView.Adapter<StatusAdapter.MyView>() {
    private var list: List<String>? = null
    inner class MyView(private val binding: ItemStatusBinding) : RecyclerView.ViewHolder(binding.root) {
        lateinit var text:TextView
        init {
            binding.root.setOnClickListener {
                text=binding.status
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyView {
        val itemView= ItemStatusBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyView(itemView)
    }

    override fun onBindViewHolder(holder: MyView, position: Int ) {
    }
    override fun getItemCount(): Int {
        return list!!.size
    }
}