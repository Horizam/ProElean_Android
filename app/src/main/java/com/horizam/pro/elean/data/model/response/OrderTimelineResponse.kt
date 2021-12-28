package com.horizam.pro.elean.data.model.response

import com.google.gson.annotations.SerializedName

data class OrderTimelineResponse(
    @SerializedName("data") val actionList : List<Action>,
    @SerializedName("links") val links : Links,
    @SerializedName("meta") val meta : Meta
)

data class Action (
    @SerializedName("description") val description : String,
    @SerializedName("created_at") val created_at : String
)