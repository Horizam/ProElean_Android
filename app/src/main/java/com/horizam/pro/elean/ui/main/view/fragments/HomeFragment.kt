package com.horizam.pro.elean.ui.main.view.fragments


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.horizam.pro.elean.Constants
import com.horizam.pro.elean.R
import com.horizam.pro.elean.data.api.ApiHelper
import com.horizam.pro.elean.data.api.RetrofitBuilder
import com.horizam.pro.elean.data.model.SliderItem
import com.horizam.pro.elean.data.model.SpinnerModel
import com.horizam.pro.elean.data.model.response.*
import com.horizam.pro.elean.databinding.FragmentHomeBinding
import com.horizam.pro.elean.ui.base.ViewModelFactory
import com.horizam.pro.elean.ui.main.adapter.*
import com.horizam.pro.elean.ui.main.callbacks.*
import com.horizam.pro.elean.ui.main.view.activities.ManageSalesActivity
import com.horizam.pro.elean.ui.main.viewmodel.HomeViewModel
import com.horizam.pro.elean.ui.main.viewmodel.NotificationsViewModel
import com.horizam.pro.elean.utils.BaseUtils.Companion.hideKeyboard
import com.horizam.pro.elean.utils.PrefManager
import com.horizam.pro.elean.utils.Status
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView


@Suppress("DEPRECATION")
class HomeFragment : Fragment(), OnItemClickListener, SwipeRefreshLayout.OnRefreshListener,
    NotificationsHandler , AdapterView.OnItemSelectedListener {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapterServices: ServicesAdapter
    private lateinit var adapterGigs: HomeGigsAdapter
    private lateinit var viewModel: HomeViewModel
    private lateinit var notificationsViewModel: NotificationsViewModel
    private lateinit var genericHandler: GenericHandler
    private lateinit var drawerHandler: DrawerHandler
    private lateinit var prefManager: PrefManager
    private lateinit var lockHandler: LockHandler
    private lateinit var adapter: NotificationsAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var servicesArrayList: List<SpinnerModel>
    private lateinit var generalServicesArrayList: List<Category>
    private var sum: Int = 0
    private var count = 0
    private var n: Int = 0
    private lateinit var servicesAdapter: ArrayAdapter<SpinnerModel>
    private var serviceId = ""
    private var sliderView: SliderView? = null
    private var sliderAdapter: SliderAdapter? = null
    private lateinit var mAuth: FirebaseAuth
    private var check:Int=1
    override fun onAttach(context: Context) {
        super.onAttach(context)
        genericHandler = context as GenericHandler
        lockHandler = context as LockHandler
        drawerHandler = context as DrawerHandler
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)

        getIntentData()
        initViews()
        setupViewModel()
        setupObservers()
        setRecyclerViews()
        setAdsSlider()
        setClickListeners()
        executeApi()
        return binding.root
    }

    private fun executeApi() {
        genericHandler.showProgressBar(true)
        viewModel.homeDataCall()
        notificationsViewModel.getNotificationsCall()
    }

    private fun getIntentData() {
        prefManager = PrefManager(requireActivity())
        if (prefManager.sellerMode == 0) {
            if (requireActivity().intent.hasExtra(Constants.TYPE)) {
                if (requireActivity().intent.getStringExtra(Constants.TYPE) == Constants.MESSAGE) {
                    val bundle = requireActivity().intent.extras
                    val id = bundle!!.getString(Constants.SENDER_ID)
                    HomeFragmentDirections.actionHomeFragmentToMessageFragment(id!!).also {
                        findNavController().navigate(it)
                        requireActivity().intent.removeExtra(Constants.TYPE)
                    }
                } else if ((requireActivity().intent.getStringExtra(Constants.TYPE)) == Constants.TYPE_OFFER) {
                    val bundle = requireActivity().intent.extras
                    bundle!!.getString(Constants.CONTENT_ID)
                    findNavController().navigate(R.id.postedJobsFragment)
                    requireActivity().intent.removeExtra(Constants.TYPE)
                }
            }

            if (requireActivity().intent.hasExtra("order")) {
                if (requireActivity().intent.getIntExtra("order", 0) == 1) {
                    this.findNavController().navigate(R.id.ordersFragment)
                }
                requireActivity().intent.removeExtra("order")
            }
        }
    }

    private fun initViews() {
        mAuth = FirebaseAuth.getInstance()
        adapterServices = ServicesAdapter(this)
        adapterGigs = HomeGigsAdapter(this)
        adapter = NotificationsAdapter(this)
        sliderAdapter = SliderAdapter(this)
        swipeRefreshLayout = binding.swipeRefresh
        swipeRefreshLayout.setOnRefreshListener(this)
        binding.toolbar.ivSecond.setImageResource(R.drawable.ic_notifications)
        binding.toolbar.rlNoOfNotification.isVisible = false
        binding.toolbar.ivSecond.visibility = View.VISIBLE
        binding.mainLayout.isVisible = true
        binding.toolbar.ivSale.visibility = View.GONE
        binding.toolbar.tvToolbar.visibility = View.GONE
        binding.toolbar.ivLogoToolbar.visibility = View.VISIBLE
    }
    private fun notificationsRes(list: List<Notification>) {
        val notify = list
        count = notify.count()
        for (n in 0 until count) {
            if (notify[n].viewed == 0) {
                sum = sum + 1

            } else {
                println("is 1")

            }

        }
        println(sum)
        binding.toolbar.tvNoOfNotification.setText(sum.toString())
        binding.toolbar.rlNoOfNotification.isVisible = true
        binding.toolbar.tvNoOfNotification.isVisible = true
        if (sum == 0) {
            binding.toolbar.rlNoOfNotification.isVisible = false
            binding.toolbar.tvNoOfNotification.isVisible = false
        }
        sum = 0
    }

    private fun setRecyclerViews() {
        serviceRecyclerview()
        gigsRecyclerview()
    }

    private fun gigsRecyclerview() {
        binding.rvFeaturedGigs.apply {
            val gridLayoutManager = GridLayoutManager(requireContext(), 1)
            layoutManager = gridLayoutManager
            adapter = adapterGigs
        }
    }

    @SuppressLint("RtlHardcoded")
    private fun setServicesData(response: HomeDataResponse) {
        if (response.data.categories?.isNotEmpty()!!) {
            generalServicesArrayList = response.data.categories
            servicesArrayList = response.data.categories.map { spinnerServices ->
                SpinnerModel(id = spinnerServices.slug, value = spinnerServices.title)
            }
            servicesAdapter = SpinnerAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item, servicesArrayList
            ).also {
                it.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                binding.spinnerDoctor.adapter = it
                binding.spinnerDoctor.setPrompt("Select Category")
            }
            binding.spinnerDoctor.onItemSelectedListener = this
        }
    }
    private fun serviceRecyclerview() {
        binding.rvServiceCategories.apply {
            setHasFixedSize(true)
            val gridLayoutManager = GridLayoutManager(requireContext(), 1)
            layoutManager = gridLayoutManager
            adapter = adapterServices
        }
    }

    private fun setAdsSlider() {
        sliderView?.let {
            it.setSliderAdapter(sliderAdapter!!)
            //it.setIndicatorAnimation(IndicatorAnimationType.WORM) //set indicator animation by using IndicatorAnimationType. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
            it.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
            it.autoCycleDirection = SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH
            //it.indicatorSelectedColor = Color.WHITE
            //it.indicatorUnselectedColor = Color.GRAY
            it.scrollTimeInSec = 2 //set scroll delay in seconds :
            it.startAutoCycle()
        }
    }

    private fun setClickListeners() {
        binding.btnRetry.setOnClickListener {
            executeApi()
        }
        binding.btnSearch.setOnClickListener {
            binding.autoCompleteTextView.onFocusChangeListener = focusChangeListener
            val bundle = Bundle()
                bundle.putString("q", binding.autoCompleteTextView.text.toString().trim())
                bundle.putString("slug", serviceId)
                bundle.putString("check",check.toString())
                this.findNavController().navigate(R.id.serviceGigsFragment, bundle)
        }
        binding.toolbar.ivSecond.setOnClickListener {
            this.findNavController().navigate(R.id.notificationsFragment)
        }
        binding.toolbar.ivSale.setOnClickListener {
            startActivity(Intent(requireActivity(), ManageSalesActivity::class.java))
        }
    }
    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            requireActivity(),
            ViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(HomeViewModel::class.java)
        notificationsViewModel = ViewModelProviders.of(
            requireActivity(), ViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(NotificationsViewModel::class.java)
    }

    private fun setupObservers() {
        viewModel.homeData.observe(viewLifecycleOwner) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        genericHandler.showProgressBar(false)
                        resource.data?.let { response ->
                            handleResponse(response)
                            changeViewVisibility(textView = false, button = false, layout = true)
                        }
                    }
                    Status.ERROR -> {
                        genericHandler.showProgressBar(false)
                        genericHandler.showErrorMessage(it.message.toString())
                        changeViewVisibility(textView = true, button = true, layout = false)
                    }
                    Status.LOADING -> {
                        genericHandler.showProgressBar(true)
                        changeViewVisibility(textView = false, button = false, layout = false)
                    }
                }
            }
        }
        notificationsViewModel.notifications.observe(viewLifecycleOwner) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        genericHandler.showProgressBar(false)
                        resource.data?.let { response ->
                            handleResponse(response)
                            changeViewVisibility(textView = false, button = false, layout = true)
                        }
                    }
                    Status.ERROR -> {
                        genericHandler.showProgressBar(false)
                        genericHandler.showErrorMessage(it.message.toString())
                        changeViewVisibility(textView = true, button = true, layout = false)
                    }
                    Status.LOADING -> {
                        genericHandler.showProgressBar(true)
                        changeViewVisibility(textView = false, button = false, layout = false)
                    }
                }
            }
        }
    }

    private fun handleResponse(response: NotificationsResponse) {
        try {
            setUIData(response.data)
            notificationsRes(response.data)
        } catch (e: java.lang.Exception) {
            genericHandler.showErrorMessage(e.message.toString())
        }
    }

    private fun setUIData(list: List<Notification>) {
        adapter.submitList(list)
    }

    private fun changeViewVisibility(textView: Boolean, button: Boolean, layout: Boolean) {
        binding.textViewError.isVisible = textView
        binding.btnRetry.isVisible = button
        binding.mainLayout.isVisible = layout
    }

    private fun handleResponse(response: HomeDataResponse) {
        try {
            setUiData(response)
        } catch (e: Exception) {
            genericHandler.showErrorMessage(e.message.toString())
        }
    }

    private fun setUiData(response: HomeDataResponse) {
        response.data.apply {
            categories?.let {
                adapterServices.submitList(it)
                setServicesData(response)
            }
            featuredGig?.let {
                adapterGigs.submitList(it)
                binding.tvPlaceholderFeaturedGigs.isVisible = it.isEmpty()
            }
            ads.let { it ->
                val adsList: List<SliderItem> = it.map { ad ->
                    SliderItem(
//                        url = "${Constants.BASE_URL}${ad.banner}",
                        url = ad.banner,
                        description = ""
                    )
                }
                sliderAdapter?.renewItems(adsList as MutableList<SliderItem>)
                binding.tvPlaceholderAds.isVisible = it.isEmpty()
            }

        }
    }
    override fun onResume() {
        super.onResume()
        lockHandler.lockDrawer(false)
    }

    override fun onPause() {
        lockHandler.lockDrawer(true)
        super.onPause()
    }

    override fun <T> onItemClick(item: T) {
        if (item is Category) {
            val id = item.id
            val action = HomeFragmentDirections.actionHomeFragmentToServiceCategoriesFragment(id)
            findNavController().navigate(action)
        } else if (item is FeaturedGig) {
            val gson = Gson()
            val serviceData = gson.toJson(item)
            val action =
                HomeFragmentDirections.actionHomeFragmentToGigDetailsFragment(
                    serviceData
                )
            findNavController().navigate(action)
        } else if (item is Notification) {
            val gson = Gson()
            val serviceData = gson.toJson(item)
            val action =
                HomeFragmentDirections.actionHomeFragmentToNotificationFragment(serviceData)
            findNavController().navigate(action)
        }
    }

    override fun onRefresh() {
        if (swipeRefreshLayout.isRefreshing) {
            swipeRefreshLayout.isRefreshing = false
        }
        executeApi()
    }
//    private val editorListener = TextView.OnEditorActionListener { v, actionId, event ->
//        when (actionId) {
//            EditorInfo.IME_ACTION_SEARCH -> {
//                hideKeyboard()
//                val query = binding.autoCompleteTextView.text.toString().trim()
//                val id=serviceId
//
//                HomeFragmentDirections.actionHomeFragmentToServiceGigsFragment(
//                    id =  id ,
//                    from = 0,
//                    query =query
//                ).also {
//                    findNavController().navigate(it)
//                }
//            }
//        }
//        false
//    }
    private val focusChangeListener = View.OnFocusChangeListener {
            v, hasFocus ->
        if (!hasFocus) {
            hideKeyboard()
        }
    }
    override fun onItemSelected(parent: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        when (parent!!.id) {
            binding.spinnerDoctor.id -> {
                try {
                    val spinnerModel = parent.selectedItem as SpinnerModel
                    serviceId = spinnerModel.id
                    if (!servicesArrayList.isEmpty()) {
                        binding.autoCompleteTextView.filters
                    }
                } catch (ex: Exception) {
                    genericHandler.showErrorMessage(ex.message.toString())
                }
            }
        }
    }
    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}