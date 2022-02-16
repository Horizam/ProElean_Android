package com.horizam.pro.elean.data.model.response

data class UserReviewsResponse(
    val data: List<UserReview>,
    val avg_rating: String,
    val total_reviews: String,
    val links: Links,
    val meta: Meta
)

data class UserReview(
    val id: String,
    val user_name: String,
    val rating: Int,
    val comment: String,
    val profile: String
)