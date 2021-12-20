package com.horizam.pro.elean.ui.main.viewmodel

import androidx.lifecycle.*
import com.horizam.pro.elean.data.model.requests.*
import com.horizam.pro.elean.data.repository.MainRepository
import com.horizam.pro.elean.utils.BaseUtils
import com.horizam.pro.elean.utils.Resource
import kotlinx.coroutines.Dispatchers


class CreateServiceViewModel(private val mainRepository: MainRepository) : ViewModel() {

    private val createServiceRequest = MutableLiveData<CreateServiceRequest>()
    private val updateServiceRequest = MutableLiveData<UpdateServiceRequest>()
    private val spinnerSubcategoriesRequest = MutableLiveData<Int>()
    private val categoriesDaysRequest = MutableLiveData("getData")

    val createService = createServiceRequest.switchMap {
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(Resource.success(data = mainRepository.createService(it)))
            } catch (exception: Exception) {
                val errorMessage = BaseUtils.getError(exception)
                emit(Resource.error(data = null, message = errorMessage))
            }
        }
    }

    val updateService = updateServiceRequest.switchMap {
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(Resource.success(data = mainRepository.updateService(it)))
            } catch (exception: Exception) {
                val errorMessage = BaseUtils.getError(exception)
                emit(Resource.error(data = null, message = errorMessage))
            }
        }
    }

    val categoriesDays = categoriesDaysRequest.switchMap {
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(Resource.success(data = mainRepository.getCategoriesDays()))
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

    fun createServiceCall(request: CreateServiceRequest) {
        createServiceRequest.value = request
    }

    fun updateServiceCall(request: UpdateServiceRequest) {
        updateServiceRequest.value = request
    }

    fun spinnerSubcategoriesCall(request: Int) {
        spinnerSubcategoriesRequest.value = request
    }

}