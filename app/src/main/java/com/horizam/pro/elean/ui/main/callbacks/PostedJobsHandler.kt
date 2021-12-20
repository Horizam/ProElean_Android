package com.horizam.pro.elean.ui.main.callbacks

import com.horizam.pro.elean.data.model.response.Subcategory

interface PostedJobsHandler {
    fun <T>deleteItem(item: T)
    fun <T>viewOffers(item: T)
}