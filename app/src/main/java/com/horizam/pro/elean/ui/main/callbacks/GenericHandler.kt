package com.horizam.pro.elean.ui.main.callbacks

interface GenericHandler {
    fun showProgressBar(show:Boolean = false)
    fun showErrorMessage(message:String)
    fun showSuccessMessage(message:String)
    fun showNoInternet(show:Boolean = false)
}