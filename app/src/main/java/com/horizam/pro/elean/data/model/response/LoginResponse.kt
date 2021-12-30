package com.horizam.pro.elean.data.model.response

data class LoginResponse(
    val data: Data,
    val message: String,
    val token: String
)

data class Data(
    val id : String,
    val email : String,
    val name : String,
    val username : String,
    val image : String,
    val isFreelancer : Int,
    val phone : String,
    val address : String,
    val city : String,
    val rating : Int,
    val total_reviews : Int,
    val availabe_balance : Int,
    val pending_balance : Int,
    val withdraw_balance : Int,
    val expected_balance : Int
)