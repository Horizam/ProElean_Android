package com.horizam.pro.elean.data.model.response

import com.google.gson.annotations.SerializedName

data class OrderTimelineResponse(
    @SerializedName("status") var status : Int,
    @SerializedName("message") var message : String,
    @SerializedName("activityList") var activityList : List<Action>
)

data class Action (
    @SerializedName("created_at") var createdAt : String,
    @SerializedName("description") var description : String
)