package com.horizam.pro.elean.ui.main.viewmodel

import com.horizam.pro.elean.data.model.Message

data class FirebaseNotification(
    private var to: String,
    private var data: NotificationMessage
)

data class NotificationMessage(
    private var senderId: Int,
    private var senderName: String,
    private var message: String,
    private var type: String
)