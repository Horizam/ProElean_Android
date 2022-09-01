package com.horizam.pro.elean.data.model.requests

import com.google.gson.annotations.SerializedName

data class BuyerActionsRequest (
//    val order_no:String,
//    val type:Int,
  //  @SerializedName("order_id"      ) var order_id      : String?                 = null,
    @SerializedName("description"      ) var description     : String?                 = null,
)