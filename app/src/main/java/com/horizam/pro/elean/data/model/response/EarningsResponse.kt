package com.horizam.pro.elean.data.model.response

import com.google.gson.annotations.SerializedName

data class EarningsResponse(
    @SerializedName("status") var status : Int,
    @SerializedName("message") var message : String,
    @SerializedName("sellerEarning") var sellerEarning : SellerEarning
)

data class SellerEarning (
    @SerializedName("total_orders") var totalOrders : Int,
    @SerializedName("avg_earning") var avgEarning : Double,
    @SerializedName("total_earning") var totalEarning : Int,
    @SerializedName("last_month_earn") var lastMonthEarn : Double
)
