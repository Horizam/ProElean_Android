package com.horizam.pro.elean.ui.main.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import com.horizam.pro.elean.data.model.requests.CustomOrderRequest
import com.horizam.pro.elean.data.model.requests.ReviewsRequest
import com.horizam.pro.elean.data.repository.MainRepository
import com.horizam.pro.elean.utils.BaseUtils
import com.horizam.pro.elean.utils.Resource
import kotlinx.coroutines.Dispatchers


class GigDetailsViewModel(private val mainRepository: MainRepository) : ViewModel() {

    private val gigDetailsRequest = MutableLiveData<String>()
    private val customOrderRequest = MutableLiveData<CustomOrderRequest>()
    private val addClicksGigsRequest = MutableLiveData<String>()
    private val getReviewsRequest = MutableLiveData<ReviewsRequest>()

    val gigDetails = gigDetailsRequest.switchMap {
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(Resource.success(data = mainRepository.getGigDetails(gigDetailsRequest.value!!)))
            } catch (exception: Exception) {
                val errorMessage = BaseUtils.getError(exception)
                emit(Resource.error(data = null, message = errorMessage))
            }
        }
    }

    val reviewList = getReviewsRequest.switchMap {
        mainRepository.getReviews(getReviewsRequest.value!!.id)
    }

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

    val clickGigs = addClicksGigsRequest.switchMap {
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(Resource.success(data = mainRepository.addClicksGigsRequest(it)))
            } catch (exception: Exception) {
                val errorMessage = BaseUtils.getError(exception)
                emit(Resource.error(data = null, message = errorMessage))
            }
        }
    }

    fun gigDetailsCall(request: String) {
        gigDetailsRequest.value = request
    }

    fun customOrderCall(request: CustomOrderRequest) {
        customOrderRequest.value = request
    }

    fun addClickGigs(request: String) {
        addClicksGigsRequest.value = request
    }

    fun getReviews(request: ReviewsRequest) {
        getReviewsRequest.value = request
    }
}