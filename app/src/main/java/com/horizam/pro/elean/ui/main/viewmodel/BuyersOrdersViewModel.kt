package com.horizam.pro.elean.ui.main.viewmodel

import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.horizam.pro.elean.Constants
import com.horizam.pro.elean.data.repository.MainRepository
import com.horizam.pro.elean.utils.BaseUtils
import com.horizam.pro.elean.utils.Resource
import kotlinx.coroutines.Dispatchers
import java.util.*

class BuyersOrdersViewModel(
    private val mainRepository: MainRepository
) : ViewModel() {

    private val buyerOrdersRequest = MutableLiveData<Int>()

    val buyerOrders = buyerOrdersRequest.switchMap {
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(Resource.success(data = mainRepository.getBuyerOrders(it)))
            } catch (exception: Exception) {
                val errorMessage = BaseUtils.getError(exception)
                emit(Resource.error(data = null, message = errorMessage))
            }
        }
    }

    fun getBuyerOrdersCall(id: Int) {
        buyerOrdersRequest.value = id
    }

}