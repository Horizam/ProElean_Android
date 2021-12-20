package com.horizam.pro.elean.ui.main.viewmodel

import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.horizam.pro.elean.data.repository.MainRepository
import com.horizam.pro.elean.utils.BaseUtils
import com.horizam.pro.elean.utils.Resource
import kotlinx.coroutines.Dispatchers
import java.util.*

class BuyerRequestsViewModel(
    private val mainRepository: MainRepository
) : ViewModel() {

    private val buyerRequestsRequest = MutableLiveData<String>()
    private val cancelBuyerRequest = MutableLiveData<String>()

    val buyerRequests = buyerRequestsRequest.switchMap {
        mainRepository.getBuyerRequests(it).cachedIn(viewModelScope)
    }

    val deleteBuyerRequest = cancelBuyerRequest.switchMap {
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(Resource.success(data = mainRepository.cancelBuyerRequests(it)))
            } catch (exception: Exception) {
                val errorMessage = BaseUtils.getError(exception)
                emit(Resource.error(data = null, message = errorMessage))
            }
        }
    }

    fun deleteBuyerRequestCall(id:String){
        cancelBuyerRequest.value = id
    }

    fun getBuyerRequestsCall(status:String = ""){
        buyerRequestsRequest.value = status
    }
}