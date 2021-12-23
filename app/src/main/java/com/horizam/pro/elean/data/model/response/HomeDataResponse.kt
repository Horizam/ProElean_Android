package com.horizam.pro.elean.data.model.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class HomeDataResponse(
    @SerializedName("data") val data : HomeData
)

data class HomeData (
    @SerializedName("categories") val categories : List<Category>?,
    @SerializedName("featGigs") val featuredGig : List<FeaturedGig>?
)

@Parcelize
data class Category (
    @SerializedName("id") val id : String,
    @SerializedName("title") val title : String,
    @SerializedName("description") val description : String,
    @SerializedName("banner") val banner : String,
): Parcelable

data class FeaturedGig (
    @SerializedName("id") val id : String,
    @SerializedName("user_id") val user_id : String,
    @SerializedName("sub_category_id") val sub_category_id : String,
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