package com.horizam.pro.elean.ui.main.viewmodel

import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.google.firebase.firestore.Query
import com.horizam.pro.elean.data.model.requests.ChatOfferRequest
import com.horizam.pro.elean.data.model.requests.CustomOrderRequest
import com.horizam.pro.elean.data.repository.MainRepository
import com.horizam.pro.elean.utils.BaseUtils
import com.horizam.pro.elean.utils.Resource
import kotlinx.coroutines.Dispatchers

class MessagesViewModel(
    private val mainRepository: MainRepository
) : ViewModel() {

    private val messagesRequest = MutableLiveData<Query>()
    private val chatOrderRequest = MutableLiveData<ChatOfferRequest>()
    private val firebaseNotification = MutableLiveData<FirebaseNotification>()


    val messages = messagesRequest.switchMap { query ->
        mainRepository.getMessages(query).cachedIn(viewModelScope)
    }

    val chatOrder = chatOrderRequest.switchMap {
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(Resource.success(data = mainRepository.chatOrder(it)))
            } catch (exception: Exception) {
                val errorMessage = BaseUtils.getError(exception)
                emit(Resource.error(data = null, message = errorMessage))
            }
        }
    }

    val sendNotification = firebaseNotification.switchMap {
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(Resource.success(data = mainRepository.sendFirebaseNotification(it)))
            } catch (exception: Exception) {
                val errorMessage = BaseUtils.getError(exception)
                emit(Resource.error(data = null, message = errorMessage))
            }
        }
    }

    fun getMessagesCall(query: Query) {
        messagesRequest.value = query
    }

    fun chatOrderCall(request: ChatOfferRequest) {
        chatOrderRequest.value = request
    }

    fun sendFirebaseNotificationCall(request: FirebaseNotification) {
        firebaseNotification.value = request
    }

}