package com.horizam.pro.elean.ui.main.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.horizam.pro.elean.Constants
import com.horizam.pro.elean.R
import com.horizam.pro.elean.data.model.Inbox
import com.horizam.pro.elean.data.model.MembersInfo
import com.horizam.pro.elean.data.model.User
import com.horizam.pro.elean.data.model.response.Offer
import com.horizam.pro.elean.databinding.ItemInboxBinding
import com.horizam.pro.elean.databinding.ItemViewOfferBinding
import com.horizam.pro.elean.ui.main.callbacks.InboxHandler
import com.horizam.pro.elean.ui.main.callbacks.OnItemClickListener
import com.horizam.pro.elean.ui.main.callbacks.ViewOffersHandler
import com.horizam.pro.elean.utils.BaseUtils
import java.lang.Exception

class InboxAdapter(private val listener: InboxHandler) :
    PagingDataAdapter<Inbox, InboxAdapter.DataViewHolder>(ITEM_COMPARATOR) {

    private var myId: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val binding = ItemInboxBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DataViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }

    fun setMyId(id: Int) {
        myId = id
    }

    inner class DataViewHolder(private val binding: ItemInboxBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val item = fetchItem()
                if (item != null) {
                    listener.onItemClick(item)
                }
            }
        }

        private fun fetchItem(): Inbox? {
            val position = bindingAdapterPosition
            return if (position != RecyclerView.NO_POSITION) {
                getItem(position)
            } else {
                null
            }
        }

        fun bind(inbox: Inbox) {
//            binding.apply {
//                try {
//                    tvMessage.text = inbox.lastMessage
//                    tvLastMessage.text = BaseUtils.getTimeAgo(inbox.sentAt)
//                    tvCounter.isVisible = false
//                    if (myId != 0) {
//                        inbox.membersInfo.first {
//                            it.id != myId
//                        }.also { member ->
//                            tvUserName.text = member.name
//                            checkPhotoEmpty(member)
//                        }
//                    }
//                } catch (exception: Exception) {
//                    Log.i("Exception", exception.message.toString())
//                }
//            }
        }

        private fun ItemInboxBinding.checkPhotoEmpty(member: MembersInfo) {
            if (member.photo.isEmpty()) {
                ivInbox.setImageResource(R.color.colorAccent)
                tvLetter.isVisible = false
                tvLetter.text = member.name.first().toString()
            } else {
                Glide.with(itemView)
                    .load(Constants.BASE_URL.plus(member.photo))
                    .error(R.drawable.img_profile)
                    .into(ivInbox)
                tvLetter.isVisible = false
            }
        }
    }

    companion object {
        private val ITEM_COMPARATOR = object : DiffUtil.ItemCallback<Inbox>() {
            override fun areItemsTheSame(oldItem: Inbox, newItem: Inbox) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Inbox, newItem: Inbox) =
                oldItem == newItem
        }
    }

}