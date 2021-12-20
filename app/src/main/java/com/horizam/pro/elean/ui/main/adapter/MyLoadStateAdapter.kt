package com.horizam.pro.elean.ui.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.horizam.pro.elean.databinding.ItemLoadStateAdapterBinding

class MyLoadStateAdapter(private val retry:()->Unit):LoadStateAdapter<MyLoadStateAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): MyViewHolder {
        val binding = ItemLoadStateAdapterBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    inner class MyViewHolder(private val binding: ItemLoadStateAdapterBinding) :RecyclerView.ViewHolder(binding.root){

        init {
            binding.btnRetry.setOnClickListener {
                retry.invoke()
            }
        }

        fun bind(loadState: LoadState) {
            binding.apply {
                progressBar.isVisible = loadState is LoadState.Loading
                tvResultsNotLoaded.isVisible = loadState !is LoadState.Loading
                btnRetry.isVisible = loadState !is LoadState.Loading
            }
        }

    }
}