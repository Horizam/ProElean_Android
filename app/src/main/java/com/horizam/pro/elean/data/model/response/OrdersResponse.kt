package com.horizam.pro.elean.data.model.response

import com.google.gson.annotations.SerializedName

data class OrdersResponse(
    @SerializedName("data") val orderList : List<Order>,
    @SerializedName("links") val links : Links,
    @SerializedName("meta") val meta : Meta
)

data class Order(
    @SerializedName("id") val id : String,
    @SerializedName("seller_id") val seller_id : String,
    @SerializedName("buyer_id") val buyer_id : String,
    @SerializedName("disputed_by") val disputed_by : String,
    @SerializedName("image") val image : String,
    @SerializedName("username") val username : String,
    @SerializedName("description") val description : String,
    @SerializedName("service_id") val service_id : String,
    @SerializedName("job_id") val job_id : String,
    @SerializedName("amount") val amount : Double,
    @SerializedName("service_rating") val service_rating : Int,
    @SerializedName("currency") val currency : String,
    @SerializedName("orderNo") val orderNo : String,
    @SerializedName("is_rated") val is_rated : Int,
    @SerializedName("type") val type : String,
    @SerializedName("status_id") val status_id : Int,
    @SerializedName("watch") val watch : String,
    @SerializedName("notes") val notes : String,
    @SerializedName("started") val started : Int,
    @SerializedName("created_at") val created_at : String,
    @SerializedName("end_date") val end_date : String,
    @SerializedName("delivery_time") val delivery_time : String,
    @SerializedName("delivery_note") val delivery_note : String,
    @SerializedName("delivered_file") val delivered_file : String,
    @SerializedName("revision") val revision : Int
)