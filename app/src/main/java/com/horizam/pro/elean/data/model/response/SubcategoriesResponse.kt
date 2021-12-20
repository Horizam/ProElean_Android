package com.horizam.pro.elean.data.model.response

import com.google.gson.annotations.SerializedName

data class SubcategoriesResponse(
    val status : Int,
    val message : String,
    val data : SubcategoriesData
)

data class SubcategoriesData (
    @SerializedName("current_page") val current_page : Int,
    @SerializedName("subCategories") val subCategoriesList : List<Subcategory>,
    @SerializedName("first_page_url") val first_page_url : String,
    @SerializedName("from") val from : Int,
    @SerializedName("next_page_url") val next_page_url : String,
    @SerializedName("path") val path : String,
    @SerializedName("per_page") val per_page : Int,
    @SerializedName("prev_page_url") val prev_page_url : String,
    @SerializedName("to") val to : Int
)

data class Subcategory(
    val category_id: Int,
    val created_at: String,
    val id: Int,
    val status: String,
    val banner: String,
    val title: String,
    val updated_at: String
)