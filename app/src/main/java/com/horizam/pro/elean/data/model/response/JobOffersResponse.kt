package com.horizam.pro.elean.data.model.response

import com.google.gson.annotations.SerializedName

data class JobOffersResponse(
    @SerializedName("data") val offerList: List<Offer>,
    @SerializedName("links") val links: Links,
    @SerializedName("meta") val meta: Meta
)

data class Profile(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("username") val username: String,
    @SerializedName("image") val image: String,
    @SerializedName("isFreelancer") val isFreelancer: Int,
    @SerializedName("created_at") val created_at: String,
    @SerializedName("address") val address: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("email") val email: String,
    @SerializedName("description") val description: String,
    @SerializedName("total_reviews") val total_reviews: Int,
    @SerializedName("user_rating") val user_rating: Int,
    @SerializedName("recent_delivery") val recent_delivery: String,
    @SerializedName("user_languages") val user_languages: List<String>,
    @SerializedName("user_skills") val user_skills: List<String>
)

data class Offer(
    @SerializedName("id") val id: String,
    @SerializedName("description") val description: String,
    @SerializedName("delivery_time") val delivery_time: String,
    @SerializedName("price") val price: Int,
    @SerializedName("profile") val profile: Profile
)