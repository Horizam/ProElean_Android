package com.horizam.pro.elean.ui.main.view.activities

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.horizam.pro.elean.Constants
import com.horizam.pro.elean.R
import com.horizam.pro.elean.data.api.ApiHelper
import com.horizam.pro.elean.data.api.RetrofitBuilder
import com.horizam.pro.elean.data.model.response.GeneralResponse
import com.horizam.pro.elean.databinding.ActivityHomeBinding
import com.horizam.pro.elean.databinding.DialogDeleteBinding
import com.horizam.pro.elean.ui.base.ViewModelFactory
import com.horizam.pro.elean.ui.main.callbacks.DrawerHandler
import com.horizam.pro.elean.ui.main.callbacks.GenericHandler
import com.horizam.pro.elean.ui.main.callbacks.LockHandler
import com.horizam.pro.elean.ui.main.callbacks.UpdateHomeHandler
import com.horizam.pro.elean.ui.main.events.LogoutEvent
import com.horizam.pro.elean.ui.main.viewmodel.HomeViewModel
import com.horizam.pro.elean.utils.BaseUtils
import com.horizam.pro.elean.utils.PrefManager
import com.horizam.pro.elean.utils.Resource
import com.horizam.pro.elean.utils.Status
import kotlinx.android.synthetic.main.dialog_delete.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext


class HomeActivity : AppCompatActivity(), LockHandler, DrawerHandler, GenericHandler,
    UpdateHomeHandler, CoroutineScope {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var binding: ActivityHomeBinding
    private lateinit var navController: NavController
    private lateinit var viewModel: HomeViewModel
    private lateinit var prefManager: PrefManager
    private lateinit var db: FirebaseFirestore
    private lateinit var dialogDelete: Dialog
    private lateinit var bindingDeleteDialog: DialogDeleteBinding
    private var isUserFreelancer: Int = 0
    private var userId: String = ""
    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
        getData()
        setupViewModel()
        setupObservers()
        setClickListeners()
        onClickRequestPermission()
        initDeleteDialog()
        setData()
    }

    private fun initDeleteDialog() {
        dialogDelete = Dialog(this)
        bindingDeleteDialog = DialogDeleteBinding.inflate(layoutInflater)
        bindingDeleteDialog.tvTitle.text = "Are you sure you want to logout"
        dialogDelete.setContentView(bindingDeleteDialog.root)
    }

    private fun getData() {
        if (intent.hasExtra("startChat")) {
            val bundle = Bundle()
            bundle.putString("id", intent.getStringExtra("id"))
            navController.navigate(R.id.messagesFragment, bundle)
        } else if (intent.hasExtra(Constants.ORDER_ID)) {
            val intent1 = Intent(this, OrderDetailsActivity::class.java)
            intent1.putExtra(Constants.ORDER_ID, intent.getStringExtra(Constants.ORDER_ID))
            startActivity(intent1)
        }
    }

    private val locationCallback = object : LocationCallback() {
//        override fun onLocationResult(locationResult: LocationResult) {
//            super.onLocationResult(locationResult)
//            val currentLocation = locationResult.lastLocation
//            val myLocation =
//                MyLocation(lat = currentLocation.latitude, long = currentLocation.longitude)
//            prefManager.location = myLocation
//            val fcmToken = if (prefManager.fcmToken.isNotEmpty()) prefManager.fcmToken else null
//            val storeUserInfoRequest = StoreUserInfoRequest(
//                latitude = currentLocation.latitude,
//                longitude = currentLocation.longitude,
//                device_id = fcmToken
//            )
//            viewModel.storeUserInfoCall(storeUserInfoRequest)
//        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Log.i("Permission: ", "Granted")
                try {
                    fusedLocationClient.requestLocationUpdates(
                        locationRequest,
                        locationCallback,
                        Looper.getMainLooper()
                    )
                } catch (unlikely: SecurityException) {
                    Log.e(
                        this::class.java.simpleName,
                        "Lost location permissions. Couldn't remove updates. $unlikely"
                    )
                }
            } else {
                Log.i("Permission: ", "Denied")
                showMessage(
                    getString(R.string.permission_required)
                        .plus(". Please enable it settings")
                )
            }
        }

    private fun onClickRequestPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) -> {
                showSnackbar(
                    findViewById(android.R.id.content),
                    getString(R.string.permission_required),
                    Snackbar.LENGTH_INDEFINITE,
                    getString(R.string.str_ok)
                ) {
                    requestPermissionLauncher.launch(
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                }
            }
            else -> {
                requestPermissionLauncher.launch(
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            }
        }
    }

    private fun showSnackbar(
        view: View, msg: String, length: Int, actionMessage: CharSequence?,
        action: (View) -> Unit
    ) {
        val snackbar = Snackbar.make(view, msg, length)
        if (actionMessage != null) {
            snackbar.setAction(actionMessage) {
                action(findViewById(android.R.id.content))
            }.show()
        } else {
            snackbar.show()
        }
    }

    private fun checkInternet() {
        showNoInternet(!BaseUtils.isInternetAvailable(this))
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(HomeViewModel::class.java)
    }

    private fun setupObservers() {
        viewModel.logoutUser.observe(this, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        showProgressBar(false)
                        resource.data?.let { response ->
                            handleResponse(response)
                        }
                    }
                    Status.ERROR -> {
                        showProgressBar(false)
                        showMessage(it.message.toString())
                    }
                    Status.LOADING -> {
                        showProgressBar(true)
                    }
                }
            }
        })
        viewModel.userInfo.observe(this, storeUserInfoObserver)
    }

    private val storeUserInfoObserver = Observer<Resource<GeneralResponse>> {
        it?.let { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    showProgressBar(false)
                    removeLocationUpdates()
                }
                Status.ERROR -> {
                    showProgressBar(false)
                    showMessage(it.message.toString())
                }
                Status.LOADING -> {
                    showProgressBar(true)
                }
            }
        }
    }

    private fun setData() {
        Glide.with(this@HomeActivity)
            .load(Constants.BASE_URL.plus(prefManager.userImage))
            .error(R.drawable.img_profile)
            .into(binding.ivUser)
        binding.tvUserName.text = prefManager.username
        isUserFreelancer = prefManager.isFreelancer
        userId = prefManager.userId
        if (isUserFreelancer == 0) {
            binding.llBuyerBecomFreelancer.visibility = View.VISIBLE
            binding.llBuyerSeller.visibility = View.GONE
        } else {
            binding.llBuyerBecomFreelancer.visibility = View.GONE
            binding.llBuyerSeller.visibility = View.VISIBLE
        }
    }

    private fun handleResponse(response: GeneralResponse) {
        showMessage(response.message)
        logout()
    }

    private fun logout() {
        val fcmToken = prefManager.fcmToken
        prefManager.clearAll()
        prefManager.fcmToken = fcmToken
        startActivity(Intent(this, AuthenticationActivity::class.java))
        finish()
    }

    private fun executeApi() {
        showProgressBar(true)
        viewModel.logoutCall()
    }

    private fun setClickListeners() {
        binding.navLogOut.setOnClickListener {
            dialogDelete.show()
            dialogDelete.btn_yes.setOnClickListener {
                closeDrawer()
                executeApi()
                dialogDelete.dismiss()
            }
            dialogDelete.btn_no.setOnClickListener {
                dialogDelete.dismiss()
            }
        }
        binding.navSupport.setOnClickListener {
            navController.navigate(R.id.supportFragment)
        }
        binding.navNotification.setOnClickListener {
            navController.navigate(R.id.notificationsFragment)
        }
        binding.navSaved.setOnClickListener {
            navController.navigate(R.id.savedFragment)
        }
        binding.navSetting.setOnClickListener {
            navController.navigate(R.id.settingsFragment)
        }
        binding.navInbox.setOnClickListener {
            navController.navigate(R.id.inboxFragment)
        }
        binding.navBuyer.setOnClickListener {
            navController.navigate(R.id.buyerActionsFragment)
        }
        binding.navBuyerFreelancer.setOnClickListener {
            navController.navigate(R.id.buyerActionsFragment)
        }
        binding.navSeller.setOnClickListener {
            navController.navigate(R.id.sellerActionsFragment)
        }
        binding.navHome.setOnClickListener {
            binding.drawer.closeDrawer(GravityCompat.START)
        }
        binding.tvUserName.setOnClickListener {
            navigateToUserProfile()
        }
        binding.ivUser.setOnClickListener {
            navigateToUserProfile()
        }
        binding.navBecomeFreelancer.setOnClickListener {
            navController.navigate(R.id.becomeFreelancerOneFragment)
        }
        binding.navBecomeFreelancer.setOnClickListener {
            navController.navigate(R.id.becomeFreelancerOneFragment)
        }
        binding.layoutNoInternet.btnClose.setOnClickListener {
            finish()
        }
        binding.layoutNoInternet.btnRetry.setOnClickListener {
            checkInternet()
        }
    }

    private fun navigateToUserProfile() {
        if (isUserFreelancer == 0) {
            navController.navigate(R.id.userNonFreelancerFragment)
        } else {
            Intent(this, UserAboutActivity::class.java).also {
                it.putExtra("id", userId)
                startActivity(it)
            }
        }
    }

    override fun openDrawer() {
        binding.drawer.openDrawer(GravityCompat.START)
    }

    private fun closeDrawer() {
        binding.drawer.closeDrawer(GravityCompat.START)
    }

    private fun initViews() {
        job = Job()
        db = Firebase.firestore
        prefManager = PrefManager(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.create().apply {
            interval = TimeUnit.SECONDS.toMillis(60)
            fastestInterval = TimeUnit.SECONDS.toMillis(30)
            maxWaitTime = TimeUnit.MINUTES.toMillis(2)
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_home) as NavHostFragment
        navController = navHostFragment.navController
    }

    override fun lockDrawer(lock: Boolean) {
        if (lock) {
            binding.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        } else {
            if (BaseUtils.isInternetAvailable(this)) {
                binding.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            } else {
                binding.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
        }
    }

    override fun showProgressBar(show: Boolean) {
        binding.progressLayout.isVisible = show
    }

    override fun showMessage(message: String) {
        Snackbar.make(
            findViewById(android.R.id.content),
            message, Snackbar.LENGTH_LONG
        ).show()
    }

    override fun showNoInternet(show: Boolean) {
        binding.layoutNoInternet.dialogParent.isVisible = show
        lockDrawer(show)
    }

    override fun onResume() {
        super.onResume()
        checkInternet()
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
        removeLocationUpdates()
    }

    private fun removeLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLogoutEvent(event: LogoutEvent) {
        logout()
    }

    override fun callHomeApi() {
        viewModel.homeDataCall()
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }

}