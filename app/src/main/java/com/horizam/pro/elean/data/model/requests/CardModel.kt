package com.horizam.pro.elean.data.model.requests

data class CardModel(
    var number: String = "",
    var exp_month: Int = 0,
    var exp_year: Int = 0,
    var cvc: String = ""
)