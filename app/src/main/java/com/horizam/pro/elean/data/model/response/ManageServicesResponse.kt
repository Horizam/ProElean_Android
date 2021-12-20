package com.horizam.pro.elean.data.model.response

import com.google.gson.annotations.SerializedName

data class ManageServicesResponse(
    @SerializedName("status") var status : Int,
    @SerializedName("message") var message : String,
    @SerializedName("service") var service : List<User_services>,
    @SerializedName("days") val days : List<String>?,
    @SerializedName("revision") val revisions : List<String>?
)
