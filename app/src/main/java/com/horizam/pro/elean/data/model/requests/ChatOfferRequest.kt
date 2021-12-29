package com.horizam.pro.elean.data.model.requests

data class ChatOfferRequest(
    val service_id: String,
    val description: String,
    val price: Double,
    val revision: String,
    val delivery_time: String,
    val token: String,
)