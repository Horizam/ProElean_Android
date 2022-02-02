package com.horizam.pro.elean.ui.main.viewmodel

import androidx.lifecycle.*
import com.horizam.pro.elean.data.model.requests.*
import com.horizam.pro.elean.data.repository.MainRepository
import com.horizam.pro.elean.utils.BaseUtils
import com.horizam.pro.elean.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlin.collections.HashMap

class SellerOrdersViewModel(
    private val mainRepository: MainRepository
) : ViewModel() {

    private val sellerOrdersRequest = MutableLiveData<Int>()
    private val sellerActionsRequest = MutableLiveData<SellerActionsRequest>()
    private val sellerActionsWithFileRequest = MutableLiveData<SellerActionRequestMultipart>()
    private val buyerActionsRequest = MutableLiveData<BuyerActionsRequest>()
    private val orderTimelineRequest = MutableLiveData<OrderTimelineRequest>()
    private val ratingOrderRequest = MutableLiveData<RatingOrderRequest>()
    private val orderByIdRequest = MutableLiveData<Int>()

    //we used seller hashmap when we send different value that required due to backend issues
    private val sellerHashMap = MutableLiveData<HashMap<String, Any>>()

    //we used seller hashmap when we send different value that required due to backend issues
    private val buyerHashMap = MutableLiveData<HashMap<String, Any>>()

    val sellerOrders = sellerOrdersRequest.switchMap {
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(Resource.success(data = mainRepository.getSellersOrders(it)))
            } catch (exception: Exception) {
                val errorMessage = BaseUtils.getError(exception)
                emit(Resource.error(data = null, message = errorMessage))
            }
        }
    }

    val sellerActionWithFile = sellerActionsWithFileRequest.switchMap {
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(Resource.success(data = mainRepository.sellerActionsWithFile(it)))
            } catch (exception: Exception) {
                val errorMessage = BaseUtils.getError(exception)
                emit(Resource.error(data = null, message = errorMessage))
            }
        }
    }

    val sellerActions = sellerHashMap.switchMap {
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(Resource.success(data = mainRepository.sellerActions(it)))
            } catch (exception: Exception) {
                val errorMessage = BaseUtils.getError(exception)
                emit(Resource.error(data = null, message = errorMessage))
            }
        }
    }

    val buyerActions = buyerHashMap.switchMap {
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(Resource.success(data = mainRepository.buyerActions(it)))
            } catch (exception: Exception) {
                val errorMessage = BaseUtils.getError(exception)
                emit(Resource.error(data = null, message = errorMessage))
            }
        }
    }

    val orderTimeline = orderTimelineRequest.switchMap {
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(Resource.success(data = mainRepository.orderTimeline(it)))
            } catch (exception: Exception) {
                val errorMessage = BaseUtils.getError(exception)
                emit(Resource.error(data = null, message = errorMessage))
            }
        }
    }

    val ratingOrder = ratingOrderRequest.switchMap {
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(Resource.success(data = mainRepository.ratingOrder(it)))
            } catch (exception: Exception) {
                val errorMessage = BaseUtils.getError(exception)
                emit(Resource.error(data = null, message = errorMessage))
            }
        }
    }

    val orderByID = orderByIdRequest.switchMap {
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(Resource.success(data = mainRepository.orderByID(it)))
            } catch (exception: Exception) {
                val errorMessage = BaseUtils.getError(exception)
                emit(Resource.error(data = null, message = errorMessage))
            }
        }
    }

    fun getSellerOrdersCall(id: Int) {
        sellerOrdersRequest.value = id
    }

    fun sellerActionsCall(hashMap: HashMap<String, Any>) {
        sellerHashMap.value = hashMap
    }

    fun buyerActionsCall(hashMap: HashMap<String, Any>) {
        buyerHashMap.value = hashMap
    }

    fun sellerActionsCallWithFile(request: SellerActionRequestMultipart) {
        sellerActionsWithFileRequest.value = request
    }

    fun orderTimelineCall(request: OrderTimelineRequest) {
        orderTimelineRequest.value = request
    }

    fun ratingOrderCall(request: RatingOrderRequest) {
        ratingOrderRequest.value = request
    }

    fun getOrderById(request: Int) {
        orderByIdRequest.value = request
    }
}