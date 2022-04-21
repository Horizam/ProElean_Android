package com.horizam.pro.elean.ui.main.viewmodel

import androidx.lifecycle.*
import com.horizam.pro.elean.data.model.requests.*
import com.horizam.pro.elean.data.repository.MainRepository
import com.horizam.pro.elean.utils.BaseUtils
import com.horizam.pro.elean.utils.Resource
import kotlinx.coroutines.Dispatchers


class CheckOutViewModel(private val mainRepository: MainRepository) : ViewModel() {

    private val customOrderRequest = MutableLiveData<CustomOrderRequest>()
    private val getTokenRequest = MutableLiveData<CardModel>()

    val customOrder = customOrderRequest.switchMap {
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(Resource.success(data = mainRepository.customOrder(customOrderRequest.value!!)))
            } catch (exception: Exception) {
                val errorMessage = BaseUtils.getError(exception)
                emit(Resource.error(data = null, message = errorMessage))
            }
        }
    }

    val getToken = getTokenRequest.switchMap {
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(Resource.success(data = mainRepository.getToken(getTokenRequest.value!!)))
            } catch (exception: Exception) {
                val errorMessage = BaseUtils.getError(exception)
                emit(Resource.error(data = null, message = errorMessage))
            }
        }
    }

    fun customOrderCall(request: CustomOrderRequest) {
        customOrderRequest.value = request
    }

    fun getToken(request: CardModel){
        getTokenRequest.value = request
    }
}