package com.horizam.pro.elean.data.model.response

data class LoginResponse(
    val data: Data,
    val message: String,
    val status: Int,
    val token: String
)

data class Data(
    val active: String,
    val added_by: Int,
    val address: String,
    val city: String?,
    val created_at: String,
    val currency_id: String?,
    val device_id: String?,
    val email: String,
    val email_verified_at: String?,
    val fcm_token: String?,
    val id: Int,
    val image: String? = null,
    val latitude: Double?,
    val longitude: Double?,
    val name: String,
    val phone: String,
    val postal_code: String?,
    val referal_code: Int,
    val status: String,
    val updated_at: String,
    val username: String,
    val verified: String
)