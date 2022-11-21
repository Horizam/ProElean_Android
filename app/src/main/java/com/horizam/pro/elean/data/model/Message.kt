package com.horizam.pro.elean.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class Message(
    val attachment: String = "",
    val message: String = "",
    val senderId: String = "",
    val sentAt: Long = 0,
    val attachmentType: Int = 0,
    val id: String = "",
    val refersGig: Boolean = false,
    val deleteMessage: List<Int> = ArrayList(),
    val messageOffer: MessageOffer? = null,
    val messageGig: MessageGig? = null,
)

data class MessageOffer(
    val serviceId: String = "",
    val offerSenderId: String = "",
    val status: Int = 0,
    val serviceTitle: String = "",
    val description: String = "",
    val deliveryDays: String = "",
    val revisions: String = "",
    val totalOffer: Double = 0.0
)@Parcelize
data class MessageGig(
    var gigId: String = "",
    var gigImage: String = "",
    var gigTitle: String = "",
    var gigUsername: String = "",
) : Parcelable