package com.horizam.pro.elean.ui.main.callbacks

import com.horizam.pro.elean.data.model.response.Subcategory

interface ViewOffersHandler {
    fun <T>deleteItem(item: T)
    fun <T>viewProfile(item: T)
    fun <T>askQuestion(item: T)
    fun <T>order(item: T)
}