package com.horizam.pro.elean.ui.main.viewmodel

import androidx.lifecycle.*
import com.horizam.pro.elean.Constants
import com.horizam.pro.elean.data.model.requests.ChangePasswordRequest
import com.horizam.pro.elean.data.repository.MainRepository
import com.horizam.pro.elean.utils.BaseUtils
import com.horizam.pro.elean.utils.Resource
import kotlinx.coroutines.Dispatchers


class SettingsViewModel(private val mainRepository: MainRepository) : ViewModel() {

    private val policyTermsRequest = MutableLiveData(Constants.DEFAULT_MUTABLE_LIVEDATA_VALUE)
    private val languageAndCurrencyRequest = MutableLiveData(Constants.DEFAULT_MUTABLE_LIVEDATA_VALUE)
    private val changePasswordRequest = MutableLiveData<ChangePasswordRequest>()


    val policyTerms = policyTermsRequest.switchMap {
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(Resource.success(data = mainRepository.getPrivacyTerms()))
            } catch (exception: Exception) {
                val errorMessage = BaseUtils.getError(exception)
                emit(Resource.error(data = null, message = errorMessage))
            }
        }
    }

    val languageAndCurrency = languageAndCurrencyRequest.switchMap {
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(Resource.success(data = mainRepository.getLanguageAndCurrency()))
            } catch (exception: Exception) {
                val errorMessage = BaseUtils.getError(exception)
                emit(Resource.error(data = null, message = errorMessage))
            }
        }
    }

    val changePassword = changePasswordRequest.switchMap {
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(Resource.success(data = mainRepository.changePassword(it)))
            } catch (exception: Exception) {
                val errorMessage = BaseUtils.getError(exception)
                emit(Resource.error(data = null, message = errorMessage))
            }
        }
    }

    fun getLanguageAndCurrency(data: String) {
        languageAndCurrencyRequest.value = data
    }

    fun changePasswordCall(changePassword: ChangePasswordRequest) {
        changePasswordRequest.value = changePassword
    }

}