package com.horizam.pro.elean.ui.main.adapter

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.horizam.pro.elean.Constants
import com.horizam.pro.elean.R
import com.horizam.pro.elean.data.model.response.Gig
import com.horizam.pro.elean.databinding.ItemGigsBinding
import com.horizam.pro.elean.ui.main.callbacks.ContactSellerHandler
import com.horizam.pro.elean.ui.main.callbacks.FavouriteHandler
import com.horizam.pro.elean.ui.main.callbacks.OnItemClickListener

class GigsAdapter(private val listener: OnItemClickListener,
                  private val favouriteHandler: FavouriteHandler,
                  private val contactSellerHandler: ContactSellerHandler) :
    PagingDataAdapter<Gig, GigsAdapter.DataViewHolder>(ITEM_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val binding =
            ItemGigsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DataViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }

    inner class DataViewHolder(private val binding: ItemGigsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    if (item != null) {
                        listener.onItemClick(item)
                    }
                }
            }
            binding.ivFavorite.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    if (item != null) {
                        favouriteHandler.addRemoveWishList(item)
                    }
                }
            }
            binding.btnContactSeller.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    if (item != null) {
                        contactSellerHandler.contactSeller(item)
                    }
                }
            }
        }

        fun bind(gig: Gig) {
            binding.apply {
                tvTitleGig.text = gig.shortDescription
                tvDescriptionGig.text = gig.description
                ratingGig.rating = gig.rating.toFloat()
                val imageResource:Int = if (gig.favourite==0){
                    R.drawable.ic_not_liked
                }else{
                    R.drawable.ic_liked
                }
                setImage("${Constants.BASE_URL}${gig.banner}",ivMain)
                setImage("${Constants.BASE_URL}${gig.user_image}",ivProfile)
                setImage(imageResource,ivFavorite)
            }
        }

        private fun <T>setImage(source: T,imageView:ImageView){
            Glide.with(itemView)
                .load(source)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .error(R.drawable.bg_splash)
                .into(imageView)
        }
    }

    companion object {
        private val ITEM_COMPARATOR = object : DiffUtil.ItemCallback<Gig>() {
            override fun areItemsTheSame(oldItem: Gig, newItem: Gig) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Gig, newItem: Gig) =
                oldItem == newItem
        }
    }

}