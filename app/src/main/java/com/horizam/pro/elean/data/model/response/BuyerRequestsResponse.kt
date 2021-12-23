package com.horizam.pro.elean.data.model.response

import com.google.gson.annotations.SerializedName

data class BuyerRequestsResponse(
    @SerializedName("status") var status: Int,
    @SerializedName("message") var message: String,
    @SerializedName("jobList") var buyerRequestsData: BuyerRequestsData
)

data class BuyerRequestsData(
    @SerializedName("current_page") var currentPage: Int,
    @SerializedName("data") var buyerRequestsList: List<BuyerRequest>,
    @SerializedName("first_page_url") var firstPageUrl: String,
    @SerializedName("from") var from: Int,
    @SerializedName("next_page_url") var nextPageUrl: String,
    @SerializedName("path") var path: String,
    @SerializedName("per_page") var perPage: Int,
    @SerializedName("prev_page_url") var prevPageUrl: String,
    @SerializedName("to") var to: Int
)

data class BuyerRequest(
    @SerializedName("id") var id: String,
    @SerializedName("user_id") var userId: String,
    @SerializedName("category_id") var categoryId: String,
    @SerializedName("sub_category_id") var subCategoryId: String,
    @SerializedName("description") var description: String,
    @SerializedName("budget") var budget: Double,
    @SerializedName("created_at") var createdAt: String,
    @SerializedName("delivery_time") var deliveryTime: String,
    @SerializedName("uuid") var uuid: String,
    @SerializedName("cinic") var cinic: String,
    @SerializedName("doc_type") var docType: String,
    @SerializedName("offers") var offers: Int,
    @SerializedName("status") var status: Int,
    @SerializedName("is_applied") var isApplied: Int,
    @SerializedName("user") var user: GigUser
)