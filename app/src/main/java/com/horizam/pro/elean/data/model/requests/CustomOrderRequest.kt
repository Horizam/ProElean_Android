package com.horizam.pro.elean.data.model.requests

data class CustomOrderRequest(
    val service_id: String,
    val description: String,
    var token: String
)