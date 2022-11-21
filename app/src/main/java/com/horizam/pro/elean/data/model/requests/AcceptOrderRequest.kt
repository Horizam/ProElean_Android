package com.horizam.pro.elean.data.model.requests

data class AcceptOrderRequest(
    val service_id: String,
    var token: String,
    var deliveryTime:String,
    var price:String,
    var revision:String

)