package com.horizam.pro.elean.data.model.response

import com.google.gson.annotations.SerializedName

data class NotificationsResponse(
    @SerializedName("status") var status: Int,
    @SerializedName("message") var message: String,
    @SerializedName("notifications") var notificationsList: List<Notification>
)

data class Notification(
    @SerializedName("sender_id") var senderId: Int,
    @SerializedName("id") var id: String,
    @SerializedName("name") var name: String,
    @SerializedName("body") var body: String,
    @SerializedName("created_at") var createdAt: String,
    @SerializedName("type") var type: Int
)