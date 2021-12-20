package com.horizam.pro.elean.ui.main.callbacks

interface GenericHandler {
    fun showProgressBar(show:Boolean = false)
    fun showMessage(message:String)
    fun showNoInternet(show:Boolean = false)
}