package com.horizam.pro.elean.ui.main.viewmodel

import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.horizam.pro.elean.data.repository.MainRepository
import java.util.*

class ServiceCategoriesViewModel(
    private val mainRepository: MainRepository
) : ViewModel() {

    private val currentCategory = MutableLiveData<String>()
    private var searchQuery:String = ""

    val subcategories = currentCategory.switchMap { category ->
        mainRepository.getSubcategories(category,searchQuery).cachedIn(viewModelScope)
    }

    fun getSubcategories(category: String,query: String="") {
        searchQuery = query
        currentCategory.value = category
    }

}