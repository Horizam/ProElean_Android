package com.horizam.pro.elean.ui.main.callbacks

interface ManageServiceHandler {
     fun <T>removeService(item: T)
     fun <T>onItemClick(item: T)
}