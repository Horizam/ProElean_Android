package com.horizam.pro.elean.data.model.response

import com.google.gson.annotations.SerializedName

data class SubcategoriesDataResponse(
    @SerializedName("data") val subcategoriesList: List<SubcategoriesData>,
    @SerializedName("links") val links: Links,
    @SerializedName("meta") val meta: Meta
)

data class SubcategoriesData (
    @SerializedName("id"          ) var id         : String,
    @SerializedName("title"       ) var title      : String,
    @SerializedName("fi_title"    ) var fiTitle    : String,
    @SerializedName("category_id" ) var categoryId : String,
    @SerializedName("banner"      ) var banner     : String,
    @SerializedName("slug"        ) var slug       : String
)