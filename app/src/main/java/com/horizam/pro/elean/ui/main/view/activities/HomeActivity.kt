package com.horizam.pro.elean.ui.main.view.activities

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.Menu
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.google.android.gms.location.*
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.horizam.pro.elean.Constants
import com.horizam.pro.elean.R
import com.horizam.pro.elean.data.api.ApiHelper
import com.horizam.pro.elean.data.api.RetrofitBuilder
import com.horizam.pro.elean.data.model.BottomNotification
import com.horizam.pro.elean.data.model.response.GeneralResponse
import com.horizam.pro.elean.databinding.ActivityHomeBinding
import com.horizam.pro.elean.databinding.DialogDeleteBinding
import com.horizam.pro.elean.ui.base.ViewModelFactory
import com.horizam.pro.elean.ui.main.callbacks.*
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
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext


class HomeActivity : AppCompatActivity(), LockHandler, DrawerHandler, GenericHandler,
    UpdateHomeHandler, CoroutineScope, HideBottomNavigation, SellerActionModeHandler,
    UpdateProfileHandler {

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
    private var unreadMsg = 0
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job
    var handler: Handler = Handler()
    var runnable: Runnable? = null
    var delay = 5000
    private lateinit var appUpdateManager: AppUpdateManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        updateApp()
        initViews()
        getData()
        setupViewModel()
        setupObservers()
        setClickListeners()
        onClickRequestPermission()
        initDeleteDialog()
        setData()
        setBottomNavigation()
        setDrawerStopFromOpening()

        //call function for every 5 seconds
        handler.postDelayed(Runnable {
            handler.postDelayed(runnable!!, delay.toLong())
            getTotalNumberUnreadMsg()
        }.also { runnable = it }, delay.toLong())
    }

    private fun updateApp() {
        Log.wtf("mytag", "checking for update")
        appUpdateManager = AppUpdateManagerFactory.create(this)
        checkUpdate()
    }

    private val listener: InstallStateUpdatedListener? =
        InstallStateUpdatedListener { installState ->
            if (installState.installStatus() == InstallStatus.DOWNLOADED) {
                // After the update is downloaded, show a notification
                // and request user confirmation to restart the app.
                Log.d("mytag", "An update has been downloaded")
                showSuccessMessage("An update has been downloaded")
                appUpdateManager!!.completeUpdate()
            }
        }

    private fun checkUpdate() {
        // Returns an intent object that you use to check for an update.
        val appUpdateInfoTask = appUpdateManager?.appUpdateInfo
        // Checks that the platform will allow the specified type of update.
        Log.d("mytag", "Checking for updates")
        appUpdateInfoTask?.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
            ) {
                // Request the update.
                Log.wtf("mytag", "Update available")
                appUpdateManager.registerListener(listener!!)
                appUpdateManager.startUpdateFlowForResult(
                    // Pass the intent that is returned by 'getAppUpdateInfo()'.
                    appUpdateInfo,
                    // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                    AppUpdateType.FLEXIBLE,
                    // The current activity making the update request.
                    this,
                    // Include a request code to later monitor this update request.
                    100
                )
            } else {
                appUpdateManager.unregisterListener(listener!!)
                Log.wtf("mytag", "No Update available")
            }
        }
    }


    private fun getTotalNumberUnreadMsg() {
//        unreadMsg = 0
//        setUnreadMessages(unreadMsg)
//        db.collection(Constants.FIREBASE_DATABASE_ROOT)
//            .whereArrayContains("members", prefManager.userId)
//            .addSnapshotListener { snapshots, e ->
//                if (e != null) {
//                    Log.wtf("MyTag", "listen:error", e)
//                    return@addSnapshotListener
//                }
//                for (dc in snapshots!!.documentChanges) {
//                    when (dc.type) {
//                        DocumentChange.Type.ADDED -> {
//                            val inbox = dc.document.toObject(Inbox::class.java)
//                            for (membersInfo in inbox.membersInfo) {
//                                if ((membersInfo.id == prefManager.userId) && !membersInfo.hasReadLastMessage) {
//                                    unreadMsg++
//                                    setUnreadMessages(unreadMsg)
//                                }
//                            }
//                        }
//                        DocumentChange.Type.MODIFIED -> {
//                        }
//                        DocumentChange.Type.REMOVED -> {
//
//                        }
//                    }
//                }
//            }
    }

    private fun initDeleteDialog() {
        dialogDelete = Dialog(this)
        bindingDeleteDialog = DialogDeleteBinding.inflate(layoutInflater)
        bindingDeleteDialog.tvTitle.text = "Are you sure you want to logout"
        dialogDelete.setContentView(bindingDeleteDialog.root)
    }


    private fun getData() {
//        if (intent.hasExtra("startChat")) {
//            val bundle = Bundle()
//            bundle.putString("id", intent.getStringExtra("id"))
//            navController.navigate(R.id.messagesFragment, bundle)
//        }
        val bundle = intent.extras
        if (bundle != null) {
            if (bundle!!.containsKey(Constants.TYPE)) {
                if (bundle.get(Constants.TYPE) == Constants.TYPE_ORDER) {
                    val intent1 = Intent(this, OrderDetailsActivity::class.java)
                    intent1.putExtra(
                        Constants.ORDER_ID,
                        bundle.get(Constants.CONTENT_ID).toString()
                    )
                    intent.removeExtra(Constants.CONTENT_ID)
                    startActivity(intent1)
                }
            }
        }
    }

    private val locationCallback = object : LocationCallback() {
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
                showErrorMessage(
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
                        showErrorMessage(it.message.toString())
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
                    showErrorMessage(it.message.toString())
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

    private fun setDrawerStopFromOpening() {
        binding.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    private fun handleResponse(response: GeneralResponse) {
        showSuccessMessage(response.message)
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
//        if (isUserFreelancer == 0) {
//            navController.navigate(R.id.userNonFreelancerFragment)
//        } else {
//            Intent(this, UserAboutActivity::class.java).also {
//                it.putExtra("id", userId)
//                startActivity(it)
//            }
//        }
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
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
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

    override fun showErrorMessage(message: String) {
        val snackbar = Snackbar.make(
            findViewById(android.R.id.content),
            message, Snackbar.LENGTH_LONG
        )
        val snackBarView = snackbar.view
        val tvMessage =
            snackBarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        tvMessage.setTextColor(ResourcesCompat.getColor(resources, R.color.colorWhite, null))
        tvMessage.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tvMessage.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        } else {
            tvMessage.setGravity(Gravity.CENTER_HORIZONTAL);
        }
        snackBarView.setBackgroundColor(
            ResourcesCompat.getColor(
                resources,
                R.color.color_red,
                null
            )
        )
        snackbar.show()
    }

    override fun showSuccessMessage(message: String) {
        val snackbar = Snackbar.make(
            findViewById(android.R.id.content),
            message, Snackbar.LENGTH_LONG
        )
        val snackBarView = snackbar.view
        val tvMessage =
            snackBarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        tvMessage.setTextColor(ResourcesCompat.getColor(resources, R.color.colorWhite, null))
        tvMessage.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tvMessage.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        } else {
            tvMessage.setGravity(Gravity.CENTER_HORIZONTAL);
        }
        snackBarView.setBackgroundColor(
            ResourcesCompat.getColor(
                resources,
                R.color.color_green,
                null
            )
        )
        snackbar.show()
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(bottomNotification: BottomNotification) {
        if ((bottomNotification.type == Constants.MESSAGE) && (bottomNotification.value == 1)) {
            val a = binding.bottomNav.selectedItemId
            val b = R.id.inboxFragment
            if (binding.bottomNav.selectedItemId != R.id.inboxFragment) {
                setMessageBottomNotification(1)
            }
        } else if (bottomNotification.type == Constants.MESSAGE && (bottomNotification.value == 0)) {
            setMessageBottomNotification(0)
        } else if (bottomNotification.type == Constants.ORDER && (bottomNotification.value == 1)) {
            if (binding.bottomNav.selectedItemId != R.id.orderFragment) {
                setOrderBottomNotification(1)
            }
        } else if (bottomNotification.type == Constants.ORDER && (bottomNotification.value == 0)) {
            setOrderBottomNotification(0)
        }
    }

    private fun setOrderBottomNotification(value: Int) {
        val bageDashboard: BadgeDrawable = binding.bottomNav.getOrCreateBadge(R.id.orderFragment)
        bageDashboard.backgroundColor = ContextCompat.getColor(this, R.color.color_light_green)
        bageDashboard.badgeTextColor = Color.WHITE
        bageDashboard.isVisible = value == 1
    }

    private fun setMessageBottomNotification(value: Int) {
        val bageDashboard: BadgeDrawable = binding.bottomNav.getOrCreateBadge(R.id.inboxFragment)
        bageDashboard.backgroundColor = ContextCompat.getColor(this, R.color.color_light_green)
        bageDashboard.badgeTextColor = Color.WHITE
        bageDashboard.isVisible = value == 1
    }

    override fun callHomeApi() {
        viewModel.homeDataCall()
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }

    private fun setBottomNavigation() {
        binding.bottomNav.setupWithNavController(navController)
        if (prefManager.sellerMode == 1) {
            setSellerBottomNavigation()
        } else {
            setBuyerBottomNavigation()
        }
    }

    private fun setBuyerBottomNavigation() {
        val menu = binding.bottomNav.menu
        menu.clear()
        menu.add(Menu.NONE, R.id.homeFragment, Menu.NONE, "Home")
            .setIcon(R.drawable.img_home)
        menu.add(Menu.NONE, R.id.inboxFragment, Menu.NONE, "Inbox")
            .setIcon(R.drawable.img_inbox)
        menu.add(Menu.NONE, R.id.serviceGigsFragment, Menu.NONE, "Search")
            .setIcon(R.drawable.img_search)
        menu.add(Menu.NONE, R.id.ordersFragment, Menu.NONE, "Order")
            .setIcon(R.drawable.img_order)
        menu.add(Menu.NONE, R.id.profile_fragment, Menu.NONE, "Profile")
            .setIcon(R.drawable.img_profile_)
        binding.bottomNav.selectedItemId = R.id.homeFragment
        setStartDestinationBuyer()
    }

    private fun setSellerBottomNavigation() {
        val menu = binding.bottomNav.menu
        menu.clear()
        menu.add(Menu.NONE, R.id.sellerActionsFragment, Menu.NONE, "Home")
            .setIcon(R.drawable.img_home)
        menu.add(Menu.NONE, R.id.inboxFragment, Menu.NONE, "Inbox")
            .setIcon(R.drawable.img_inbox)
        menu.add(Menu.NONE, R.id.salesFragment, Menu.NONE, "Order")
            .setIcon(R.drawable.img_order)
        menu.add(Menu.NONE, R.id.profile_fragment, Menu.NONE, "Profile")
            .setIcon(R.drawable.img_profile_)
        binding.bottomNav.selectedItemId = R.id.sellerActionsFragment
        setStartDestinationSeller()
    }

    private fun setStartDestinationSeller() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val navGraph = navController.navInflater.inflate(R.navigation.nav_home)
        navGraph.startDestination = R.id.sellerActionsFragment
        navController.graph = navGraph
        binding.bottomNav.setupWithNavController(navController)
    }

    private fun setStartDestinationBuyer() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val navGraph = navController.navInflater.inflate(R.navigation.nav_home)
        navGraph.startDestination = R.id.homeFragment
        navController.graph = navGraph
        binding.bottomNav.setupWithNavController(navController)
    }

    override fun hideNavigation() {
        binding.bottomNav.visibility = View.GONE
    }

    override fun showNavigation() {
        binding.bottomNav.visibility = View.VISIBLE
    }

    override fun sellerActionMode(state: Int) {
        binding.showWhiteScreen.visibility = View.VISIBLE
        if (state == 0) {
            val menu = binding.bottomNav.menu
            menu.clear()
            menu.add(Menu.NONE, R.id.homeFragment, Menu.NONE, "Home")
                .setIcon(R.drawable.img_home)
            menu.add(Menu.NONE, R.id.inboxFragment, Menu.NONE, "Inbox")
                .setIcon(R.drawable.img_inbox)
            menu.add(Menu.NONE, R.id.serviceGigsFragment, Menu.NONE, "Search")
                .setIcon(R.drawable.img_search)
            menu.add(Menu.NONE, R.id.ordersFragment, Menu.NONE, "Order")
                .setIcon(R.drawable.img_order)
            menu.add(Menu.NONE, R.id.profile_fragment, Menu.NONE, "Profile")
                .setIcon(R.drawable.img_profile_)
            Handler().postDelayed({
                binding.showWhiteScreen.visibility = View.INVISIBLE
            }, 500)
            setStartDestinationBuyer()
            binding.bottomNav.selectedItemId = R.id.profile_fragment
        } else {
            val menu = binding.bottomNav.menu
            menu.clear()
            menu.add(Menu.NONE, R.id.sellerActionsFragment, Menu.NONE, "Home")
                .setIcon(R.drawable.img_home)
            menu.add(Menu.NONE, R.id.inboxFragment, Menu.NONE, "Inbox")
                .setIcon(R.drawable.img_inbox)
            menu.add(Menu.NONE, R.id.salesFragment, Menu.NONE, "Order")
                .setIcon(R.drawable.img_order)
            menu.add(Menu.NONE, R.id.profile_fragment, Menu.NONE, "Profile")
                .setIcon(R.drawable.img_profile_)
            Handler().postDelayed({
                binding.showWhiteScreen.visibility = View.INVISIBLE
            }, 200)
            setStartDestinationSeller()
            binding.bottomNav.selectedItemId = R.id.profile_fragment
        }
    }

    override fun updateProfile() {
        setData()
    }
}