package com.horizam.pro.elean.data.model.requests

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.RequestBody

data class SellerActionRequestMultipart(
    val orderNumber: RequestBody,
    val typeUser: RequestBody,
    val deliveryNote: RequestBody,
    val image: MultipartBody.Part
)