package com.horizam.pro.elean.data.model.response

import com.google.gson.annotations.SerializedName

data class OrderResponse(
    @SerializedName("status") var status: Int,
    @SerializedName("message") var message: String,
    @SerializedName("order") var order: Order
)