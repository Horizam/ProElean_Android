package com.horizam.pro.elean.ui.main.callbacks

interface SavedGigsHandler {
     fun <T>addRemoveWishList(item: T)
     fun <T>onItemClick(item: T)
     fun <T>contactSeller(item: T)
}