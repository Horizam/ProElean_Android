package com.horizam.pro.elean.data.model.requests

data class PostJobRequest (
    val category_id:String,
    val sub_category_id:String,
    val description:String,
    val delivery_time:String,
    val budget:Double,
    )