package com.horizam.pro.elean.data.model.requests

data class ForgetChangePasswordRequest(
    val email: String = "",
    val token: String = "",
    val password: String = "",
    val password_confirmation: String = ""
)