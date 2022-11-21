package com.horizam.pro.elean.ui.main.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.text.Html
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
import com.horizam.pro.elean.data.model.response.ServiceDetail
import com.horizam.pro.elean.databinding.ItemGigsBinding
import com.horizam.pro.elean.ui.main.callbacks.*
import com.horizam.pro.elean.ui.main.view.fragments.ServiceGigsFragment
import com.horizam.pro.elean.utils.PrefManager

class GigsAdapter(
    private val listener: OnItemClickListener,
    private val favouriteHandler: FavouriteHandler,
    private val contactSellerHandler: ContactSellerHandler,
    private val logoutHandler: LogoutHandler,
    private val serviceGigsFragment: ServiceGigsFragment,

) :
    PagingDataAdapter<ServiceDetail, GigsAdapter.DataViewHolder>(ITEM_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val binding =
            ItemGigsBinding.inflate(LayoutInflater.from(parent.context),
                parent, false)
        return DataViewHolder(binding)
    }
    private val serviceDetail: ServiceDetail?=null
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
            binding.btnContactSeller.setOnClickListener {
                val prefManager = PrefManager(serviceGigsFragment.requireContext())
                if (prefManager.accessToken.isEmpty()) {
                    logoutHandler.checkLogout()
                } else {
                    val position = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val item = getItem(position)
                        if (item != null) {
                            contactSellerHandler.contactSeller(item)
                        }
                    }
                }
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(serviceDetail: ServiceDetail) {
            binding.apply {
                ivShare.setOnClickListener {
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.setType("text/plain")
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Pro elean")
                    intent.putExtra(Intent.EXTRA_TEXT,
                        "${Constants.dex_Url}gig-detail/${serviceDetail.id}")
                    itemView.context.startActivity(intent)
                }
                binding.ivFavorite.setOnClickListener {
                    val position = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val item = getItem(position)
                        if (item != null) {
                            if(item.favourite==1)
                            {
                                ivFavorite.setImageResource(R.drawable.ic_not_liked)
                                item.favourite=0
                            }
                            else
                            {
                                ivFavorite.setImageResource(R.drawable.ic_liked)
                                item.favourite=1
                            }
                            favouriteHandler.addRemoveWishList(item)
                        }

                    }

                    else{
                    }

                }
                tvTitleGig.text = serviceDetail.s_description
                tvDescriptionGig.setText(Html.fromHtml(Html.fromHtml(serviceDetail.description).toString()))
                tvPrice.text = "${serviceDetail.price}${Constants.CURRENCY}"
                ratingGig.rating = serviceDetail.service_rating.toFloat()
                totalNumberOfRating.text = "(${serviceDetail.total_reviews})"
                if (serviceDetail.favourite == 1) {
                    ivFavorite.setImageResource(R.drawable.ic_liked)
                } else {
                    ivFavorite.setImageResource(R.drawable.ic_not_liked)
                }
                if (serviceDetail.service_media.size > 0) {
                    setImage("${Constants.BASE_URL}${serviceDetail.service_media[0].media}", ivMain)
                    // setImage(imageResource,ivFavorite)

                }

                setImage("${Constants.BASE_URL}${serviceDetail.service_user.image}", ivProfile)

            }
        }
        private fun <T> setImage(source: T, imageView: ImageView) {
            Glide.with(itemView)
                .load(source)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .error(R.drawable.bg_splash)
                .into(imageView)
        }
    }

    companion object {
        private val ITEM_COMPARATOR = object : DiffUtil.ItemCallback<ServiceDetail>() {
            override fun areItemsTheSame(oldItem: ServiceDetail, newItem: ServiceDetail) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: ServiceDetail,
                newItem: ServiceDetail) = oldItem == newItem
        }
    }

}