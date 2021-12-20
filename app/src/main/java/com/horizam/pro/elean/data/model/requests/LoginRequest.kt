package com.horizam.pro.elean.data.model.requests

data class LoginRequest (
    val email:String,
    val password:String,
    val fcm_token:String,
        )