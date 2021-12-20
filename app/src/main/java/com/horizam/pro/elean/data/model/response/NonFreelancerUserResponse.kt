package com.horizam.pro.elean.data.model.response

import com.google.gson.annotations.SerializedName

data class NonFreelancerUserResponse(
    @SerializedName("status") val status : Int,
    @SerializedName("message") val message : String,
    @SerializedName("UserProfile") val userProfile : UserProfile
)

data class UserProfile (
    @SerializedName("id") val id : Int,
    @SerializedName("name") val name : String,
    @SerializedName("username") val username : String,
    @SerializedName("isFreelancer") val isFreelancer : Int,
    @SerializedName("image") val image : String,
    @SerializedName("address") val address : String,
    @SerializedName("phone") val phone : String,
    @SerializedName("email") val email : String,
    @SerializedName("description") val description : String,
    @SerializedName("created_at") val created_at : String,
    @SerializedName("total_reviews") val total_reviews : Int,
    @SerializedName("average_rating") val average_rating : Int,
    @SerializedName("recent_delivery") val recent_delivery : String,
    @SerializedName("user_languages") val user_languages : List<String>,
    @SerializedName("user_skills") val user_skills : List<String>
)