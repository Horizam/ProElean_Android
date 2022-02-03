package com.horizam.pro.elean.data.model.response

data class EarningsResponse(
    val data : EarningsData
)

data class EarningsData(
    val weekly_earning : Double,
    val year_earning : Double,
    val monthly_earning : Double,
    val total_earning : Double,
    val current_balance : Double
)