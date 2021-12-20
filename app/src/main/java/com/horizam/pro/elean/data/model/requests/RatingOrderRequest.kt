package com.horizam.pro.elean.data.model.requests

data class RatingOrderRequest(
    val type: Int,
    val order_no: String,
    val description: String,
    val rating: Float
)