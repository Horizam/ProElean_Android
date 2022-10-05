package com.horizam.pro.elean.data.model.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SubcategoriesResponse (
    @SerializedName("data") val subcategoriesList : List<Subcategory>,
    @SerializedName("links") val links : Links,
    @SerializedName("meta") val meta : Meta
): Parcelable

@Parcelize
data class Subcategory(
    @SerializedName("id"          ) var id         : String,
    @SerializedName("title"       ) var title      : String,
    @SerializedName("fi_title"    ) var fiTitle    : String,
    @SerializedName("category_id" ) var categoryId : String,
    @SerializedName("banner"      ) var banner     : String,
    @SerializedName("slug"        ) var slug       : String
): Parcelable

@Parcelize
data class Links (
    @SerializedName("first") val first : String,
    @SerializedName("last") val last : String,
    @SerializedName("prev") val prev : String,
    @SerializedName("next") val next : String
): Parcelable

@Parcelize
data class Meta (
    @SerializedName("current_page") val current_page : Int,
    @SerializedName("from") val from : Int,
    @SerializedName("path") val path : String,
    @SerializedName("per_page") val per_page : Int,
    @SerializedName("to") val to : Int
): Parcelable