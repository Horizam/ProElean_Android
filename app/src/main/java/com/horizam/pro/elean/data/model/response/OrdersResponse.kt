package com.horizam.pro.elean.data.model.response

import com.google.gson.annotations.SerializedName

data class OrdersResponse(
    @SerializedName("status") var status: Int,
    @SerializedName("message") var message: String,
    @SerializedName("orderList") var orderList: List<Order>
)

data class Order(
    @SerializedName("seller_id") var sellerId: String,
    @SerializedName("buyer_id") var buyerId: String,
    @SerializedName("disputed_by") var disputedBy: String,
    @SerializedName("image") var image: String,
    @SerializedName("username") var username: String,
    @SerializedName("description") var description: String?,
    @SerializedName("service_id") var serviceId: String,
    @SerializedName("is_rated") var isRated: Int,
    @SerializedName("service_rating") var rating: Int,
    @SerializedName("job_id") var jobId: String,
    @SerializedName("amount") var amount: Double,
    @SerializedName("currency") var currency: String,
    @SerializedName("orderNo") var orderNo: String,
    @SerializedName("created_at") var createdAt: String,
    @SerializedName("end_date") var endDate: String,
    @SerializedName("type") var type: String,
    @SerializedName("status_id") var statusId: String,
    @SerializedName("watch") var watch: String,
    @SerializedName("notes") var notes: String,
    @SerializedName("started") var started: Int,
    @SerializedName("delivery_time") var deliveryTime: String,
    @SerializedName("delivery_note") var deliveryNote: String,
    @SerializedName("delivered_file") var deliveredFile: String,
    @SerializedName("revision") var revision: Int
)