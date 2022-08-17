package com.horizam.pro.elean.ui.main.adapter

import android.graphics.Color
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.horizam.pro.elean.Constants
import com.horizam.pro.elean.R
import com.horizam.pro.elean.data.model.Inbox
import com.horizam.pro.elean.data.model.MembersInfo
import com.horizam.pro.elean.databinding.ItemInboxBinding
import com.horizam.pro.elean.ui.main.callbacks.InboxHandler
import com.horizam.pro.elean.utils.BaseUtils
import java.lang.Exception

class InboxAdapter(private val listener: InboxHandler) :
    PagingDataAdapter<Inbox, InboxAdapter.DataViewHolder>(ITEM_COMPARATOR) {

    private var myId: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val binding = ItemInboxBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DataViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null) {
            holder.bind(currentItem , holder)
        }
    }

    fun setMyId(id: String) {
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

        fun bind(inbox: Inbox, holder: DataViewHolder) {
            binding.apply {
                try {
                    for (memberInfo in inbox.membersInfo) {
                        if (memberInfo.id == myId) {
                            if (memberInfo.hasReadLastMessage) {
                                tvMessage.typeface = Typeface.DEFAULT
                            } else {
                                tvMessage.typeface = Typeface.DEFAULT_BOLD
                                tvMessage.setTextColor(Color.parseColor("#000000"))
                            }
                        }
                    }
                    if (inbox.senderId == myId) {
                        tvMessage.text = "me: ${inbox.lastMessage}"
                    } else {
                        tvMessage.text = "${inbox.lastMessage}"
                    }
                    tvLastMessage.text = BaseUtils.getTimeAgo(inbox.sentAt)
                    tvCounter.isVisible = false
                    if (myId != "") {
                        inbox.membersInfo.first {
                            it.id != myId
                        }.also { member ->
                            tvUserName.text = member.name
                            checkPhotoEmpty(member)
                        }
                    }
                } catch (exception: Exception) {
                    Log.i("Exception", exception.message.toString())
                }
            }
        }

        private fun ItemInboxBinding.checkPhotoEmpty(member: MembersInfo) {
            if (member.photo==null) {
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
                oldItem == newItem

            override fun areContentsTheSame(oldItem: Inbox, newItem: Inbox) =
                oldItem.id == newItem.id
        }
    }

}