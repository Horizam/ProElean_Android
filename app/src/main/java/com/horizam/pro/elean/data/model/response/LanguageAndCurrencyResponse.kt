package com.horizam.pro.elean.data.model.response

import com.google.gson.annotations.SerializedName

data class LanguageAndCurrencyResponse(
    @SerializedName("status") var status: Int,
    @SerializedName("message") var message: String,
    @SerializedName("data") var languageAndCurrencyData: LanguageAndCurrencyData
)

data class LanguageAndCurrencyData (
    @SerializedName("languages"  ) var languages  : List<String>,
    @SerializedName("currencies" ) var currencies : List<String>
)