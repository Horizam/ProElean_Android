package com.horizam.pro.elean.data.model.response

import com.google.gson.annotations.SerializedName

data class OrderResponse(
    @SerializedName("data") val order : Order
)