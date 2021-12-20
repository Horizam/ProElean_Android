package com.horizam.pro.elean.ui.main.callbacks

interface MessagesHandler {
     fun <T>onMessageClick(item: T)
     fun <T>onImageClick(item: T)
     fun <T>onPlayVideo(item: T)
     fun <T>onDownloadDocument(item: T)
     fun <T>onOfferButtonClick(item: T)
}