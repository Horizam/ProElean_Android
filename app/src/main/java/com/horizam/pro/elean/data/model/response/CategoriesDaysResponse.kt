package com.horizam.pro.elean.data.model.response

import com.google.gson.annotations.SerializedName

data class CategoriesDaysResponse(
    @SerializedName("status") val status : Int,
    @SerializedName("message") val message : String,
//    @SerializedName("categories") val categories : List<SpinnerCategories>,
    @SerializedName("days") val days : List<String>,
    @SerializedName("revision") val noOfRevision : List<String>
)