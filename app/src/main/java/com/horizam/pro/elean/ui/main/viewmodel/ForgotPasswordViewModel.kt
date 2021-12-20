package com.horizam.pro.elean.ui.main.viewmodel

import androidx.lifecycle.*
import com.horizam.pro.elean.data.model.requests.ForgotPasswordRequest
import com.horizam.pro.elean.data.model.requests.LoginRequest
import com.horizam.pro.elean.data.model.requests.RegisterRequest
import com.horizam.pro.elean.data.repository.MainRepository
import com.horizam.pro.elean.utils.BaseUtils
import com.horizam.pro.elean.utils.Resource
import kotlinx.coroutines.Dispatchers


class ForgotPasswordViewModel(private val mainRepository: MainRepository) : ViewModel() {

    private val forgotPasswordRequest = MutableLiveData<ForgotPasswordRequest>()

    val forgotPassword = forgotPasswordRequest.switchMap {
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(Resource.success(data = mainRepository.forgotPassword(forgotPasswordRequest.value!!)))
            } catch (exception: Exception) {
                val errorMessage = BaseUtils.getError(exception)
                emit(Resource.error(data = null, message = errorMessage))
            }
        }
    }

    fun forgotPasswordCall(request: ForgotPasswordRequest) {
        forgotPasswordRequest.value = request
    }

}