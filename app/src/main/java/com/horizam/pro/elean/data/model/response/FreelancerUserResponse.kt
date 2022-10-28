package com.horizam.pro.elean.data.model.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.horizam.pro.elean.data.model.Analytics
import kotlinx.android.parcel.Parcelize

data class FreelancerUserResponse(
    @SerializedName("status") val status: Int,
    @SerializedName("message") val message: String,
    @SerializedName("ProfileInfo") val profileInfo: ProfileInfo,
    @SerializedName("user_services") val user_services: List<User_services>,
    @SerializedName("service_reviews") val service_reviews: List<ServiceReviews>
)

@Parcelize
data class ProfileInfo(

    @SerializedName("id"                ) var id               : String?           = null,
    @SerializedName("name"              ) var name             : String?           = null,
    @SerializedName("username"          ) var username         : String?           = null,
    @SerializedName("image"             ) var image            : String?           = null,
    @SerializedName("isFreelancer"      ) var isFreelancer     : Int?              = null,
    @SerializedName("created_at"        ) var createdAt        : String?           = null,
    @SerializedName("address"           ) var address          : String?           = null,
    @SerializedName("phone"             ) var phone            : String?           = null,
    @SerializedName("email"             ) var email            : String?           = null,
    @SerializedName("description"       ) var description      : String?           = null,
    @SerializedName("total_reviews"     ) var totalReviews     : Int?              = null,
    @SerializedName("user_rating"       ) var userRating       : Int?              = null,
    @SerializedName("recent_delivery"   ) var recentDelivery   : String?           = null,
    @SerializedName("user_languages"    ) var userLanguages    : String?           = null,
    @SerializedName("languages"         ) var languages        : ArrayList<String> = arrayListOf(),
    @SerializedName("user_skills"       ) var userSkills       : ArrayList<String> = arrayListOf(),
    @SerializedName("active_orders"     ) var activeOrders     : Int?              = null,
    @SerializedName("monthly_selling"   ) var monthlySelling   : Int?              = null,
    @SerializedName("weekly_clicks"     ) var weeklyClicks     : Int?              = null,
    @SerializedName("weekly_impression" ) var weeklyImpression : Int?              = null,
    @SerializedName("analytics"         ) var analytics        : ArrayList<String> = arrayListOf(),
    @SerializedName("average_selling"   ) var averageSelling   : Int?              = null,
    @SerializedName("total_services"    ) var totalServices    : Int?              = null,
    @SerializedName("payments_enabled"  ) var paymentsEnabled  : Boolean?          = null
) : Parcelable

@Parcelize
data class Service_media(

    @SerializedName("id") val id: String,
    @SerializedName("service_id") val service_id: String,
    @SerializedName("media") val media: String
) : Parcelable

@Parcelize
data class ServiceReviews(
    @SerializedName("id") val id: String,
    @SerializedName("rating") val rating: Int,
    @SerializedName("description") val description: String,
    @SerializedName("user") val user: ProfileInfo
) : Parcelable

data class ServiceReviewsResponse(
    @SerializedName("data") val data: List<ServiceReviews>,
    @SerializedName("links") val links: Links,
    @SerializedName("meta") val meta: Meta
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