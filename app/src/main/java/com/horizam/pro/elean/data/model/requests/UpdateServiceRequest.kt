package com.horizam.pro.elean.data.model.requests

import okhttp3.MultipartBody
import okhttp3.RequestBody

data class UpdateServiceRequest (
    val partMap: HashMap<String, RequestBody>,
    val images: ArrayList<MultipartBody.Part>,
    val deletedImages: ArrayList<String>,
    val id:Int
    )