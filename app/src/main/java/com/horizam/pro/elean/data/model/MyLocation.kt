package com.horizam.pro.elean.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MyLocation(
    val lat: Double,
    val long: Double
): Parcelable