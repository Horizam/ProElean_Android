package com.horizam.pro.elean.data.model.response

import com.google.gson.annotations.SerializedName

data class SavedGigsResponse(
    @SerializedName("status") var status : Int,
    @SerializedName("message") var message : String,
    @SerializedName("data") var savedGigsData : SavedGigsData
)

data class SavedGig (
    @SerializedName("id") var id : String,
    @SerializedName("s_description") var sDescription : String,
    @SerializedName("description") var description : String,
    @SerializedName("additional_info") var additionalInfo : String,
    @SerializedName("uuid") var uuid : String,
    @SerializedName("price") var price : Double,
    @SerializedName("banner") var banner : String,
    @SerializedName("user_image") var userImage : String,
    @SerializedName("user_id") var userId : String,
    @SerializedName("user_rating") var userRating : Int
)

data class SavedGigsData (
    @SerializedName("current_page") var currentPage : Int,
    @SerializedName("wishlist") var savedGigList : List<SavedGig>,
    @SerializedName("first_page_url") var firstPageUrl : String,
    @SerializedName("from") var from : Int,
    @SerializedName("next_page_url") var nextPageUrl : String,
    @SerializedName("path") var path : String,
    @SerializedName("per_page") var perPage : Int,
    @SerializedName("prev_page_url") var prevPageUrl : String,
    @SerializedName("to") var to : Int
)