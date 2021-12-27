package com.horizam.pro.elean.data.model.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class FreelancerUserResponse(
    @SerializedName("status") val status: Int,
    @SerializedName("message") val message: String,
    @SerializedName("ProfileInfo") val profileInfo: ProfileInfo,
    @SerializedName("user_services") val user_services: List<User_services>,
    @SerializedName("service_reviews") val service_reviews: List<ServiceReviews>
)

data class ProfileInfo(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("username") val username: String,
    @SerializedName("image") val image: String,
    @SerializedName("isFreelancer") val isFreelancer: Int,
    @SerializedName("address") val address: String,
    @SerializedName("created_at") val created_at: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("email") val email: String,
    @SerializedName("description") val description: String,
    @SerializedName("total_reviews") val total_reviews: Int,
    @SerializedName("user_rating") val user_rating: Int,
    @SerializedName("recent_delivery") val recent_delivery: String,
    @SerializedName("user_languages") val user_languages: List<String>,
    @SerializedName("user_skills") val user_skills: List<String>
)

@Parcelize
data class Service_media(

    @SerializedName("id") val id: String,
    @SerializedName("service_id") val service_id: String,
    @SerializedName("media") val media: String
): Parcelable

data class ServiceReviews(
    @SerializedName("id") val id: String,
    @SerializedName("user_id") val user_id: String,
    @SerializedName("rating") val rating: Int,
    @SerializedName("description") val description: String,
    @SerializedName("user") val user: User
)

data class User(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("username") val username: String,
    @SerializedName("image") val image: String,
    @SerializedName("isFreelancer") val isFreelancer: Int,
    @SerializedName("address") val address: String,
    @SerializedName("phone") val phone: Int,
    @SerializedName("email") val email: String,
    @SerializedName("description") val description: String,
    @SerializedName("total_reviews") val total_reviews: Int,
    @SerializedName("average_rating") val average_rating: Int,
    @SerializedName("recent_delivery") val recent_delivery: String
)

data class User_services(
    @SerializedName("id") val id: String,
    @SerializedName("user_id") val user_id: String,
    @SerializedName("uuid") val uuid: String,
    @SerializedName("category_id") val category_id: String,
    @SerializedName("sub_category_id") val sub_category_id: String,
    @SerializedName("s_description") val s_description: String,
    @SerializedName("description") val description: String,
    @SerializedName("price") val price: Double,
    @SerializedName("rating") val rating: Int,
    @SerializedName("orders") val orders: Int,
    @SerializedName("lat") val lat: Double,
    @SerializedName("lng") val lng: Double,
    @SerializedName("delivery_time") val delivery_time: String,
    @SerializedName("additional_info") val additional_info: String,
    @SerializedName("total_clicks") val total_clicks: Int,
    @SerializedName("reviews_count") val reviews_count: Int,
    @SerializedName("service_average") val service_average: Int,
    @SerializedName("service_media") val service_media: List<Service_media>
)