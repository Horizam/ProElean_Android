package com.horizam.pro.elean.data.model.response

import com.google.gson.annotations.SerializedName

data class NotificationsResponse(
    @SerializedName("data") val data : List<Notification>,
    @SerializedName("links") val links : Links,
    @SerializedName("meta") val meta : Meta
)

data class Notification(
    @SerializedName("id") val id : String,
    @SerializedName("sender_id") val sender_id : String,
    @SerializedName("sender_pic") val sender_pic : String,
    @SerializedName("reciever_id") val reciever_id : String,
    @SerializedName("body") val body : String,
    @SerializedName("name") val name : String,
    @SerializedName("type") val type : String,
    @SerializedName("viewed") val viewed : Int,
    @SerializedName("content_id") val content_id : String,
    @SerializedName("created_at") val created_at : String
)