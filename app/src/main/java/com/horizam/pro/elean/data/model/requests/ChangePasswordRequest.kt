package com.horizam.pro.elean.data.model.requests

data class ChangePasswordRequest (
    val current_password: String,
    val password: String,
    val password_confirmation: String
)