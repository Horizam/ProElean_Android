package com.horizam.pro.elean.data.model.response

import com.google.gson.annotations.SerializedName

data class PrivacyPolicyResponse(
    @SerializedName("status") var status: Int,
    @SerializedName("message") var message: String,
    @SerializedName("data") var data: PrivacyTermsData
)

data class PrivacyTermsData(
    @SerializedName("privacy_policy") var privacyPolicy: PrivacyPolicy,
    @SerializedName("term_conditions") var termConditions: TermConditions
)

data class PrivacyPolicy(
    @SerializedName("id") var id: String,
    @SerializedName("description") var description: String,
    @SerializedName("created_at") var createdAt: String,
    @SerializedName("updated_at") var updatedAt: String
)

data class TermConditions(
    @SerializedName("id") var id: String,
    @SerializedName("description") var description: String,
    @SerializedName("created_at") var createdAt: String,
    @SerializedName("updated_at") var updatedAt: String
)