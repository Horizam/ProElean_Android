package com.horizam.pro.elean.data.model.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class HomeDataResponse(
    @SerializedName("data") val data: HomeData
)

data class HomeData(
    @SerializedName("categories") val categories: List<Category>?,
    @SerializedName("featGigs") val featuredGig: List<FeaturedGig>?,
    @SerializedName("ads") val ads: List<Ads>,
)

@Parcelize
data class Ads(
    @SerializedName("id") val id: String,
    @SerializedName("banner") val banner: String
) : Parcelable

@Parcelize
data class Category(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("banner") val banner: String,
) : Parcelable

@Parcelize
data class FeaturedGig(
    @SerializedName("id") val id: String = "",
    @SerializedName("user_id") val user_id: String,
    @SerializedName("category_id") val category_id: String,
    @SerializedName("sub_category_id") val sub_category_id: String,
    @SerializedName("s_description") val s_description: String,
    @SerializedName("description") val description: String,
    @SerializedName("price") val price: Int,
    @SerializedName("favourite") val favourite: Int,
    @SerializedName("service_rating") val service_rating: Int,
    @SerializedName("lat") val lat: Double,
    @SerializedName("lng") val lng: Double,
    @SerializedName("revision") val revision: Int,
    @SerializedName("total_orders") val total_orders: Int,
    @SerializedName("total_reviews") val total_reviews: Int,
    @SerializedName("total_clicks") val total_clicks: Int,
    @SerializedName("delivery_time") val delivery_time: String,
    @SerializedName("additional_info") val additional_info: String,
    @SerializedName("category") val category: Category,
    @SerializedName("sub_category") val sub_category: Subcategory,
    @SerializedName("service_user") val service_user: ServiceUser,
    @SerializedName("service_media") val service_media: List<Service_media>,
    @SerializedName("service_reviews") val service_reviews: List<String>
) : Parcelable