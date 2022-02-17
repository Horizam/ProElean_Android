package com.horizam.pro.elean.data.model.response

import com.google.gson.annotations.SerializedName

data class BuyerRequestsResponse(
    @SerializedName("data") val buyerRequestList : List<BuyerRequest>,
    @SerializedName("links") val links : Links,
    @SerializedName("meta") val meta : Meta
)

data class BuyerRequest(
    @SerializedName("id") val id : String,
    @SerializedName("user_id") val user_id : String,
    @SerializedName("category_id") val category_id : String,
    @SerializedName("sub_category_id") val sub_category_id : String,
    @SerializedName("description") val description : String,
    @SerializedName("budget") val budget : Double,
    @SerializedName("delivery_time") val delivery_time : String,
    @SerializedName("cinic") val cinic : String,
    @SerializedName("featured") val featured : Int,
    @SerializedName("latitude") val latitude : Int,
    @SerializedName("longitude") val longitude : Int,
    @SerializedName("total_offers") val total_offers : Int,
    @SerializedName("is_applied") val is_applied : Int,
    @SerializedName("created_at") val created_at : String,
    @SerializedName("user") val user : Profile
)