package com.horizam.pro.elean.ui.main.viewmodel

import android.provider.SyncStateContract
import androidx.lifecycle.*
import com.horizam.pro.elean.Constants
import com.horizam.pro.elean.data.model.requests.LoginRequest
import com.horizam.pro.elean.data.model.requests.StoreUserInfoRequest
import com.horizam.pro.elean.data.model.response.GeneralResponse
import com.horizam.pro.elean.data.repository.MainRepository
import com.horizam.pro.elean.utils.BaseUtils
import com.horizam.pro.elean.utils.Resource
import kotlinx.coroutines.Dispatchers


class ManageServicesViewModel(private val mainRepository: MainRepository) : ViewModel() {


    private val userServicesRequest = MutableLiveData<String>()
    private val deleteUserServiceRequest = MutableLiveData<Int>()

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

    fun deleteUserServiceCall(id:Int){
        deleteUserServiceRequest.value = id
    }

}