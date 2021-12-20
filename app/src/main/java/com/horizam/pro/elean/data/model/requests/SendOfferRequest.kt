package com.horizam.pro.elean.data.model.requests

data class SendOfferRequest(
    val service_id: Int,
    val job_id: Int,
    val description: String,
    var price: Double,
    var delivery_time: String,
)