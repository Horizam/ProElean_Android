package com.horizam.pro.elean.data.model.response

import com.google.gson.annotations.SerializedName

data class SellersResponse(
    val status: Int,
    val message: String,
    val data: SellerData
)

data class SellerData(
    val current_page: Int,
    val sellerList: List<Gig>,
    val first_page_url: String,
    val from: Int,
    val next_page_url: String,
    val path: String,
    val per_page: Int,
    val prev_page_url: String,
    val to: Int
)

data class Gig(
    val id: Int,
    val user_id: Int,
    val sub_category_id: Int,
    @SerializedName("s_description") val shortDescription: String,
    val description: String,
    val price: Double,
    val delivery_time: String,
    val additional_info: String,
    val uuid: String,
    val banner: String,
    val user_image: String,
    val username: String,
    val extension: String,
    val total_clicks: Int,
    val deleted: Int,
    val status: Int,
    val featured: Int,
    val favourite: Int,
    val rating: Int,
    val lat: Double?,
    val lng: Double?,
    val created_at: String,
    val updated_at: String
)