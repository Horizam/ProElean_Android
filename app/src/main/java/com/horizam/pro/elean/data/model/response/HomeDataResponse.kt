package com.horizam.pro.elean.data.model.response

import com.google.gson.annotations.SerializedName

data class HomeDataResponse(
    @SerializedName("status") val status : Int,
    @SerializedName("message") val message : String,
    @SerializedName("data") val data : HomeData
)

data class HomeData (
    @SerializedName("categories") val categories : List<Category>?,
    @SerializedName("user_info") val user : AppUser,
    @SerializedName("ads") val ads : List<Ads>?,
    @SerializedName("featgig") val featuredGig : List<FeaturedGig>?
)

data class AppUser(
    @SerializedName("id") val id: Int,
    @SerializedName("isFreelancer") val isFreelancer: Int,
    @SerializedName("username") val name: String,
    @SerializedName("image") val image: String
)

data class Category (
    @SerializedName("id") val id : Int,
    @SerializedName("title") val title : String,
    @SerializedName("description") val description : String,
    @SerializedName("banner") val banner : String,
    @SerializedName("status") val status : Int,
    @SerializedName("created_at") val created_at : String,
    @SerializedName("updated_at") val updated_at : String
)

data class FeaturedGig (
    @SerializedName("id") val id : Int,
    @SerializedName("user_id") val user_id : Int,
    @SerializedName("sub_category_id") val sub_category_id : Int,
    @SerializedName("s_description") val shortDescription : String,
    @SerializedName("description") val description : String,
    @SerializedName("price") val price : Double,
    @SerializedName("delivery_time") val delivery_time : String,
    @SerializedName("additional_info") val additional_info : String,
    @SerializedName("uuid") val uuid : String,
    @SerializedName("banner") val banner : String,
    @SerializedName("extension") val extension : String,
    @SerializedName("total_clicks") val total_clicks : Int,
    @SerializedName("deleted") val deleted : Int,
    @SerializedName("status") val status : Int,
    @SerializedName("featured") val featured : Int,
    @SerializedName("rating") val rating : Int,
    @SerializedName("lat") val lat : Double?,
    @SerializedName("lng") val lng : Double?,
    @SerializedName("created_at") val created_at : String,
    @SerializedName("updated_at") val updated_at : String
)

data class Ads (
    @SerializedName("id") val id : Int,
    @SerializedName("user_id") val user_id : Int,
    @SerializedName("banner") val banner : String,
    @SerializedName("description") val description : String,
    @SerializedName("created_at") val created_at : String,
    @SerializedName("updated_at") val updated_at : String
)