package com.horizam.pro.elean.data.model

import com.google.gson.annotations.SerializedName

data class AnalyticModel(
    @SerializedName("weekly_earning"    ) var weeklyEarning    : Int?                 = null,
    @SerializedName("year_earning"      ) var yearEarning      : Int?                 = null,
    @SerializedName("monthly_earning"   ) var monthlyEarning   : Int?                 = null,
    @SerializedName("total_earning"     ) var totalEarning     : Int?                 = null,
    @SerializedName("current_balance"   ) var currentBalance   : Double?              = null,
    @SerializedName("active_orders"     ) var activeOrders     : Int?                 = null,
    @SerializedName("payments_enabled"  ) var paymentsEnabled  : Boolean?             = null,
    @SerializedName("analytics"         ) var analytics        : ArrayList<Analytics> = arrayListOf(),
    @SerializedName("total_impressions" ) var totalImpressions : Int?                 = null,
    @SerializedName("total_clicks"      ) var totalClicks      : Int? = null,

)
data class Analytics (
    @SerializedName("impressions" ) var impressions : Int?    = null,
    @SerializedName("clicks"      ) var clicks      : Int?    = null,
    @SerializedName("date"        ) var date        : String? = null,
    @SerializedName("day"         ) var day         : String? = null
)
