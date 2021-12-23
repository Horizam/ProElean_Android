package com.horizam.pro.elean.data.model.response

import com.google.gson.annotations.SerializedName

data class SubcategoriesDataResponse(
    @SerializedName("data") val subcategoriesList: List<SubcategoriesData>,
    @SerializedName("links") val links: Links,
    @SerializedName("meta") val meta: Meta
)

data class SubcategoriesData (
    @SerializedName("id") val id : String,
    @SerializedName("title") val title : String,
    @SerializedName("category_id") val category_id : String,
    @SerializedName("banner") val banner : String
)