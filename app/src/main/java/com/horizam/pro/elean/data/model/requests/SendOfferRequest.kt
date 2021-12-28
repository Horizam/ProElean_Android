package com.horizam.pro.elean.data.model.requests

data class SendOfferRequest(
    val service_id: String,
    val job_id: String,
    val description: String,
    var price: Double,
    var delivery_time: String,
)