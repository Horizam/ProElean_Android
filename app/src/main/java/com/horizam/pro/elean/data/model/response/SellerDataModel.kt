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
    val user_languages: List<String>,
    val user_skills: List<String>,
    val pending_balance: Int,
    val availabe_balance: Int,
    val cancelled_orders: Int,
    val cancelled_orders_balance: Int,
    val active_orders: Int,
    val active_orders_balance: Int,
    val monthly_selling: Int,
    val average_selling: Int
)