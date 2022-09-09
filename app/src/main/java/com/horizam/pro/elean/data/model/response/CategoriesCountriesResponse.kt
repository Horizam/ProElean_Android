package com.horizam.pro.elean.data.model.response

import com.google.gson.annotations.SerializedName

data class CategoriesCountriesResponse(
    @SerializedName("data") val categoriesCountriesData: CategoriesCountriesData
)

data class CategoriesCountriesData(
    @SerializedName("countries"     ) var countries    : ArrayList<Countries>  = arrayListOf(),
    @SerializedName("categories"    ) var categories   : ArrayList<Categories> = arrayListOf(),
    @SerializedName("featGigs"      ) var featGigs     : ArrayList<String>     = arrayListOf(),
    @SerializedName("ads"           ) var ads          : ArrayList<String>     = arrayListOf(),
    @SerializedName("languages"     ) var languages    : ArrayList<Languages>  = arrayListOf(),
    @SerializedName("delivery_days" ) var deliveryDays : ArrayList<String>     = arrayListOf(),
    @SerializedName("revisions"     ) var revisions    : ArrayList<String>     = arrayListOf()
)
data class Countries(
    @SerializedName("id"        ) var id        : String? = null,
    @SerializedName("name"      ) var name      : String? = null,
    @SerializedName("image"     ) var image     : String? = null,
    @SerializedName("phonecode" ) var phonecode : String? = null
)
data class Languages (

    @SerializedName("id"        ) var id       : Int?    = null,
    @SerializedName("language"  ) var language : String? = null,
    @SerializedName("fin_trans" ) var finTrans : String? = null

)
data class Categories(
    @SerializedName("id"             ) var id            : String? = null,
    @SerializedName("title"          ) var title         : String? = null,
    @SerializedName("fi_title"       ) var fiTitle       : String? = null,
    @SerializedName("slug"           ) var slug          : String? = null,
    @SerializedName("banner"         ) var banner        : String? = null,
    @SerializedName("description"    ) var description   : String? = null,
    @SerializedName("fi_description" ) var fiDescription : String? = null
)