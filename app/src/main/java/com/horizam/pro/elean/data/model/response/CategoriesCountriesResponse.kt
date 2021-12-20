package com.horizam.pro.elean.data.model.response

import com.google.gson.annotations.SerializedName

data class CategoriesCountriesResponse(
    @SerializedName("status") val status: Int,
    @SerializedName("message") val message: String,
    @SerializedName("BecomeFreelancer") val becomeFreelancer: BecomeFreelancer
)

data class BecomeFreelancer(
    @SerializedName("categories") val categories: List<SpinnerCategories>,
    @SerializedName("countries") val countries: List<SpinnerCountries>,
    @SerializedName("languages") var languages : List<String>
)

data class SpinnerCategories(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("banner") val banner: String,
    @SerializedName("description") val description: String
)

data class SpinnerCountries(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String
)