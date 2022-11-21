package com.horizam.pro.elean.data.model

data class Inbox(
    val membersInfo: List<MembersInfo> = ArrayList(),
    val members: MutableList<String> = ArrayList(),
    val createdBy: String = "",
    val title: String = "",
    val lastMessage: String = "",
    val senderId: String = "",
    val sentAt: Long = 0,
    val senderName: String = "",
    val createdAt: Long = 0,
    val id: String = "",
    val combinedId: String = "",
    val lastMessageId: String = ""
)

data class MembersInfo(
    val id: String = "",
    var hasReadLastMessage: Boolean = true,
    var type: String = "",
    var photo: String = "",
    var name: String = "",
)