package com.horizam.pro.elean.ui.main.viewmodel

import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.horizam.pro.elean.data.model.requests.FavouriteRequest
import com.horizam.pro.elean.data.repository.MainRepository
import com.horizam.pro.elean.utils.BaseUtils
import com.horizam.pro.elean.utils.Resource
import kotlinx.coroutines.Dispatchers
import java.util.*

class SavedViewModel(
    private val mainRepository: MainRepository
) : ViewModel() {

    private val addToWishlistRequest = MutableLiveData<FavouriteRequest>()
    private val savedGigsRequest = MutableLiveData<String>()

    val savedGigs = savedGigsRequest.switchMap {
        mainRepository.getSavedGigs().cachedIn(viewModelScope)
    }

    val makeFavourite = addToWishlistRequest.switchMap {
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(Resource.success(data = mainRepository.addRemoveWishlist(it)))
            } catch (exception: Exception) {
                val errorMessage = BaseUtils.getError(exception)
                emit(Resource.error(data = null, message = errorMessage))
            }
        }
    }

    fun getSavedGigsCall(id: String = "") {
        savedGigsRequest.value = id
    }

    fun addToWishlistCall(request: FavouriteRequest) {
        addToWishlistRequest.value = request
    }

}