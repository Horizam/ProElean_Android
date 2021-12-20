package com.horizam.pro.elean.ui.main.callbacks

import com.horizam.pro.elean.data.model.response.Subcategory

interface BuyerRequestsHandler {
    fun <T>cancelOffer(item: T)
    fun <T>sendOffer(item: T)
}