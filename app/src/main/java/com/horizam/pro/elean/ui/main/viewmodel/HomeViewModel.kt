package com.horizam.pro.elean.ui.main.viewmodel

import androidx.lifecycle.*
import com.horizam.pro.elean.data.model.requests.LoginRequest
import com.horizam.pro.elean.data.model.requests.StoreUserInfoRequest
import com.horizam.pro.elean.data.model.response.GeneralResponse
import com.horizam.pro.elean.data.repository.MainRepository
import com.horizam.pro.elean.utils.BaseUtils
import com.horizam.pro.elean.utils.Resource
import kotlinx.coroutines.Dispatchers


class HomeViewModel(private val mainRepository: MainRepository) : ViewModel() {

    private val logoutRequest = MutableLiveData<String>()
    private val storeInfoRequest = MutableLiveData<StoreUserInfoRequest>()
    private val homeDataRequest = MutableLiveData(DEFAULT_HOME_REQUEST)

    val logoutUser = logoutRequest.switchMap {
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(Resource.success(data = mainRepository.logout()))
            } catch (exception: Exception) {
                val errorMessage = BaseUtils.getError(exception)
                emit(Resource.error(data = null, message = errorMessage))
            }
        }
    }

    val homeData = homeDataRequest.switchMap {
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(Resource.success(data = mainRepository.getHomeData()))
            } catch (exception: Exception) {
                val errorMessage = BaseUtils.getError(exception)
                emit(Resource.error(data = null, message = errorMessage))
            }
        }
    }

    val userInfo = storeInfoRequest.switchMap {
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(Resource.success(data = mainRepository.storeUserInfo(it)))
            } catch (exception: Exception) {
                val errorMessage = BaseUtils.getError(exception)
                emit(Resource.error(data = null, message = errorMessage))
            }
        }
    }

    fun logoutCall(request: String = "logout") {
        logoutRequest.value = request
    }

    fun homeDataCall(request: String = "homeDataRequest") {
        homeDataRequest.value = request
    }

    fun storeUserInfoCall(request: StoreUserInfoRequest) {
        storeInfoRequest.value = request
    }

    companion object{
        const val DEFAULT_HOME_REQUEST = "homeDataRequest"
    }

}