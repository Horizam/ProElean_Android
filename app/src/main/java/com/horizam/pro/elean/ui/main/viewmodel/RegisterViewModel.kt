package com.horizam.pro.elean.ui.main.viewmodel

import androidx.lifecycle.*
import com.horizam.pro.elean.data.model.requests.RegisterRequest
import com.horizam.pro.elean.data.repository.MainRepository
import com.horizam.pro.elean.utils.BaseUtils
import com.horizam.pro.elean.utils.Resource
import kotlinx.coroutines.Dispatchers


class RegisterViewModel(private val mainRepository: MainRepository) : ViewModel() {

    private val registerRequest = MutableLiveData<RegisterRequest>()
    private var referralCode = ""

    val registerUser = registerRequest.switchMap {
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(Resource.success(data = mainRepository.registerUser(registerRequest.value!!,referralCode)))
            } catch (exception: Exception) {
                val errorMessage = BaseUtils.getError(exception)
                emit(Resource.error(data = null, message = errorMessage))
            }
        }
    }

    fun registerUserCall(request: RegisterRequest, code: String) {
        referralCode = code
        registerRequest.value = request
    }

}