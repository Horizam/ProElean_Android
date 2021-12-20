package com.horizam.pro.elean.data.model.response

data class PostJobResponse(
    val job: Job,
    val message: String,
    val status: Int
)

data class Job(
    val budget: Int,
    val delivery_time: String,
    val description: String,
    val lat: Double,
    val lng: Double,
    val uuid: String
)