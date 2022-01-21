package com.horizam.pro.elean.ui.main.viewmodel


data class FirebaseNotificationRequest(
    val subject: String,
    val reciever_id: String,
    val body: String,
    val data: NotificationMessage
)

data class NotificationMessage(
    val message: String,
    val type: String,
    val sender_id: String,
    val sender_name: String
)