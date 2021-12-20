package com.horizam.pro.elean.data.model.response

import com.google.gson.annotations.SerializedName

data class SpinnerSubcategoriesResponse(
    @SerializedName("status") val status : Int,
    @SerializedName("message") val message : String,
    @SerializedName("data") val subcategories : List<SpinnerSubcategory>
)

data class SpinnerSubcategory (
    @SerializedName("id") val id : Int,
    @SerializedName("title") val title : String,
    @SerializedName("banner") val banner : String
)