package com.horizam.pro.elean.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.google.firebase.firestore.Query
import com.horizam.pro.elean.data.api.ApiHelper
import com.horizam.pro.elean.data.model.BuyerActionRequestMultipart
import com.horizam.pro.elean.data.model.requests.*
import com.horizam.pro.elean.data.pagingsource.*
import com.horizam.pro.elean.ui.main.viewmodel.FirebaseNotificationRequest

class MainRepository(private val apiHelper: ApiHelper) {

    suspend fun registerUser(request: RegisterRequest, referralCode: String) =
        apiHelper.registerUser(request, referralCode)
    suspend fun buyerAction(order_id: String,request: BuyerActionRequestMultipart)=
        apiHelper.buyerAction(order_id,request)
    suspend fun buyerCancelDispute(order_id: String)=apiHelper.buyerCancelDispute(order_id)
    suspend fun sellerRejectDispute(order_id: String)=apiHelper.sellerRejectDispute(order_id)
    suspend fun sellerAccepttDispute(order_id: String)=apiHelper.sellerAcceptDispute(order_id)
    suspend fun buyerCompleted(order_id: String, request: BuyerActionRequestMultipart)=apiHelper.buyerCompleted(order_id,request)
    suspend fun loginUser(request: LoginRequest) = apiHelper.loginUser(request)
    suspend fun addAccountDetail(request: BankDetail) = apiHelper.addAccountDetail(request)
    suspend fun getAccountDetail() = apiHelper.getAccountDetail()
    suspend fun forgotPassword(request: ForgotPasswordRequest) = apiHelper.forgotPassword(request)
    suspend fun forgotChangePassword(request: ForgetChangePasswordRequest) =
        apiHelper.forgotChangePassword(request)

    suspend fun postJob(request: PostJobRequest) = apiHelper.postJob(request)
    suspend fun acceptOrder(request: AcceptOrderRequest) = apiHelper.acceptOrder(request)
    suspend fun sendOffer(request: SendOfferRequest) = apiHelper.sendOffer(request)
    suspend fun submitQuery(request: SubmitQueryRequest) = apiHelper.submitQuery(request)
    suspend fun customOrder(request: CustomOrderRequest) = apiHelper.customOrder(request)
    suspend fun getToken(request: CardModel) = apiHelper.getToken(request)
    suspend fun chatOrder(request: ChatOfferRequest) = apiHelper.chatOrder(request)
    suspend fun sellerActions(sellerHashMap: HashMap<String, Any>) =
        apiHelper.sellerActions(sellerHashMap)

    suspend fun sellerActionsWithFile(order_id:String,request: SellerActionRequestMultipart) =
        apiHelper.sellerActionsWithFile(order_id,request)
    suspend fun orderTimeline(request: OrderTimelineRequest) = apiHelper.orderTimeline(request)
    suspend fun storeUserInfo(request: StoreUserInfoRequest) = apiHelper.storeUserInfo(request)
    suspend fun addRemoveWishlist(request: FavouriteRequest) = apiHelper.addRemoveWishlist(request)
    suspend fun addClicksGigsRequest(request: String) = apiHelper.addClicksGigsRequest(request)
    suspend fun logout() = apiHelper.logout()
    suspend fun getHomeData() = apiHelper.getHomeData()
    suspend fun getManageServices(status: String) = apiHelper.getManageServices(status)
    suspend fun getSellerServicesByID(userID: String) = apiHelper.getSellerServicesByID(userID)
    suspend fun getCategoriesCountries() = apiHelper.getCategoriesCountries()
    suspend fun getCategoriesDays() = apiHelper.getCategoriesDays()
    suspend fun getNonFreelancerProfile() = apiHelper.getNonFreelancerProfile()
    suspend fun getUserReviews() = apiHelper.getUserReviews()
    suspend fun getPrivacyTerms() = apiHelper.getPrivacyTerms()
    suspend fun getLanguageAndCurrency() = apiHelper.getLanguageAndCurrency()
    suspend fun getNotifications() = apiHelper.getNotifications()
    suspend fun getNotificationsRead() = apiHelper.getNotificationsRead()
    suspend fun getEarnings() = apiHelper.getEarnings()
    suspend fun withdrawalAmount(amount: Double) = apiHelper.withdrawalAmount(amount)
    suspend fun getSpinnerSubcategories(id: String) = apiHelper.getSpinnerSubcategories(id)
    suspend fun deletePostedJob(id: String) = apiHelper.deletePostedJob(id)
    suspend fun getBuyerOrders(id: String) = apiHelper.getBuyerOrders(id)
    suspend fun getSellersOrders(id: Int) = apiHelper.getSellersOrders(id)
    suspend fun deleteJobOffer(id: String) = apiHelper.deleteJobOffer(id)
    suspend fun deleteUserService(id: String) = apiHelper.deleteUserService(id)
    suspend fun getFreelancerProfile(id: String) = apiHelper.getFreelancerProfile(id)
    suspend fun getGigDetails(uid: String) = apiHelper.getGigDetails(uid)
    suspend fun getFeaturedGigDetails(uid: String) = apiHelper.getFeaturedGigDetails(uid)
    suspend fun cancelBuyerRequests(uid: String) = apiHelper.cancelBuyerRequests(uid)
    suspend fun becomeFreelancer(request: BecomeFreelancerRequest) =
        apiHelper.becomeFreelancer(request)

    suspend fun createService(request: CreateServiceRequest) = apiHelper.createService(request)
    suspend fun updateService(request: UpdateServiceRequest) = apiHelper.updateService(request)
    suspend fun updateProfile(request: UpdateProfileRequest) = apiHelper.updateProfile(request)
    suspend fun sendNotification(notificationRequest: FirebaseNotificationRequest) =
        apiHelper.sendNotification(notificationRequest)

    suspend fun ratingOrder(order_id: String,request: RatingOrderRequest) = apiHelper.ratingOrder(order_id,request)
    suspend fun changePassword(changePasswordRequest: ChangePasswordRequest) =
        apiHelper.changePassword(changePasswordRequest)

    suspend fun orderByID(request: Int) = apiHelper.orderByID(request)
    suspend fun extendTime(order_id: String,request: ExtendDeliveryTimeModel) = apiHelper.extendTime(order_id,request)
    suspend fun acceptExtension(order_id: String) = apiHelper.acceptExtension(order_id)
    suspend fun rejectExtension(order_id: String) = apiHelper.rejectExtension(order_id)

    suspend fun getSellerData() = apiHelper.getSellerData()

    fun getSubcategories(id: String, query: String) = Pager(
        config = PagingConfig(
            pageSize = 20,
            maxSize = 100,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            SubcategoryPagingSource(apiHelper, id, query)
        }
    ).liveData

    fun getServicesBySubCategories(id: String) = Pager(
        config = PagingConfig(
            pageSize = 20,
            maxSize = 100,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            SellersPagingSource(apiHelper, id)
        }
    ).liveData

    fun searchGigs(query: String, distance: String, filter: String, filterValue: String) = Pager(
        config = PagingConfig(
            pageSize = 20,
            maxSize = 100,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            SearchGigsPagingSource(apiHelper, query, distance, filter, filterValue)
        }
    ).liveData

    fun getReviews(id: String) = Pager(
        config = PagingConfig(
            pageSize = 10,
            maxSize = 50,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            GetGigsPagingSource(apiHelper ,  id)
        }
    ).liveData

    fun getJobOffers(id: String) = Pager(
        config = PagingConfig(
            pageSize = 20,
            maxSize = 100,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            JobOffersPagingSource(apiHelper, id)
        }
    ).liveData

    fun getMessages(query: Query) = Pager(
        config = PagingConfig(
            pageSize = 10,
            maxSize = 100,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            MessagesPagingSource(query)
        }
    ).liveData

    fun getInbox(query: Query) = Pager(
        config = PagingConfig(
            pageSize = 20,
            maxSize = 100,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            InboxPagingSource(query)
        }
    ).liveData

    fun getBuyerRequests(status: String) = Pager(
        config = PagingConfig(
            pageSize = 20,
            maxSize = 100,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            BuyerRequestsPagingSource(apiHelper, status)
        }
    ).liveData

    fun getSavedGigs() = Pager(
        config = PagingConfig(
            pageSize = 20,
            maxSize = 100,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            SavedGigsPagingSource(apiHelper)
        }
    ).liveData

    fun getPostedJobs(status: String) = Pager(
        config = PagingConfig(
            pageSize = 20,
            maxSize = 100,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            PostedJobsPagingSource(apiHelper, status)
        }
    ).liveData

}