package com.horizam.pro.elean.data.model.requests

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.RequestBody

data class SellerActionRequestMultipart(
    val description: String,
    val image: MultipartBody.Part,
    val typeUser: RequestBody,
    val orderNumber: RequestBody
)