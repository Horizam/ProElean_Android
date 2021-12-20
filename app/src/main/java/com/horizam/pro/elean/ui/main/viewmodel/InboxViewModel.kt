package com.horizam.pro.elean.ui.main.viewmodel

import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.google.firebase.firestore.Query
import com.horizam.pro.elean.data.repository.MainRepository

class InboxViewModel(
    private val mainRepository: MainRepository
) : ViewModel() {

    private val inboxRequest = MutableLiveData<Query>()

    val inbox = inboxRequest.switchMap { query ->
        mainRepository.getInbox(query).cachedIn(viewModelScope)
    }

    fun getInboxCall(query: Query) {
        inboxRequest.value = query
    }

}