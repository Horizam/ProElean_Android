package com.horizam.pro.elean.ui.main.viewmodel

import androidx.lifecycle.*
import com.horizam.pro.elean.data.model.requests.ForgetChangePasswordRequest
import com.horizam.pro.elean.data.model.requests.ForgotPasswordRequest
import com.horizam.pro.elean.data.repository.MainRepository
import com.horizam.pro.elean.utils.BaseUtils
import com.horizam.pro.elean.utils.Resource
import kotlinx.coroutines.Dispatchers


class ForgotPasswordViewModel(private val mainRepository: MainRepository) : ViewModel() {

    private val forgotPasswordRequest = MutableLiveData<ForgotPasswordRequest>()
    private val forgetChangePasswordRequest = MutableLiveData<ForgetChangePasswordRequest>()


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

    val changePassword = forgetChangePasswordRequest.switchMap {
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(Resource.success(data = mainRepository.forgotChangePassword(forgetChangePasswordRequest.value!!)))
            } catch (exception: Exception) {
                val errorMessage = BaseUtils.getError(exception)
                emit(Resource.error(data = null, message = errorMessage))
            }
        }
    }

    fun forgotPasswordCall(request: ForgotPasswordRequest) {
        forgotPasswordRequest.value = request
    }

    fun forgetChangePasswordVerificationCodeCall(request: ForgetChangePasswordRequest) {
        forgetChangePasswordRequest.value = request
    }

}