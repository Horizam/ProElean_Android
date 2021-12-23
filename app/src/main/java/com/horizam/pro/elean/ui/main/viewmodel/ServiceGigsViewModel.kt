package com.horizam.pro.elean.ui.main.viewmodel

import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.horizam.pro.elean.data.model.requests.FavouriteRequest
import com.horizam.pro.elean.data.model.requests.SearchGigsRequest
import com.horizam.pro.elean.data.repository.MainRepository
import com.horizam.pro.elean.utils.BaseUtils
import com.horizam.pro.elean.utils.Resource
import kotlinx.coroutines.Dispatchers
import java.util.*

class ServiceGigsViewModel(
    private val mainRepository: MainRepository
) : ViewModel() {

    private val currentSubCategory = MutableLiveData<String>()
    private val searchGigsRequest = MutableLiveData<SearchGigsRequest>()
    private val addToWishlistRequest = MutableLiveData<FavouriteRequest>()

    val sellers = currentSubCategory.switchMap { subcategory ->
        mainRepository.getServicesBySubCategories(subcategory).cachedIn(viewModelScope)
    }

    val searchSellers = searchGigsRequest.switchMap { request ->
        mainRepository.searchGigs(
            request.query,
            request.distance,
            request.filter,
            request.filterValue
        ).cachedIn(viewModelScope)
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

    fun getServicesBySubCategories(subcategory: String) {
        currentSubCategory.value = subcategory
    }
    fun searchGigs(request: SearchGigsRequest) {
        searchGigsRequest.value = request
    }

    fun addToWishlistCall(request: FavouriteRequest) {
        addToWishlistRequest.value = request
    }

}