package com.horizam.pro.elean.data.model.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class HomeDataResponse(
    @SerializedName("data") val data: HomeData
)

data class HomeData(
    @SerializedName("categories") val categories: List<Category>?,
    @SerializedName("ads") val ads: List<Ads>,
    @SerializedName("featGigs") val featuredGig: List<FeaturedGig>?
)

@Parcelize
data class Ads(
    @SerializedName("id") val id: String,
    @SerializedName("banner") val banner: String
) : Parcelable

@Parcelize
data class Category(
    @SerializedName("id"             ) var id            : String,
    @SerializedName("title"          ) var title         : String,
    @SerializedName("fi_title"       ) var fiTitle       : String,
    @SerializedName("slug"           ) var slug          : String,
    @SerializedName("banner"         ) var banner        : String,
    @SerializedName("description"    ) var description   : String,
    @SerializedName("fi_description" ) var fiDescription : String
) : Parcelable

@Parcelize
data class FeaturedGig(
    @SerializedName("id") val id: String = "",
    @SerializedName("s_description") val s_description: String,
    @SerializedName("description") val description: String,
    @SerializedName("price") val price: Double,
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
    @SerializedName("offered_services" ) var offeredServices : ArrayList<String>       = arrayListOf()
) : Parcelable