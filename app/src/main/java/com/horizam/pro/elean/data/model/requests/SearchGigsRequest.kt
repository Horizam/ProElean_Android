package com.horizam.pro.elean.data.model.requests

data class SearchGigsRequest(
    val query:String,
    val distance:String,
    val filter:String,
    val filterValue:String
)