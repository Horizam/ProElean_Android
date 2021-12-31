package com.horizam.pro.elean.ui.main.viewmodel

import android.provider.SyncStateContract
import androidx.lifecycle.*
import com.horizam.pro.elean.Constants
import com.horizam.pro.elean.data.model.requests.CreateServiceRequest
import com.horizam.pro.elean.data.model.requests.LoginRequest
import com.horizam.pro.elean.data.model.requests.StoreUserInfoRequest
import com.horizam.pro.elean.data.model.requests.UpdateServiceRequest
import com.horizam.pro.elean.data.model.response.GeneralResponse
import com.horizam.pro.elean.data.repository.MainRepository
import com.horizam.pro.elean.utils.BaseUtils
import com.horizam.pro.elean.utils.Resource
import kotlinx.coroutines.Dispatchers


class ManageServicesViewModel(private val mainRepository: MainRepository) : ViewModel() {


    private val userServicesRequest = MutableLiveData<String>()
    private val deleteUserServiceRequest = MutableLiveData<String>()
    private val createServiceRequest = MutableLiveData<CreateServiceRequest>()
    private val updateServiceRequest = MutableLiveData<UpdateServiceRequest>()
    private val spinnerSubcategoriesRequest = MutableLiveData<String>()
    private val spinnerDataRequest = MutableLiveData(DEFAULT_REQUEST)

    var userServices = userServicesRequest.switchMap {
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

    val deleteUserService = deleteUserServiceRequest.switchMap {
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(Resource.success(data = mainRepository.deleteUserService(it)))
            } catch (exception: Exception) {
                val errorMessage = BaseUtils.getError(exception)
                emit(Resource.error(data = null, message = errorMessage))
            }
        }
    }

    fun userServicesCall(request: String) {
        userServicesRequest.value = request
    }

    fun deleteUserServiceCall(id: String) {
        deleteUserServiceRequest.value = id
    }

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

    val categoriesRevisionDeliveryTimeResponse = spinnerDataRequest.switchMap {
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

    fun createServiceCall(request: CreateServiceRequest) {
        createServiceRequest.value = request
    }

    fun updateServiceCall(request: UpdateServiceRequest) {
        updateServiceRequest.value = request
    }

    fun spinnerSubcategoriesCall(request: String) {
        spinnerSubcategoriesRequest.value = request
    }

    companion object {
        const val DEFAULT_REQUEST = "spinnerDataRequest"
    }


}