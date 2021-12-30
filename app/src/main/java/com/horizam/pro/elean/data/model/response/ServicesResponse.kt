package com.horizam.pro.elean.data.model.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class ServicesResponse(
    @SerializedName("data") var serviceList: List<ServiceDetail>,
    @SerializedName("links") val links: Links,
    @SerializedName("meta") val meta: Meta
)

data class ServiceResponse(
    @SerializedName("data") var service: ServiceDetail,
)

@Parcelize
data class ServiceDetail(
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
    @SerializedName("service_reviews") val serviceReviewsList : List<ServiceReviews>
): Parcelable


@Parcelize
data class ServiceUser(
    @SerializedName("id") val id : String,
    @SerializedName("name") val name : String,
    @SerializedName("username") val username : String,
    @SerializedName("image") val image : String,
    @SerializedName("isFreelancer") val isFreelancer : Int,
    @SerializedName("created_at") val created_at : String,
    @SerializedName("address") val address : String,
    @SerializedName("phone") val phone : Int,
    @SerializedName("email") val email : String,
    @SerializedName("description") val description : String,
    @SerializedName("total_reviews") val total_reviews : Int,
    @SerializedName("user_rating") val user_rating : Int,
    @SerializedName("recent_delivery") val recent_delivery : String,
    @SerializedName("user_languages") val user_languages : List<String>,
    @SerializedName("user_skills") val user_skills : List<String>
): Parcelable
