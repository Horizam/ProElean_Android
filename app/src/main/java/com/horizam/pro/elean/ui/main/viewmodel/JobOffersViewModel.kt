package com.horizam.pro.elean.ui.main.viewmodel

import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.horizam.pro.elean.data.model.requests.AcceptOrderRequest
import com.horizam.pro.elean.data.repository.MainRepository
import com.horizam.pro.elean.utils.BaseUtils
import com.horizam.pro.elean.utils.Resource
import kotlinx.coroutines.Dispatchers
import java.util.*

class JobOffersViewModel(
    private val mainRepository: MainRepository
) : ViewModel() {

    private val offerIdRequest = MutableLiveData<Int>()
    private val acceptOrderRequest = MutableLiveData<AcceptOrderRequest>()
    private val deleteJobOfferRequest = MutableLiveData<Int>()

    val jobOffers = offerIdRequest.switchMap { id ->
        mainRepository.getJobOffers(id).cachedIn(viewModelScope)
    }

    val deleteJobOffer = deleteJobOfferRequest.switchMap {
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(Resource.success(data = mainRepository.deleteJobOffer(it)))
            } catch (exception: Exception) {
                val errorMessage = BaseUtils.getError(exception)
                emit(Resource.error(data = null, message = errorMessage))
            }
        }
    }

    val acceptOrder = acceptOrderRequest.switchMap {
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(Resource.success(data = mainRepository.acceptOrder(it)))
            } catch (exception: Exception) {
                val errorMessage = BaseUtils.getError(exception)
                emit(Resource.error(data = null, message = errorMessage))
            }
        }
    }

    fun getJobOffersCall(id: Int) {
        offerIdRequest.value = id
    }

    fun deletePostedJobCall(id:Int){
        deleteJobOfferRequest.value = id
    }

    fun acceptOrderCall(request: AcceptOrderRequest){
        acceptOrderRequest.value = request
    }
}