package com.horizam.pro.elean.ui.main.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import com.horizam.pro.elean.data.model.requests.BankDetail
import com.horizam.pro.elean.data.model.requests.LoginRequest
import com.horizam.pro.elean.data.repository.MainRepository
import com.horizam.pro.elean.utils.BaseUtils
import com.horizam.pro.elean.utils.Resource
import kotlinx.coroutines.Dispatchers

class BankDetailViewModel(private val mainRepository: MainRepository) : ViewModel() {

    private val addAccountDetailRequest = MutableLiveData<BankDetail>()
    private val getAccountDetailRequest = MutableLiveData(defaultRequest)

    val addAccountDetail = addAccountDetailRequest.switchMap {
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(Resource.success(data = mainRepository.addAccountDetail(addAccountDetailRequest.value!!)))
            } catch (exception: Exception) {
                val errorMessage = BaseUtils.getError(exception)
                emit(Resource.error(data = null, message = errorMessage))
            }
        }
    }

    val getAccountDetail = getAccountDetailRequest.switchMap {
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(Resource.success(data = mainRepository.getAccountDetail()))
            } catch (exception: Exception) {
                val errorMessage = BaseUtils.getError(exception)
                emit(Resource.error(data = null, message = errorMessage))
            }
        }
    }

    fun addAccountDetailRequest(request: BankDetail) {
        addAccountDetailRequest.value = request
    }

    companion object {
        const val defaultRequest = "profileDataRequest"
    }
}