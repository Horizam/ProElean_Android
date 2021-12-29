package com.horizam.pro.elean.ui.main.viewmodel

import androidx.lifecycle.*
import com.horizam.pro.elean.data.model.requests.CreateServiceRequest
import com.horizam.pro.elean.data.model.requests.LoginRequest
import com.horizam.pro.elean.data.model.requests.UpdateProfileRequest
import com.horizam.pro.elean.data.model.response.GeneralResponse
import com.horizam.pro.elean.data.repository.MainRepository
import com.horizam.pro.elean.utils.BaseUtils
import com.horizam.pro.elean.utils.Resource
import kotlinx.coroutines.Dispatchers


class ProfileViewModel(private val mainRepository: MainRepository) : ViewModel() {

    private val profileDataRequest = MutableLiveData(DEFAULT_PROFILE_REQUEST)
    private val freelancerProfileDataRequest = MutableLiveData<String>()
    private val updateProfileRequest = MutableLiveData<UpdateProfileRequest>()
    private val userServicesRequest = MutableLiveData<String>()

    val profileData = profileDataRequest.switchMap {
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(Resource.success(data = mainRepository.getNonFreelancerProfile()))
            } catch (exception: Exception) {
                val errorMessage = BaseUtils.getError(exception)
                emit(Resource.error(data = null, message = errorMessage))
            }
        }
    }

    val freelancerProfileData = freelancerProfileDataRequest.switchMap {
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(Resource.success(data = mainRepository.getFreelancerProfile(it)))
            } catch (exception: Exception) {
                val errorMessage = BaseUtils.getError(exception)
                emit(Resource.error(data = null, message = errorMessage))
            }
        }
    }

    val updateProfile = updateProfileRequest.switchMap {
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(Resource.success(data = mainRepository.updateProfile(it)))
            } catch (exception: Exception) {
                val errorMessage = BaseUtils.getError(exception)
                emit(Resource.error(data = null, message = errorMessage))
            }
        }
    }

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

    fun profileDataCall(request: String = "profileDataRequest") {
        profileDataRequest.value = request
    }

    fun freelancerProfileDataCall(request: String) {
        freelancerProfileDataRequest.value = request
    }

    fun updateProfileCall(request: UpdateProfileRequest) {
        updateProfileRequest.value = request
    }

    fun userServicesCall(request: String) {
        userServicesRequest.value = request
    }

    companion object{
        const val DEFAULT_PROFILE_REQUEST = "profileDataRequest"
    }

}