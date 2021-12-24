package com.horizam.pro.elean.ui.main.viewmodel

import androidx.lifecycle.*
import com.horizam.pro.elean.data.model.requests.ForgotPasswordRequest
import com.horizam.pro.elean.data.model.requests.LoginRequest
import com.horizam.pro.elean.data.model.requests.PostJobRequest
import com.horizam.pro.elean.data.model.requests.RegisterRequest
import com.horizam.pro.elean.data.repository.MainRepository
import com.horizam.pro.elean.utils.BaseUtils
import com.horizam.pro.elean.utils.Resource
import kotlinx.coroutines.Dispatchers


class PostJobViewModel(private val mainRepository: MainRepository) : ViewModel() {

    private val postJobRequest = MutableLiveData<PostJobRequest>()
    private val spinnerSubcategoriesRequest = MutableLiveData<String>()
    private val categoriesDaysRequest = MutableLiveData("getData")

    val postJob = postJobRequest.switchMap {
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(Resource.success(data = mainRepository.postJob(it)))
            } catch (exception: Exception) {
                val errorMessage = BaseUtils.getError(exception)
                emit(Resource.error(data = null, message = errorMessage))
            }
        }
    }

    val categoriesRevisionDeliveryTimeResponse = categoriesDaysRequest.switchMap {
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(Resource.success(data = mainRepository.getCategoriesCountries()))
            } catch (exception: Exception) {
                val errorMessage = BaseUtils.getError(exception)
                emit(Resource.error(data = null, message = errorMessage))
            }
        }
    }

    val spinnerSubcategories = spinnerSubcategoriesRequest.switchMap {
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(Resource.success(data = mainRepository.getSpinnerSubcategories(it)))
            } catch (exception: Exception) {
                val errorMessage = BaseUtils.getError(exception)
                emit(Resource.error(data = null, message = errorMessage))
            }
        }
    }

    fun postJobCall(request: PostJobRequest) {
        postJobRequest.value = request
    }

    fun spinnerSubcategoriesCall(request: String) {
        spinnerSubcategoriesRequest.value = request
    }
}