package com.horizam.pro.elean.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.horizam.pro.elean.data.api.ApiHelper
import com.horizam.pro.elean.data.repository.MainRepository
import com.horizam.pro.elean.ui.main.viewmodel.*

class ViewModelFactory(private val apiHelper: ApiHelper) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(MainRepository(apiHelper)) as T
            }
            modelClass.isAssignableFrom(SellerViewModel::class.java) -> {
                SellerViewModel(MainRepository(apiHelper)) as T
            }
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(MainRepository(apiHelper)) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(MainRepository(apiHelper)) as T
            }
            modelClass.isAssignableFrom(ForgotPasswordViewModel::class.java) -> {
                ForgotPasswordViewModel(MainRepository(apiHelper)) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(MainRepository(apiHelper)) as T
            }
            modelClass.isAssignableFrom(ServiceCategoriesViewModel::class.java) -> {
                ServiceCategoriesViewModel(MainRepository(apiHelper)) as T
            }
            modelClass.isAssignableFrom(ServiceGigsViewModel::class.java) -> {
                ServiceGigsViewModel(MainRepository(apiHelper)) as T
            }
            modelClass.isAssignableFrom(GigDetailsViewModel::class.java) -> {
                GigDetailsViewModel(MainRepository(apiHelper)) as T
            }
            modelClass.isAssignableFrom(FeaturedGigDetailsViewModel::class.java) -> {
                FeaturedGigDetailsViewModel(MainRepository(apiHelper)) as T
            }
            modelClass.isAssignableFrom(SupportViewModel::class.java) -> {
                SupportViewModel(MainRepository(apiHelper)) as T
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(MainRepository(apiHelper)) as T
            }
            modelClass.isAssignableFrom(PostJobViewModel::class.java) -> {
                PostJobViewModel(MainRepository(apiHelper)) as T
            }
            modelClass.isAssignableFrom(PostedJobsViewModel::class.java) -> {
                PostedJobsViewModel(MainRepository(apiHelper)) as T
            }
            modelClass.isAssignableFrom(JobOffersViewModel::class.java) -> {
                JobOffersViewModel(MainRepository(apiHelper)) as T
            }
            modelClass.isAssignableFrom(BecomeFreelancerViewModel::class.java) -> {
                BecomeFreelancerViewModel(MainRepository(apiHelper)) as T
            }
            modelClass.isAssignableFrom(BuyerRequestsViewModel::class.java) -> {
                BuyerRequestsViewModel(MainRepository(apiHelper)) as T
            }
            modelClass.isAssignableFrom(SavedViewModel::class.java) -> {
                SavedViewModel(MainRepository(apiHelper)) as T
            }
            modelClass.isAssignableFrom(ManageServicesViewModel::class.java) -> {
                ManageServicesViewModel(MainRepository(apiHelper)) as T
            }
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                SettingsViewModel(MainRepository(apiHelper)) as T
            }
            modelClass.isAssignableFrom(MessagesViewModel::class.java) -> {
                MessagesViewModel(MainRepository(apiHelper)) as T
            }
            modelClass.isAssignableFrom(InboxViewModel::class.java) -> {
                InboxViewModel(MainRepository(apiHelper)) as T
            }
            modelClass.isAssignableFrom(NotificationsViewModel::class.java) -> {
                NotificationsViewModel(MainRepository(apiHelper)) as T
            }
            modelClass.isAssignableFrom(CheckOutViewModel::class.java) -> {
                CheckOutViewModel(MainRepository(apiHelper)) as T
            }
            modelClass.isAssignableFrom(SellerServicesViewModel::class.java) -> {
                SellerServicesViewModel(MainRepository(apiHelper)) as T
            }
            modelClass.isAssignableFrom(BuyersOrdersViewModel::class.java) -> {
                BuyersOrdersViewModel(MainRepository(apiHelper)) as T
            }
            modelClass.isAssignableFrom(SellerOrdersViewModel::class.java) -> {
                SellerOrdersViewModel(MainRepository(apiHelper)) as T
            }
            modelClass.isAssignableFrom(EarningsViewModel::class.java) -> {
                EarningsViewModel(MainRepository(apiHelper)) as T
            }
            else -> throw IllegalArgumentException("Unknown class name")
        }
    }

}

