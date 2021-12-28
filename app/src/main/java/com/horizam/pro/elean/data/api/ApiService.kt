package com.horizam.pro.elean.data.api

import com.horizam.pro.elean.data.model.requests.*
import com.horizam.pro.elean.data.model.response.*
import com.horizam.pro.elean.ui.main.viewmodel.FirebaseNotification
import retrofit2.http.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.PartMap
import retrofit2.http.POST
import retrofit2.http.Multipart


interface ApiService {

    @POST("register")
    suspend fun registerUser(
        @Body request: RegisterRequest,
        @Query("referal_code") referralCode: String
    ): RegisterResponse

    @POST("login")
    suspend fun loginUser(@Body request: LoginRequest): LoginResponse

    @POST("forgot_password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): GeneralResponse

    @POST("seller/send_offer")
    suspend fun sendOffer(@Body request: SendOfferRequest): BuyerRequest

    @POST("buyer/jobs")
    suspend fun postJob(@Body request: PostJobRequest): PostedJobResponse

    @POST("orders")
    suspend fun acceptOrder(@Body request: AcceptOrderRequest): GeneralResponse

    @Multipart
    @POST("become_freelancer")
    suspend fun becomeFreelancer(
        @PartMap partMap: HashMap<String, RequestBody>,
        @Part file: MultipartBody.Part
    ): GeneralResponse

    @Multipart
    @POST("seller/services")
    suspend fun createService(
        @PartMap partMap: HashMap<String, RequestBody>,
        @Part images: ArrayList<MultipartBody.Part>
    ): ServiceResponse

    @Multipart
    @POST("seller/services/{id}")
    suspend fun updateService(
        @PartMap partMap: HashMap<String, RequestBody>,
        @Part images: ArrayList<MultipartBody.Part>,
        @Part("delete[]") deletedImages: ArrayList<String>,
        @Path("id") id: String
    ): ServiceResponse

    @Multipart
    @POST("update_profile")
    suspend fun updateProfile(
        @PartMap partMap: HashMap<String, RequestBody>,
        @Part image: MultipartBody.Part?
    ): ProfileInfo

    @POST("logout")
    suspend fun logout(): GeneralResponse

    @GET("home")
    suspend fun getHomeData(): HomeDataResponse

    @GET("categories")
    suspend fun getCategoriesDays(): CategoriesDaysResponse

    @GET("countries&categories")
    suspend fun getCategoriesCountries(): CategoriesCountriesResponse

    @GET("profile")
    suspend fun getNonFreelancerProfile(): ProfileInfo

    @GET("term_condition")
    suspend fun getPrivacyTerms(): PrivacyPolicyResponse

    @GET("lang-currency")
    suspend fun getLanguageAndCurrency(): LanguageAndCurrencyResponse

    @GET("notification")
    suspend fun getNotifications(): NotificationsResponse

    @GET("seller_info/{id}")
    suspend fun getFreelancerProfile(@Path("id") id: Int): FreelancerUserResponse

    @GET("subcategories/{id}")
    suspend fun getSpinnerSubcategories(@Path("id") id: String): SubcategoriesDataResponse

    @GET("subcategories/{id}")
    suspend fun getSubcategories(
        @Path("id") id: String,
        @Query("page") page: Int,
        @Query("q") query: String
    ): SubcategoriesResponse

    @GET("categories/{id}/services")
    suspend fun getServicesBySubCategories(@Path("id") id: String, @Query("page") page: Int): ServicesResponse

    @GET("search")
    suspend fun searchGigs(
        @Query("q") query: String,
        @Query("distance") distance: String,
        @Query("filter") filter: String,
        @Query("filter_value") filter_value: String,
        @Query("page") page: Int
    ): ServicesResponse

    @GET("job_requests/{id}")
    suspend fun getJobOffers(@Path("id") id: Int, @Query("page") page: Int): JobOffersResponse

    @GET("seller/buyer_requests")
    suspend fun getBuyerRequests(
        @Query("page") page: Int,
        @Query("status") status: String
    ): BuyerRequestsResponse

    @GET("seller/services")
    suspend fun getManageServices(@Query("status") status: String): ServicesResponse

    @GET("get_wishlist")
    suspend fun getSavedGigs(@Query("page") page: Int): ServicesResponse

    @GET("buyer/jobs")
    suspend fun getPostedJobs(
        @Query("page") page: Int,
        @Query("status") status: String
    ): PostedJobsResponse

    @GET("service/{uid}")
    suspend fun getGigDetails(@Path("uid") uid: String): GigDetailsResponse

    @GET("seller/services/{uid}")
    suspend fun getFeaturedGigDetails(@Path("uid") uid: String): ServiceResponse

    @POST("customer_support")
    suspend fun submitQuery(@Body request: SubmitQueryRequest): GeneralResponse

    @POST("buyer/custom_order")
    suspend fun customOrder(@Body request: CustomOrderRequest): GeneralResponse

    @POST("chat/order")
    suspend fun chatOrder(@Body request: ChatOfferRequest): GeneralResponse

    @GET("order/{order_no}/activity")
    suspend fun orderTimeline(@Path("order_no") orderNo: String): OrderTimelineResponse

    @POST("store_user_info")
    suspend fun storeUserInfo(@Body request: StoreUserInfoRequest): GeneralResponse

    @POST("wishlist")
    suspend fun addRemoveWishlist(@Body request: FavouriteRequest): GeneralResponse

    @FormUrlEncoded
    @POST("seller/manage_orders")
    suspend fun sellerActions(
        @FieldMap sellerHashMap: HashMap<String, Any>
    ): GeneralResponse

    @FormUrlEncoded
    @POST("buyer/manage_order")
    suspend fun buyerActions(
        @FieldMap buyerHashMap: HashMap<String, Any>
    ): GeneralResponse

    @Multipart
    @POST("seller/manage_orders")
    suspend fun sellerActionsWithFile(
        @Part("order_no") orderNumber: RequestBody,
        @Part("type") typeUser: RequestBody,
        @Part("delivery_note") deliveryNote: RequestBody,
        @Part image: MultipartBody.Part,
    ): GeneralResponse

    @DELETE("buyer/jobs/{id}")
    suspend fun deletePostedJob(@Path("id") id: String): GeneralResponse

    @GET("buyer/orders/{id}")
    suspend fun getBuyerOrders(@Path("id") id: Int): OrdersResponse

    @GET("seller_earning")
    suspend fun getEarnings(): EarningsResponse

    @GET("seller/orders/{id}")
    suspend fun getSellersOrders(@Path("id") id: Int): OrdersResponse

    @DELETE("delete_job_requests/{id}")
    suspend fun deleteJobOffer(@Path("id") id: Int): GeneralResponse

    @DELETE("seller/services/{id}")
    suspend fun deleteUserService(@Path("id") id: String): GeneralResponse

    @DELETE("seller/cancel_offer/{uid}")
    suspend fun cancelBuyerRequests(@Path("uid") uid: String): GeneralResponse

    @POST("buyer/manage_order")
    suspend fun ratingOrder(@Body request: RatingOrderRequest): GeneralResponse

    @POST("change_password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): GeneralResponse

    @GET("get_order/{id}")
    suspend fun orderByID(@Path("id") orderId: Int): OrderResponse

    @Headers(
        "authorization:key=AAAACZscWGo:APA91bFaEgC97RCJ7cLWT75xnjuBGFe12IfUSNvedfoZjzyEY5tahwSRjpGG94aprnH8EFjx5CIqNE0d3fepm5pHRut6T6xkotvRvbN4Exoduzo8uS594-q7kymeoKq9lAAuTMdLqmtn",
        "content-type:application/json"
    )
    @POST
    suspend fun sendFirebaseNotification(
        @Url url: String?, @Body rootModel: FirebaseNotification?
    ): GeneralResponse

}