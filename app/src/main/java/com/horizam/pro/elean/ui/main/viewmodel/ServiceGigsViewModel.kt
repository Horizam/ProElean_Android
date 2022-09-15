package com.horizam.pro.elean.ui.main.viewmodel

import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.horizam.pro.elean.data.model.requests.FavouriteRequest
import com.horizam.pro.elean.data.model.requests.SearchGigsRequest
import com.horizam.pro.elean.data.model.requests.SearchRequest
import com.horizam.pro.elean.data.repository.MainRepository
import com.horizam.pro.elean.utils.BaseUtils
import com.horizam.pro.elean.utils.Resource
import kotlinx.coroutines.Dispatchers
import java.util.*

class ServiceGigsViewModel(
    private val mainRepository: MainRepository
) : ViewModel() {

    private val currentSubCategory = MutableLiveData<String>()
    private val SubCategory = MutableLiveData<SearchGigsRequest>()
    private val searchGigsRequest = MutableLiveData<SearchGigsRequest>()
    private val searchRequest = MutableLiveData<SearchRequest>()
    private val addToWishlistRequest = MutableLiveData<FavouriteRequest>()
    private val addClicksGigsRequest = MutableLiveData<String>()
    private val filter=""
    private val filter_value=""

//    val sellers = SubCategory.switchMap { subcategory ->
//        mainRepository.
//        getServicesBySubCategories(subcategory.category,
//            subcategory.filter,
//            subcategory.filterValue,
//        ).cachedIn(viewModelScope)
//    }
    val sellers = currentSubCategory.switchMap {
    mainRepository.
        getServicesBySubCategories(it,filter_value,filter).cachedIn(viewModelScope)
    }

    val searchSellers = searchGigsRequest.switchMap { request ->
        mainRepository.searchGigs(
            request.query,
            request.distance,
            request.filter,
            request.filterValue,
            request.category
        ).cachedIn(viewModelScope)
    }

    val sub= SubCategory.switchMap { request ->
        mainRepository.searchGigs(
            request.query,
            request.distance,
            request.filter,
            request.filterValue,
            request.category
        ).cachedIn(viewModelScope)
    }

    val search = searchRequest.switchMap { request ->
        mainRepository.search(
            request.query,
            request.category
        ).cachedIn(viewModelScope)
    }
    val searchSellersbyHome = searchGigsRequest.switchMap { request ->
        mainRepository.searchGigs(
            request.query,
            request.distance,
            request.filter,
            request.filterValue,
            request.category
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

    val clickGigs = addClicksGigsRequest.switchMap {
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(Resource.success(data = mainRepository.addClicksGigsRequest(it)))
            } catch (exception: Exception) {
                val errorMessage = BaseUtils.getError(exception)
                emit(Resource.error(data = null, message = errorMessage))
            }
        }
    }
    fun getServicesBySubCategories(subcategory:String) {
        currentSubCategory.value = subcategory


    }
    fun getServicesSubCategories( request: SearchGigsRequest) {
        SubCategory.value = request

    }
    fun searchSeller(request: SearchGigsRequest)
    {
        searchGigsRequest.value=request
    }
    fun search(request: SearchRequest) {
        searchRequest.value = request
    }
    fun searchGigsByHome(request: SearchGigsRequest) {
        searchGigsRequest.value = request
    }
    fun addToWishlistCall(request: FavouriteRequest) {
        addToWishlistRequest.value = request
    }

    fun addClickGigs(request: String){
        addClicksGigsRequest.value = request
    }

}