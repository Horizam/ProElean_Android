package com.horizam.pro.elean.data.model.response

import com.google.gson.annotations.SerializedName

data class FeaturedGigDetailsResponse(
    @SerializedName("status") val status : Int,
    @SerializedName("message") val message : String,
    @SerializedName("service") val featuredGig : Service
)

data class Service (
    @SerializedName("id") val id : Int,
    @SerializedName("sub_category_id") val sub_category_id : Int,
    @SerializedName("user_id") val user_id : Int,
    @SerializedName("s_description") val s_description : String,
    @SerializedName("description") val description : String,
    @SerializedName("price") val price : Double,
    @SerializedName("additional_info") val additional_info : String,
    @SerializedName("sub_category") val sub_category : FeatureSubCategory,
    @SerializedName("user_service") val gigUser: GigUser,
    @SerializedName("service_media") val serviceMedia: List<ServiceMedia>
)

data class FeatureSubCategory (
    @SerializedName("id") val id : Int,
    @SerializedName("banner") val banner : String,
    @SerializedName("category_id") val category_id : Int,
    @SerializedName("category") val category : Category
)