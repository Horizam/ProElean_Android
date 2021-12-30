package com.horizam.pro.elean.ui.main.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.horizam.pro.elean.Constants
import com.horizam.pro.elean.R
import com.horizam.pro.elean.data.model.Message
import com.horizam.pro.elean.data.model.MessageUser
import com.horizam.pro.elean.databinding.ItemMessageLeftBinding
import com.horizam.pro.elean.databinding.ItemMessageRightBinding
import com.horizam.pro.elean.ui.main.callbacks.MessagesHandler

import com.bumptech.glide.request.RequestOptions


class MessageAdapter(val listener: MessagesHandler, myInfo: MessageUser?, userInfo: MessageUser?) :
    PagingDataAdapter<Message, RecyclerView.ViewHolder>(COMPARATOR) {

    private var userInfo: MessageUser? = userInfo
    private var myInfo: MessageUser? = myInfo


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0) {
            val binding =
                ItemMessageLeftBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            DataViewHolderLeft(binding)
        } else {
            val binding =
                ItemMessageRightBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            DataViewHolderRight(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is DataViewHolderLeft) {
            val message = getItem(position) ?: return
            holder.bind(message)
        } else if (holder is DataViewHolderRight) {
            val message = getItem(position) ?: return
            holder.bind(message)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val message = getItem(position)
        return if (message!!.senderId == userInfo!!.id) {
            0
        } else {
            1
        }
    }

    fun setUserInfo(user: MessageUser) {
        userInfo = user
    }

    fun setMyInfo(user: MessageUser) {
        myInfo = user
    }

    inner class DataViewHolderLeft(private val binding: ItemMessageLeftBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val message = fetchItem()
                if (message != null) {
                    listener.onMessageClick(message)
                }
            }
            binding.ivSentSender.setOnClickListener {
                val message = fetchItem()
                if (message != null) {
                    listener.onImageClick(message)
                }
            }
            binding.cvSenderVideo.setOnClickListener {
                val message = fetchItem()
                if (message != null) {
                    listener.onPlayVideo(message)
                }
            }
            binding.ivSenderDocumentDownload.setOnClickListener {
                val message = fetchItem()
                if (message != null) {
                    listener.onDownloadDocument(message)
                }
            }
            binding.btnOffer.setOnClickListener {
                val message = fetchItem()
                if (message != null) {
                    listener.onOfferButtonClick(message)
                }
            }
        }

        private fun fetchItem(): Message? {
            val position = bindingAdapterPosition
            return if (position != RecyclerView.NO_POSITION) {
                getItem(position)
            } else {
                null
            }
        }

        fun bind(message: Message) {
            binding.apply {
                if (Constants.MESSAGE_TYPE_TEXT == message.attachmentType) {
                    tvSenderMessage.text = message.message
                    tvSenderNameMessage.text = userInfo!!.name
                    changeViewsVisibility(
                        textMessage = true, image = false, video = false,
                        document = false,offer = false
                    )
                } else if (Constants.MESSAGE_TYPE_IMAGE == message.attachmentType) {
                    changeViewsVisibility(
                        textMessage = false, image = true, video = false,
                        document = false,offer = false
                    )
                    Glide.with(itemView)
                        .load(message.attachment)
                        .error(R.drawable.bg_splash)
                        .into(ivSentSender)
                } else if (Constants.MESSAGE_TYPE_VIDEO == message.attachmentType) {
                    changeViewsVisibility(
                        textMessage = false, image = false, video = true,
                        document = false,offer = false
                    )
                    // set thumbnail
                    val requestOptions = RequestOptions()
                        .placeholder(R.drawable.bg_splash)
                        .error(R.drawable.bg_splash)
                    Glide.with(itemView)
                        .load(message.attachment)
                        .apply(requestOptions)
                        .thumbnail(Glide.with(itemView).load(message.attachment))
                        .into(ivSenderThumbnail)
                } else if (Constants.MESSAGE_TYPE_DOCUMENT == message.attachmentType) {
                    changeViewsVisibility(
                        textMessage = false, image = false, video = false,
                        document = true,offer = false
                    )
                    tvSenderDocumentName.text = message.attachment.substringAfterLast("/")
                }else if (Constants.MESSAGE_TYPE_OFFER == message.attachmentType) {
                    changeViewsVisibility(
                        textMessage = false, image = false, video = false,
                        document = true,offer = true
                    )
                    message.messageOffer?.let { offer ->
                        binding.apply {
                            tvOfferServiceTitle.text = offer.serviceTitle
                            tvOfferDescription.text = offer.description
                            tvOfferPrice.text = itemView.context.getString(R.string.str_offer_price_new)
                                .plus(" ").plus(offer.totalOffer.toString()).plus(Constants.CURRENCY)
                            tvOfferTime.text = offer.deliveryDays
                            tvOfferRevisions.text = offer.revisions.plus(" ").plus(itemView.context.getString(R.string.str_revisions))
                            tvOfferDescription.text = offer.description
                            if (offer.offerSenderId == myInfo!!.id){
                                when(offer.status){
                                    Constants.OFFER_WITHDRAW -> {
                                        btnOffer.text = itemView.context.getString(R.string.str_withdraw_the_offer)
                                        btnOffer.setBackgroundColor(ContextCompat.getColor(itemView.context,R.color.colorButtons))
                                        btnOffer.isEnabled = true
                                    }
                                    Constants.OFFER_ACCEPTED -> {
                                        btnOffer.text = itemView.context.getString(R.string.str_offer_accepted)
                                        btnOffer.setBackgroundColor(ContextCompat.getColor(itemView.context,R.color.colorDarkGrey))
                                        btnOffer.isEnabled = false
                                    }
                                    Constants.OFFER_WITHDRAWN -> {
                                        btnOffer.text = itemView.context.getString(R.string.str_offer_withdrawn)
                                        btnOffer.setBackgroundColor(ContextCompat.getColor(itemView.context,R.color.colorDarkGrey))
                                        btnOffer.isEnabled = false
                                    }
                                }
                                btnOffer.isVisible = true
                            }else{
                                when(offer.status){
                                    Constants.OFFER_WITHDRAW -> {
                                        btnOffer.text = itemView.context.getString(R.string.str_accept_the_offer)
                                        btnOffer.setBackgroundColor(ContextCompat.getColor(itemView.context,R.color.colorButtons))
                                        btnOffer.isEnabled = true
                                    }
                                    Constants.OFFER_ACCEPTED -> {
                                        btnOffer.text = itemView.context.getString(R.string.str_offer_accepted)
                                        btnOffer.setBackgroundColor(ContextCompat.getColor(itemView.context,R.color.colorDarkGrey))
                                        btnOffer.isEnabled = false
                                    }
                                    Constants.OFFER_WITHDRAWN -> {
                                        btnOffer.text = itemView.context.getString(R.string.str_offer_withdrawn)
                                        btnOffer.setBackgroundColor(ContextCompat.getColor(itemView.context,R.color.colorDarkGrey))
                                        btnOffer.isEnabled = false
                                    }
                                }
                                btnOffer.isVisible = true
                            }
                        }
                    }
                }
                userInfo?.let { user ->
                    tvSenderNameMessage.text = user.name
                    Glide.with(itemView)
                        .load(Constants.BASE_URL.plus(user.photo))
                        .error(R.drawable.img_profile)
                        .into(ivSenderMessage)
                }
                if (message.refersGig){
                    try {
                        tvTitleGig.text = message.messageGig!!.gigTitle
                        tvDescriptionGig.text = message.messageGig.gigUsername
                        Glide.with(itemView)
                            .load(Constants.BASE_URL.plus(message.messageGig.gigImage))
                            .error(R.drawable.img_profile)
                            .into(ivMain)
                    }catch (e:Exception){
                        Log.i("Exception",e.message.toString())
                    }
                }
                binding.layoutGigRefer.isVisible = message.refersGig
            }
        }

        private fun changeViewsVisibility(
            textMessage: Boolean,
            image: Boolean,
            video: Boolean,
            document: Boolean,
            offer: Boolean
        ) {
            binding.apply {
                tvSenderMessage.isVisible = textMessage
                cvSender.isVisible = image
                cvSenderVideo.isVisible = video
                cvSenderDocument.isVisible = document
                cvSenderOffer.isVisible = offer
            }
        }
    }

    inner class DataViewHolderRight(private val binding: ItemMessageRightBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val message = fetchItem()
                if (message != null) {
                    listener.onMessageClick(message)
                }
            }
            binding.ivSentReceiver.setOnClickListener {
                val message = fetchItem()
                if (message != null) {
                    listener.onImageClick(message)
                }
            }
            binding.cvReceiverVideo.setOnClickListener {
                val message = fetchItem()
                if (message != null) {
                    listener.onPlayVideo(message)
                }
            }
            binding.ivReceiverDocumentDownload.setOnClickListener {
                val message = fetchItem()
                if (message != null) {
                    listener.onDownloadDocument(message)
                }
            }
            binding.btnOffer.setOnClickListener {
                val message = fetchItem()
                if (message != null) {
                    listener.onOfferButtonClick(message)
                }
            }
        }

        private fun fetchItem(): Message? {
            val position = bindingAdapterPosition
            return if (position != RecyclerView.NO_POSITION) {
                getItem(position)
            } else {
                null
            }
        }

        fun bind(message: Message) {
            binding.apply {
                if (Constants.MESSAGE_TYPE_TEXT == message.attachmentType) {
                    tvReceiverMessage.text = message.message
                    changeViewsVisibility(
                        textMessage = true, image = false, video = false,
                        document = false,offer = false
                    )
                } else if (Constants.MESSAGE_TYPE_IMAGE == message.attachmentType) {
                    changeViewsVisibility(
                        textMessage = false, image = true, video = false,
                        document = false,offer = false
                    )
                    Glide.with(itemView)
                        .load(message.attachment)
                        .error(R.drawable.bg_splash)
                        .into(ivSentReceiver)
                } else if (Constants.MESSAGE_TYPE_VIDEO == message.attachmentType) {
                    changeViewsVisibility(
                        textMessage = false, image = false, video = true,
                        document = false,offer = false
                    )
                    val requestOptions = RequestOptions()
                        .placeholder(R.drawable.bg_splash)
                        .error(R.drawable.bg_splash)
                    Glide.with(itemView)
                        .load(message.attachment)
                        .apply(requestOptions)
                        .thumbnail(Glide.with(itemView).load(message.attachment))
                        .into(ivThumbnail)

                } else if (Constants.MESSAGE_TYPE_DOCUMENT == message.attachmentType) {
                    changeViewsVisibility(
                        textMessage = false, image = false, video = false,
                        document = true,offer = false
                    )
                    tvReceiverDocumentName.text = message.attachment.substringAfterLast("/")
                }else if (Constants.MESSAGE_TYPE_OFFER == message.attachmentType) {
                    changeViewsVisibility(
                        textMessage = false, image = false, video = false,
                        document = true,offer = true
                    )
                    message.messageOffer?.let { offer ->
                        binding.apply {
                            tvOfferServiceTitle.text = offer.serviceTitle
                            tvOfferDescription.text = offer.description
                            tvOfferPrice.text = itemView.context.getString(R.string.str_offer_price_new)
                                .plus(" ").plus(offer.totalOffer.toString()).plus(Constants.CURRENCY)
                            tvOfferTime.text = offer.deliveryDays
                            tvOfferRevisions.text = offer.revisions.plus(" ").plus(itemView.context.getString(R.string.str_revisions))
                            tvOfferDescription.text = offer.description
                            if (offer.offerSenderId == myInfo!!.id){
                                when(offer.status){
                                    Constants.OFFER_WITHDRAW -> {
                                        btnOffer.text = itemView.context.getString(R.string.str_withdraw_the_offer)
                                        btnOffer.setBackgroundColor(ContextCompat.getColor(itemView.context,R.color.colorButtons))
                                        btnOffer.isEnabled = true
                                    }
                                    Constants.OFFER_ACCEPTED -> {
                                        btnOffer.text = itemView.context.getString(R.string.str_offer_accepted)
                                        btnOffer.setBackgroundColor(ContextCompat.getColor(itemView.context,R.color.colorDarkGrey))
                                        btnOffer.isEnabled = false
                                    }
                                    Constants.OFFER_WITHDRAWN -> {
                                        btnOffer.text = itemView.context.getString(R.string.str_offer_withdrawn)
                                        btnOffer.setBackgroundColor(ContextCompat.getColor(itemView.context,R.color.colorDarkGrey))
                                        btnOffer.isEnabled = false
                                    }
                                }
                                btnOffer.isVisible = true
                            }else{
                                when(offer.status){
                                    Constants.OFFER_WITHDRAW -> {
                                        btnOffer.text = itemView.context.getString(R.string.str_accept_the_offer)
                                        btnOffer.setBackgroundColor(ContextCompat.getColor(itemView.context,R.color.colorButtons))
                                        btnOffer.isEnabled = true
                                    }
                                    Constants.OFFER_ACCEPTED -> {
                                        btnOffer.text = itemView.context.getString(R.string.str_offer_accepted)
                                        btnOffer.setBackgroundColor(ContextCompat.getColor(itemView.context,R.color.colorDarkGrey))
                                        btnOffer.isEnabled = false
                                    }
                                    Constants.OFFER_WITHDRAWN -> {
                                        btnOffer.text = itemView.context.getString(R.string.str_offer_withdrawn)
                                        btnOffer.setBackgroundColor(ContextCompat.getColor(itemView.context,R.color.colorDarkGrey))
                                        btnOffer.isEnabled = false
                                    }
                                }
                                btnOffer.isVisible = true
                            }
                        }
                    }
                }
                myInfo?.let { user ->
                    tvReceiverNameMessage.text = user.name
                    tvReceiverNameMessage.visibility = View.GONE
                    Glide.with(itemView)
                        .load(Constants.BASE_URL.plus(user.photo))
                        .error(R.drawable.img_profile)
                        .into(ivReceiverMessage)
                }
                if (message.refersGig){
                    try {
                        tvTitleGig.text = message.messageGig!!.gigTitle
                        tvDescriptionGig.text = message.messageGig.gigUsername
                        Glide.with(itemView)
                            .load(Constants.BASE_URL.plus(message.messageGig.gigImage))
                            .error(R.drawable.img_profile)
                            .into(ivMain)
                    }catch (e:Exception){
                        Log.i("Exception",e.message.toString())
                    }
                }
                binding.layoutGigRefer.isVisible = message.refersGig
            }
        }

        private fun changeViewsVisibility(
            textMessage: Boolean,
            image: Boolean,
            video: Boolean,
            document: Boolean,
            offer: Boolean
        ) {
            binding.apply {
                tvReceiverMessage.isVisible = textMessage
                cvReceiver.isVisible = image
                cvReceiverVideo.isVisible = video
                cvReceiverDocument.isVisible = document
                cvReceiverOffer.isVisible = offer
            }
        }
    }

    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<Message>() {
            override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
                return oldItem == newItem
            }

        }
    }
}