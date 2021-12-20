package com.horizam.pro.elean.ui.main.viewmodel

import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.horizam.pro.elean.Constants
import com.horizam.pro.elean.data.repository.MainRepository
import com.horizam.pro.elean.utils.BaseUtils
import com.horizam.pro.elean.utils.Resource
import kotlinx.coroutines.Dispatchers
import java.util.*

class PostedJobsViewModel(
    private val mainRepository: MainRepository
) : ViewModel() {

    private val postedJobsRequest = MutableLiveData("")
    private val deletePostedJobRequest = MutableLiveData<String>()

    val postedJobs = postedJobsRequest.switchMap {
        mainRepository.getPostedJobs(it).cachedIn(viewModelScope)
    }

    val deletePostedJob = deletePostedJobRequest.switchMap {
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(Resource.success(data = mainRepository.deletePostedJob(it)))
            } catch (exception: Exception) {
                val errorMessage = BaseUtils.getError(exception)
                emit(Resource.error(data = null, message = errorMessage))
            }
        }
    }

    fun getPostedJobsCall(status:String){
        postedJobsRequest.value = status
    }

    fun deletePostedJobCall(id:String){
        deletePostedJobRequest.value = id
    }

}