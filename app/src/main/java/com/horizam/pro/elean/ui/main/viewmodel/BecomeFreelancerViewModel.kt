package com.horizam.pro.elean.ui.main.viewmodel

import androidx.lifecycle.*
import com.horizam.pro.elean.data.model.requests.BecomeFreelancerRequest
import com.horizam.pro.elean.data.repository.MainRepository
import com.horizam.pro.elean.utils.BaseUtils
import com.horizam.pro.elean.utils.Resource
import kotlinx.coroutines.Dispatchers


class BecomeFreelancerViewModel(private val mainRepository: MainRepository) : ViewModel() {

    private val spinnerDataRequest = MutableLiveData(DEFAULT_REQUEST)
    private val spinnerSubcategoriesRequest = MutableLiveData<Int>()
    private val becomeFreelancerRequest = MutableLiveData<BecomeFreelancerRequest>()

    val spinnerData = spinnerDataRequest.switchMap {
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

    val becomeFreelancer = becomeFreelancerRequest.switchMap {
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(Resource.success(data = mainRepository.becomeFreelancer(it)))
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

    fun spinnerSubcategoriesCall(request: Int) {
        spinnerSubcategoriesRequest.value = request
    }

    fun becomeFreelancerCall(request: BecomeFreelancerRequest) {
        becomeFreelancerRequest.value = request
    }

    companion object{
        const val DEFAULT_REQUEST = "spinnerDataRequest"
    }

}