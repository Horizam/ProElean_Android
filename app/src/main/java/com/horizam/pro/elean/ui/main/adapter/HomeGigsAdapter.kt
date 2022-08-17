package com.horizam.pro.elean.ui.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.horizam.pro.elean.Constants
import com.horizam.pro.elean.R
import com.horizam.pro.elean.data.model.User
import com.horizam.pro.elean.data.model.response.Category
import com.horizam.pro.elean.data.model.response.FeaturedGig
import com.horizam.pro.elean.databinding.ItemServicesAndGigsBinding
import com.horizam.pro.elean.ui.main.callbacks.OnItemClickListener


class HomeGigsAdapter(val listener: OnItemClickListener) :
    ListAdapter<FeaturedGig,HomeGigsAdapter.DataViewHolder>(COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val binding = ItemServicesAndGigsBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return DataViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class DataViewHolder(private val binding: ItemServicesAndGigsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION){
                    val featuredGig = getItem(position)
                    if (featuredGig != null){
                        listener.onItemClick(featuredGig)
                    }
                }
            }
        }
        fun bind(featuredGig: FeaturedGig) {
            binding.apply {
                Glide.with(itemView)
                    .load("${Constants.BASE_URL}${featuredGig.service_media[0].media}")
                    .centerCrop()
                    .placeholder(R.drawable.img_loading)
                    .error(R.drawable.bg_splash)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(ivService)
                tvServiceName.text = featuredGig.s_description

            }
        }
    }


    companion object{
        private val COMPARATOR = object : DiffUtil.ItemCallback<FeaturedGig>(){
            override fun areItemsTheSame(oldItem: FeaturedGig, newItem: FeaturedGig): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: FeaturedGig, newItem: FeaturedGig): Boolean {
                return oldItem == newItem
            }

        }
    }
}