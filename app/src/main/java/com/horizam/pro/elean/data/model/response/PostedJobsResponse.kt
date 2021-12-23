package com.horizam.pro.elean.data.model.response

import com.google.gson.annotations.SerializedName

data class PostedJobsResponse(
    @SerializedName("status") val status: Int,
    @SerializedName("message") val message: String,
    @SerializedName("jobList") val postedJobsData: PostedJobsData
)

data class PostedJob(
    @SerializedName("id") val id: String,
    @SerializedName("user_id") val user_id: String,
    @SerializedName("category_id") val category_id: String,
    @SerializedName("sub_category_id") val sub_category_id: String,
    @SerializedName("description") val description: String,
    @SerializedName("budget") val budget: Double,
    @SerializedName("created_at") val created_at: String,
    @SerializedName("delivery_time") val delivery_time: String,
    @SerializedName("uuid") val uuid: String,
    @SerializedName("offers") val offers: Int,
    @SerializedName("status") val status: Int,
)

data class PostedJobsData(
    @SerializedName("current_page") val current_page: Int,
    @SerializedName("data") val postedJobsList: List<PostedJob>,
    @SerializedName("first_page_url") val first_page_url: String,
    @SerializedName("from") val from: Int,
    @SerializedName("next_page_url") val next_page_url: String,
    @SerializedName("path") val path: String,
    @SerializedName("per_page") val per_page: Int,
    @SerializedName("prev_page_url") val prev_page_url: String,
    @SerializedName("to") val to: Int
)