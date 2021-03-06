package com.horizam.pro.elean.data.api

import com.horizam.pro.elean.data.model.requests.*
import com.horizam.pro.elean.ui.main.viewmodel.FirebaseNotificationRequest

class ApiHelper(private val apiService: ApiService) {

    suspend fun registerUser(request: RegisterRequest, referralCode: String) =
        apiService.registerUser(request, referralCode)

    suspend fun loginUser(request: LoginRequest) = apiService.loginUser(request)
    suspend fun addAccountDetail(request: BankDetail) = apiService.addAccountDetail(request)
    suspend fun getAccountDetail() = apiService.getAccountDetail()
    suspend fun forgotPassword(request: ForgotPasswordRequest) = apiService.forgotPassword(request)
    suspend fun forgotChangePassword(request: ForgetChangePasswordRequest) =
        apiService.forgotChangePassword(request)

    suspend fun postJob(request: PostJobRequest) = apiService.postJob(request)
    suspend fun acceptOrder(request: AcceptOrderRequest) = apiService.acceptOrder(request)
    suspend fun sendOffer(request: SendOfferRequest) = apiService.sendOffer(request)
    suspend fun submitQuery(request: SubmitQueryRequest) = apiService.submitQuery(request)
    suspend fun customOrder(request: CustomOrderRequest) = apiService.customOrder(request)
    suspend fun getToken(request: CardModel) = apiService.getToken(request)
    suspend fun chatOrder(request: ChatOfferRequest) = apiService.chatOrder(request)
    suspend fun sellerActions(sellerHashMap: HashMap<String, Any>) =
        apiService.sellerActions(sellerHashMap)

    suspend fun sellerActionsWithFile(request: SellerActionRequestMultipart) =
        apiService.sellerActionsWithFile(
            request.orderNumber,
            request.typeUser,
            request.deliveryNote,
            request.image
        )

    suspend fun buyerActions(buyerHashMap: HashMap<String, Any>) =
        apiService.buyerActions(buyerHashMap)

    suspend fun orderTimeline(request: OrderTimelineRequest) =
        apiService.orderTimeline(request.order_no)

    suspend fun storeUserInfo(request: StoreUserInfoRequest) = apiService.storeUserInfo(request)
    suspend fun addRemoveWishlist(request: FavouriteRequest) = apiService.addRemoveWishlist(request)
    suspend fun addClicksGigsRequest(request: String) = apiService.addClicksGigsRequest(request)
    suspend fun logout() = apiService.logout()
    suspend fun getHomeData() = apiService.getHomeData()
    suspend fun getCategoriesCountries() = apiService.getCategoriesCountries()
    suspend fun getCategoriesDays() = apiService.getCategoriesDays()
    suspend fun getNonFreelancerProfile() = apiService.getNonFreelancerProfile()
    suspend fun getUserReviews() = apiService.getUserReviews()
    suspend fun getPrivacyTerms() = apiService.getPrivacyTerms()
    suspend fun getLanguageAndCurrency() = apiService.getLanguageAndCurrency()
    suspend fun getNotifications() = apiService.getNotifications()
    suspend fun getEarnings() = apiService.getEarnings()
    suspend fun withdrawalAmount(amount: Double) = apiService.withdrawalAmount(amount)
    suspend fun getFreelancerProfile(id: String) = apiService.getFreelancerProfile(id)
    suspend fun getSpinnerSubcategories(id: String) = apiService.getSpinnerSubcategories(id)
    suspend fun deletePostedJob(id: String) = apiService.deletePostedJob(id)
    suspend fun getBuyerOrders(id: String) = apiService.getBuyerOrders(id)
    suspend fun getSellersOrders(id: Int) = apiService.getSellersOrders(id)
    suspend fun deleteJobOffer(id: String) = apiService.deleteJobOffer(id)
    suspend fun deleteUserService(id: String) = apiService.deleteUserService(id)
    suspend fun getGigDetails(uid: String) = apiService.getGigDetails(uid)
    suspend fun getFeaturedGigDetails(uid: String) = apiService.getFeaturedGigDetails(uid)
    suspend fun cancelBuyerRequests(uid: String) = apiService.cancelBuyerRequests(uid)
    suspend fun getSubcategories(id: String, position: Int, query: String) =
        apiService.getSubcategories(id, position, query)

    suspend fun getServicesBySubCategories(id: String, position: Int) =
        apiService.getServicesBySubCategories(id, position)

    suspend fun searchGigs(
        query: String,
        distance: String,
        filter: String,
        filterValue: String,
        position: Int
    ) = apiService.searchGigs(query, filter, filterValue, position)

    suspend fun getReviews(
        id: String
    ) = apiService.getReviews(id)

    suspend fun getJobOffers(id: String, position: Int) = apiService.getJobOffers(id, position)
    suspend fun getBuyerRequests(position: Int, status: String) =
        apiService.getBuyerRequests(position, status)

    suspend fun getManageServices(status: String) = apiService.getManageServices(status)
    suspend fun getSellerServicesByID(userID: String) = apiService.getSellerServicesByID(userID)
    suspend fun getSavedGigs(position: Int) = apiService.getSavedGigs(position)
    suspend fun getPostedJobs(position: Int, status: String) =
        apiService.getPostedJobs(position, status)

    suspend fun becomeFreelancer(request: BecomeFreelancerRequest) =
        apiService.becomeFreelancer(request.partMap, request.file)

    suspend fun createService(request: CreateServiceRequest) =
        apiService.createService(request.partMap, request.images)

    suspend fun updateService(request: UpdateServiceRequest) =
        apiService.updateService(request.partMap, request.images, request.deletedImages, request.id)

    suspend fun updateProfile(request: UpdateProfileRequest) =
        apiService.updateProfile(request.partMap, request.image)

    suspend fun ratingOrder(request: RatingOrderRequest) = apiService.ratingOrder(request)
    suspend fun changePassword(changePasswordRequest: ChangePasswordRequest) =
        apiService.changePassword(changePasswordRequest)

    suspend fun orderByID(request: Int) = apiService.orderByID(request)
    suspend fun extendTime(request: ExtendDeliveryTimeModel) = apiService.extendTime(request)
    suspend fun getSellerData() = apiService.getSellerData()
    suspend fun sendNotification(notificationRequest: FirebaseNotificationRequest) =
        apiService.sendFirebaseNotification(
            notificationRequest
        )
}