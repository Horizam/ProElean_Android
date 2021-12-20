package com.horizam.pro.elean.ui.main.viewmodel

import android.provider.SyncStateContract
import androidx.lifecycle.*
import com.horizam.pro.elean.Constants
import com.horizam.pro.elean.data.model.requests.LoginRequest
import com.horizam.pro.elean.data.model.requests.PostJobRequest
import com.horizam.pro.elean.data.model.requests.SendOfferRequest
import com.horizam.pro.elean.data.model.requests.StoreUserInfoRequest
import com.horizam.pro.elean.data.model.response.GeneralResponse
import com.horizam.pro.elean.data.repository.MainRepository
import com.horizam.pro.elean.utils.BaseUtils
import com.horizam.pro.elean.utils.Resource
import kotlinx.coroutines.Dispatchers


class SellerServicesViewModel(private val mainRepository: MainRepository) : ViewModel() {


    private val userServicesRequest = MutableLiveData<String>()
    private val sendOfferRequest = MutableLiveData<SendOfferRequest>()

    val userServices = userServicesRequest.switchMap {
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(Resource.success(data = mainRepository.getManageServices(it)))
            } catch (exception: Exception) {
                val errorMessage = BaseUtils.getError(exception)
                emit(Resource.error(data = null, message = errorMessage))
            }
        }
    }

    val sendOffer = sendOfferRequest.switchMap {
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(Resource.success(data = mainRepository.sendOffer(it)))
            } catch (exception: Exception) {
                val errorMessage = BaseUtils.getError(exception)
                emit(Resource.error(data = null, message = errorMessage))
            }
        }
    }

    fun userServicesCall(request: String) {
        userServicesRequest.value = request
    }

    fun sendOfferCall(request: SendOfferRequest) {
        sendOfferRequest.value = request
    }

}