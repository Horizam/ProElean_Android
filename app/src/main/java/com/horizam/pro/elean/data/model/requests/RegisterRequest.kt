package com.horizam.pro.elean.data.model.requests

data class RegisterRequest (
    val email:String,
    val password:String,
    val password_confirmation:String,
    val username:String,
    val country:String? = null,
    val name:String
        )