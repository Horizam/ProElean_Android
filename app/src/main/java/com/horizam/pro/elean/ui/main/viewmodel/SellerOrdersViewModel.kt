package com.horizam.pro.elean.ui.main.viewmodel

import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.horizam.pro.elean.data.model.BuyerActionRequestMultipart
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
    private val sellerActionsWithFileRequest = MutableLiveData<SellerActionRequestMultipart>()
    private val buyerActionRequestMultipart=MutableLiveData<BuyerActionRequestMultipart>()
    private val orderTimelineRequest = MutableLiveData<OrderTimelineRequest>()
    private val ratingOrderRequest = MutableLiveData<RatingOrderRequest>()
    private val orderByIdRequest = MutableLiveData<Int>()
    private val extendTimeRequest = MutableLiveData<ExtendDeliveryTimeModel>()
    private val cancelRequest=MutableLiveData<String>()
    private val disagreedRequest=MutableLiveData<String>()
    private val acceptRequest=MutableLiveData<String>()
    private val rejectRequest=MutableLiveData<String>()
    private val CompleteRequest=MutableLiveData<BuyerActionRequestMultipart>()
    private val RevisionRequest=MutableLiveData<BuyerRevisionAction>()
    private val sellerHashMap = MutableLiveData<HashMap<String, Any>>()
    private var order_id = ""
    val sellerOrders = sellerOrdersRequest.switchMap {
            mainRepository.getSellOrders(it.toString()).cachedIn(viewModelScope)
        }
    val sellerActionWithFile = sellerActionsWithFileRequest.switchMap {
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(Resource.success(data = mainRepository.sellerActionsWithFile(order_id,sellerActionsWithFileRequest.value!!)))
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
    val User = buyerActionRequestMultipart.switchMap {
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(Resource.success(data = mainRepository.buyerAction(order_id,
                    buyerActionRequestMultipart.value!!)))
            } catch (exception: Exception) {
                val errorMessage = BaseUtils.getError(exception)
                emit(Resource.error(data = null, message = errorMessage))
            }
        }
    }
    val buyerCancelDispute = disagreedRequest.switchMap {
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(Resource.success(data = mainRepository.buyerCancelDispute(order_id)))
            } catch (exception: Exception) {
                val errorMessage = BaseUtils.getError(exception)
                emit(Resource.error(data = null, message = errorMessage))
            }
        }
    }
    val acceptDispute = cancelRequest.switchMap {
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(Resource.success(data = mainRepository.sellerAccepttDispute(order_id)))
            } catch (exception: Exception) {
                val errorMessage = BaseUtils.getError(exception)
                emit(Resource.error(data = null, message = errorMessage))
            }
        }
    }

    val buyerComplete = CompleteRequest.switchMap {
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(Resource.success(data = mainRepository.buyerCompleted(order_id,CompleteRequest.value!!)))
            } catch (exception: Exception) {
                val errorMessage = BaseUtils.getError(exception)
                emit(Resource.error(data = null, message = errorMessage))
            }
        }
    }
    val buyerRevision = RevisionRequest.switchMap {
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(Resource.success(data = mainRepository.buyerRevision(order_id,RevisionRequest.value!!)))
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
                emit(Resource.success(data = mainRepository.ratingOrder(order_id,ratingOrderRequest.value!!)))
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

    val accept = acceptRequest.switchMap {
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(Resource.success(data = mainRepository.acceptExtension(order_id)))
            } catch (exception: Exception) {
                val errorMessage = BaseUtils.getError(exception)
                emit(Resource.error(data = null, message = errorMessage))
            }
        }
    }
    val reject = rejectRequest.switchMap {
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(Resource.success(data = mainRepository.rejectExtension(order_id)))
            } catch (exception: Exception) {
                val errorMessage = BaseUtils.getError(exception)
                emit(Resource.error(data = null, message = errorMessage))
            }
        }
    }

    val extendTime = extendTimeRequest.switchMap {
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(Resource.success(data = mainRepository.extendTime(order_id,extendTimeRequest.value!!)))
            } catch (exception: Exception) {
                val errorMessage = BaseUtils.getError(exception)
                emit(Resource.error(data = null, message = errorMessage))
            }
        }
    }

    fun getSellerOrdersCall(id: Int) {
        sellerOrdersRequest.value = id
    }
    fun buyerActionsCall(id: String, request: BuyerActionRequestMultipart) {
    order_id=id
    buyerActionRequestMultipart.value = request
}
    fun buyerCompleteActions(id: String, request: BuyerActionRequestMultipart) {
        order_id=id
        CompleteRequest.value = request
    }
    fun buyerRevisionActions(id: String, request: BuyerRevisionAction) {
        order_id=id
        RevisionRequest.value = request
    }
    fun cancelRequest(id:String)
    {
        order_id=id
        disagreedRequest.value=id
    }
    fun AcceptDisputeRequest(id:String)
    {
        order_id=id
        cancelRequest.value=id
    }
    fun acceptExtensionRequest(id:String)
    {
        order_id=id
        acceptRequest.value=id
    }
    fun rejectExtensionRequest(id:String)
    {
        order_id=id
        rejectRequest.value=id
    }


    fun sellerActionsCallWithFile(id: String,request: SellerActionRequestMultipart) {
        order_id=id
        sellerActionsWithFileRequest.value = request
    }

    fun orderTimelineCall(request: OrderTimelineRequest) {
        orderTimelineRequest.value = request
    }

    fun ratingOrderCall(id:String,request: RatingOrderRequest) {
        order_id=id
        ratingOrderRequest.value = request
    }

    fun getOrderById(request: Int) {
        orderByIdRequest.value = request
    }

    fun requestExtendTime(id:   String,request: ExtendDeliveryTimeModel) {
        order_id=id
        extendTimeRequest.value = request
    }

}