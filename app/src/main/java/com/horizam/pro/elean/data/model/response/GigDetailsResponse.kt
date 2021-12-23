package com.horizam.pro.elean.data.model.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class GigDetailsResponse(
    @SerializedName("status") val status: Int,
    @SerializedName("message") val message: String,
    @SerializedName("ServiceInfo") val serviceInfo: ServiceInfo,
    @SerializedName("days") val days: List<String>?
)

@Parcelize
data class ServiceInfo(
    @SerializedName("id") val id: String,
    @SerializedName("user_id") val user_id: String,
    @SerializedName("category_id") val categoryId: String,
    @SerializedName("sub_category_id") val subcategoryId: String,
    @SerializedName("s_description") val s_description: String,
    @SerializedName("uuid") val uuid: String,
    @SerializedName("description") val description: String,
    @SerializedName("category_title") val category_title: String,
    @SerializedName("sub_category_title") val sub_category_title: String,
    @SerializedName("service_rating") val rating: Int,
    @SerializedName("revision") val noOfRevision: Int,
    @SerializedName("additional_info") val additional_info: String,
    @SerializedName("price") val price: Double,
    @SerializedName("banner") val banner: String,
    @SerializedName("delivery_time") val delivery_time: String,
    @SerializedName("average_rating") val average_rating: Int,
    @SerializedName("user_service") val gigUser: GigUser,
    @SerializedName("service_media") val serviceMedia: List<ServiceMedia>,
    @SerializedName("reviews") val reviews: List<Review>
) : Parcelable

@Parcelize
data class GigUser(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("image") val image: String,
    @SerializedName("is_applied") val isApplied: Int
) : Parcelable

@Parcelize
data class ReviewUser(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("image") val image: String
) : Parcelable

@Parcelize
data class Review(
    @SerializedName("id") val id: String,
    @SerializedName("service_id") val service_id: String,
    @SerializedName("user_id") val user_id: String,
    @SerializedName("rating") val rating: Int,
    @SerializedName("description") val description: String,
    @SerializedName("user") val reviewUser: ReviewUser
) : Parcelable

@Parcelize
data class ServiceMedia(
    @SerializedName("service_id") val service_id: String,
    @SerializedName("media") val media: String,
) : Parcelable