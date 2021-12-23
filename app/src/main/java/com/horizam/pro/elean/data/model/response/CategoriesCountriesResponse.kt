package com.horizam.pro.elean.data.model.response

import com.google.gson.annotations.SerializedName

data class CategoriesCountriesResponse(
    @SerializedName("data") val categoriesCountriesData: CategoriesCountriesData
)

data class CategoriesCountriesData(
    @SerializedName("countries") val countries: List<Countries>,
    @SerializedName("categories") val categories: List<Categories>,
    @SerializedName("languages") val languages: List<String>,
    @SerializedName("featGigs") val featGigs: List<String>,
    @SerializedName("ads") val ads: List<String>,
    @SerializedName("delivery_days") val deliveryDays: List<String>,
    @SerializedName("revisions") val revisions: List<String>
)

data class Countries(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
)

data class Categories(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("banner") val banner: String,
    @SerializedName("description") val description: String
)