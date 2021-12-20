package com.horizam.pro.elean.data.model.response

import com.google.gson.annotations.SerializedName

data class JobOffersResponse(
    @SerializedName("status") var status: Int,
    @SerializedName("message") var message: String,
    @SerializedName("jobOffers") var jobOffers: JobOffers
)

data class Profile(
    @SerializedName("id") var id: Int,
    @SerializedName("name") var name: String,
    @SerializedName("image") var image: String,
    @SerializedName("average_rating") var averageRating: Int,
    @SerializedName("isFreelancer") var isFreelancer: Int,
)

data class Offer(
    @SerializedName("id") var id: Int,
    @SerializedName("user_id") var userId: Int,
    @SerializedName("description") var description: String,
    @SerializedName("price") var price: Double,
    @SerializedName("profile") var profile: Profile
)

data class JobOffers(
    @SerializedName("current_page") var currentPage: Int,
    @SerializedName("data") var offerList: List<Offer>,
    @SerializedName("first_page_url") var firstPageUrl: String,
    @SerializedName("from") var from: Int,
    @SerializedName("next_page_url") var nextPageUrl: String,
    @SerializedName("path") var path: String,
    @SerializedName("per_page") var perPage: Int,
    @SerializedName("prev_page_url") var prevPageUrl: String,
    @SerializedName("to") var to: Int
)