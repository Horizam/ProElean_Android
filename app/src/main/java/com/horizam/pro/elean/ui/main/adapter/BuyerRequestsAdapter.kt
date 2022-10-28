package com.horizam.pro.elean.ui.main.adapter

import android.os.Build
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.horizam.pro.elean.App
import com.horizam.pro.elean.Constants
import com.horizam.pro.elean.R
import com.horizam.pro.elean.data.model.response.BuyerRequest
import com.horizam.pro.elean.databinding.ItemBuyerRequestBinding
import com.horizam.pro.elean.ui.main.callbacks.BuyerRequestsHandler
import com.horizam.pro.elean.ui.main.callbacks.OnItemClickListener
import com.horizam.pro.elean.utils.PrefManager
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class BuyerRequestsAdapter(
    private val listener: OnItemClickListener,
    private val buyersRequestsListener: BuyerRequestsHandler
) :
    PagingDataAdapter<BuyerRequest, BuyerRequestsAdapter.DataViewHolder>(ITEM_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val binding =
            ItemBuyerRequestBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DataViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }
    inner class DataViewHolder(private val binding: ItemBuyerRequestBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.layoutRequests.setOnClickListener {
                val item = fetchItem()
                if (item != null) {
                    listener.onItemClick(item)
                }
            }
            binding.btnCancelOffer.setOnClickListener {
                val item = fetchItem()
                if (item != null) {
                    buyersRequestsListener.cancelOffer(item)
                }
            }
            binding.btnSendOffer.setOnClickListener {
                val item = fetchItem()
                if (item != null) {
                    buyersRequestsListener.sendOffer(item)
                }
            }
        }

        private fun fetchItem(): BuyerRequest? {
            val position = bindingAdapterPosition
            return if (position != RecyclerView.NO_POSITION) {
                getItem(position)
            } else {
                null
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(buyerRequest: BuyerRequest) {
            var prefManager: PrefManager = PrefManager(App.getAppContext()!!)
            binding.apply {
                try {
                    tvName.text = buyerRequest.user.username
                    tvDate.text = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM-dd-yyyy"))
                    tvDuration.text = buyerRequest.delivery_time
                    tvDescription.setText(Html.fromHtml(Html.fromHtml(buyerRequest.description).toString()))

                    tvBudget.text = buyerRequest.budget.toString().plus(Constants.CURRENCY)
                    if (buyerRequest.cinic.isEmpty()) {
                        if(prefManager.setLanguage=="0")
                        {
                            tvOffers.text = buyerRequest.total_offers.toString().plus(" offer sent")
                            tvDocument.text = "No Attachment"
                        }
                        else
                        {
                            tvOffers.text = buyerRequest.total_offers.toString().plus (" tarjous lähetetty")
                            tvDocument.text = "Ei liitettä"
                        }

                    } else {
                        tvDocument.text = buyerRequest.cinic
                    }
                    setButton(buyerRequest)
                    Glide.with(itemView)
                        .load(Constants.BASE_URL.plus(buyerRequest.user.image))
                        .error(R.drawable.img_profile)
                        .into(ivBuyerRequest)
                } catch (exception: Exception) {
                    Log.i(BuyerRequestsAdapter::class.java.simpleName, exception.message.toString())
                }
            }
        }

        private fun setButton(buyerRequest: BuyerRequest) {
            if (buyerRequest.is_applied == 0) {
                binding.btnSendOffer.apply {
                    isEnabled = true
                    setBackgroundColor(ContextCompat.getColor(this.context, R.color.colorAccent))
                    setTextColor(ContextCompat.getColor(this.context, R.color.colorWhite))
                    setText(R.string.str_send_offer)
                }
            } else {
                binding.btnSendOffer.apply {
                    isEnabled = false
                    setBackgroundColor(ContextCompat.getColor(this.context, R.color.colorDarkGrey))
                    setTextColor(ContextCompat.getColor(this.context, R.color.colorWhite))
                    setText(R.string.str_applied)
                }
            }
        }
    }

    companion object {
        private val ITEM_COMPARATOR = object : DiffUtil.ItemCallback<BuyerRequest>() {
            override fun areItemsTheSame(oldItem: BuyerRequest, newItem: BuyerRequest) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: BuyerRequest, newItem: BuyerRequest) =
                oldItem == newItem
        }
    }

}