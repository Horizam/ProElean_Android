package com.horizam.pro.elean.ui.main.viewmodel

import androidx.lifecycle.*
import com.horizam.pro.elean.Constants
import com.horizam.pro.elean.data.repository.MainRepository
import com.horizam.pro.elean.utils.BaseUtils
import com.horizam.pro.elean.utils.Resource
import kotlinx.coroutines.Dispatchers


class EarningsViewModel(private val mainRepository: MainRepository) : ViewModel() {

    private val earningsRequest = MutableLiveData(Constants.DEFAULT_MUTABLE_LIVEDATA_VALUE)
    private val withdrawalRequest = MutableLiveData<Double>()

    val earnings = earningsRequest.switchMap {
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(Resource.success(data = mainRepository.getEarnings()))
            } catch (exception: Exception) {
                val errorMessage = BaseUtils.getError(exception)
                emit(Resource.error(data = null, message = errorMessage))
            }
        }
    }

    val withdrawalAmount = withdrawalRequest.switchMap {
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(Resource.success(data = mainRepository.withdrawalAmount(it)))
            } catch (exception: Exception) {
                val errorMessage = BaseUtils.getError(exception)
                emit(Resource.error(data = null, message = errorMessage))
            }
        }
    }

    fun withdrawalAmount(amount: Double) {
        withdrawalRequest.value = amount
    }
}