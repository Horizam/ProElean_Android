package com.horizam.pro.elean.ui.main.viewmodel

import androidx.lifecycle.*
import com.horizam.pro.elean.data.model.requests.ForgotPasswordRequest
import com.horizam.pro.elean.data.model.requests.LoginRequest
import com.horizam.pro.elean.data.model.requests.RegisterRequest
import com.horizam.pro.elean.data.repository.MainRepository
import com.horizam.pro.elean.utils.BaseUtils
import com.horizam.pro.elean.utils.Resource
import kotlinx.coroutines.Dispatchers


class FeaturedGigDetailsViewModel(private val mainRepository: MainRepository) : ViewModel() {

    private val featuredGigDetailsRequest = MutableLiveData<String>()

    val featuredGigDetails = featuredGigDetailsRequest.switchMap {
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(Resource.success(data = mainRepository.getFeaturedGigDetails(it)))
            } catch (exception: Exception) {
                val errorMessage = BaseUtils.getError(exception)
                emit(Resource.error(data = null, message = errorMessage))
            }
        }
    }

    fun featuredGigDetailsCall(request: String) {
        featuredGigDetailsRequest.value = request
    }

}