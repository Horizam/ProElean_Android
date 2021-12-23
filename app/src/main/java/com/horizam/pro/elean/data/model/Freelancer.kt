package com.horizam.pro.elean.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
data class Freelancer(
    val username: String,
    val shortDescription: String,
    val description: String,
    val countryId: String,
    val categoryId: String,
    val subcategoryId: String,
): Parcelable