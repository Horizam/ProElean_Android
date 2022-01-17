package com.horizam.pro.elean.ui.main.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import com.horizam.pro.elean.data.model.requests.UpdateServiceRequest
import com.horizam.pro.elean.data.repository.MainRepository
import com.horizam.pro.elean.utils.BaseUtils
import com.horizam.pro.elean.utils.Resource
import kotlinx.coroutines.Dispatchers

class SellerViewModel(private val mainRepository: MainRepository) : ViewModel() {
    private val sellerDataRequest = MutableLiveData(DEFAULT_REQUEST)

    var sellerData = sellerDataRequest.switchMap {
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(Resource.success(data = mainRepository.getSellerData()))
            } catch (exception: Exception) {
                val errorMessage = BaseUtils.getError(exception)
                emit(Resource.error(data = null, message = errorMessage))
            }
        }
    }

    companion object {
        const val DEFAULT_REQUEST = "sellerData"
    }
}