package com.horizam.pro.elean.ui.main.callbacks

import com.horizam.pro.elean.data.model.requests.BuyerActionsRequest

interface GenericHandler {
    fun showProgressBar(show: Boolean = false)
    fun showErrorMessage(message: String)
    fun showSuccessMessage(message: String)
    fun showNoInternet(show: Boolean = false)
}