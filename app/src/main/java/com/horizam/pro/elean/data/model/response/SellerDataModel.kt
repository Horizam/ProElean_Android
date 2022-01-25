package com.horizam.pro.elean.data.model.response

data class SellerDataModel(
    val id: String,
    val name: String,
    val username: String,
    val image: String,
    val isFreelancer: Int,
    val created_at: String,
    val address: String,
    val phone: String,
    val email: String,
    val description: String,
    val total_reviews: Int,
    val user_rating: Int,
    val recent_delivery: String,
    val user_languages: String,
    val user_skills: List<String>,
    val pending_balance: Double,
    val availabe_balance: Double,
    val cancelled_orders: Int,
    val cancelled_orders_balance: Double,
    val active_orders: Int,
    val active_orders_balance: Double,
    val monthly_selling: Double,
    val average_selling: Double
)