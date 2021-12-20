package com.horizam.pro.elean.data.model.requests

import okhttp3.MultipartBody
import okhttp3.RequestBody

data class BecomeFreelancerRequest (
    val partMap: HashMap<String, RequestBody>,
    val file: MultipartBody.Part
    )