package com.horizam.pro.elean.ui.main.viewmodel


data class FirebaseNotificationRequest(
    val subject: String,
    val receiver_id: String,
    val data: NotificationMessage
)

data class NotificationMessage(
    val message: String
)