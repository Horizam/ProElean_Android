package com.horizam.pro.elean.ui.main.callbacks

import com.horizam.pro.elean.data.model.response.Subcategory

interface OnItemClickListener {
    fun <T>onItemClick(item: T)
}