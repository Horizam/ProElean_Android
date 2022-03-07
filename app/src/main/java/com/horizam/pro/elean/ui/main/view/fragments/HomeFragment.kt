package com.horizam.pro.elean.ui.main.view.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.horizam.pro.elean.Constants
import com.horizam.pro.elean.R
import com.horizam.pro.elean.data.api.ApiHelper
import com.horizam.pro.elean.data.api.RetrofitBuilder
import com.horizam.pro.elean.data.model.SliderItem
import com.horizam.pro.elean.data.model.response.Category
import com.horizam.pro.elean.data.model.response.FeaturedGig
import com.horizam.pro.elean.data.model.response.HomeDataResponse
import com.horizam.pro.elean.databinding.FragmentHomeBinding
import com.horizam.pro.elean.ui.base.ViewModelFactory
import com.horizam.pro.elean.ui.main.adapter.HomeGigsAdapter
import com.horizam.pro.elean.ui.main.adapter.ServicesAdapter
import com.horizam.pro.elean.ui.main.adapter.SliderAdapter
import com.horizam.pro.elean.ui.main.callbacks.DrawerHandler
import com.horizam.pro.elean.ui.main.callbacks.GenericHandler
import com.horizam.pro.elean.ui.main.callbacks.LockHandler
import com.horizam.pro.elean.ui.main.callbacks.OnItemClickListener
import com.horizam.pro.elean.ui.main.viewmodel.HomeViewModel
import com.horizam.pro.elean.utils.BaseUtils.Companion.hideKeyboard
import com.horizam.pro.elean.utils.Status
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView
import java.lang.Exception

import android.view.inputmethod.EditorInfo
import android.widget.TextView.OnEditorActionListener
import com.google.firebase.auth.FirebaseAuth
import com.horizam.pro.elean.ui.main.view.activities.ManageSalesActivity
import com.horizam.pro.elean.utils.PrefManager


class HomeFragment : Fragment(), OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapterServices: ServicesAdapter
    private lateinit var adapterGigs: HomeGigsAdapter
    private lateinit var viewModel: HomeViewModel
    private lateinit var genericHandler: GenericHandler
    private lateinit var drawerHandler: DrawerHandler
    private lateinit var prefManager: PrefManager
    private lateinit var lockHandler: LockHandler
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private var sliderView: SliderView? = null
    private var sliderAdapter: SliderAdapter? = null
    private lateinit var mAuth: FirebaseAuth


    override fun onAttach(context: Context) {
        super.onAttach(context)
        genericHandler = context as GenericHandler
        lockHandler = context as LockHandler
        drawerHandler = context as DrawerHandler
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        getIntentData()
        initViews()
        setupViewModel()
        setupObservers()
        setRecyclerViews()
        setAdsSlider()
        setClickListeners()
        return binding.root
    }

    private fun executeApi() {
        genericHandler.showProgressBar(true)
        viewModel.homeDataCall()
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
                    val id = bundle!!.getString(Constants.CONTENT_ID)
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
        sliderView = binding.imageSlider
        sliderAdapter = SliderAdapter(this)
        swipeRefreshLayout = binding.swipeRefresh
        swipeRefreshLayout.setOnRefreshListener(this)
        binding.toolbar.ivSecond.setImageResource(R.drawable.ic_notifications)
        binding.toolbar.ivSecond.visibility = View.VISIBLE
        binding.toolbar.rlNoOfNotification.visibility = View.VISIBLE
        binding.toolbar.ivSale.visibility = View.GONE
        binding.toolbar.rlNoOfNotification.visibility = View.GONE
        binding.toolbar.tvToolbar.visibility = View.GONE
        binding.toolbar.ivLogoToolbar.visibility = View.VISIBLE
    }

    private fun setRecyclerViews() {
        serviceRecyclerview()
        gigsRecyclerview()
    }

    private fun gigsRecyclerview() {
        binding.rvFeaturedGigs.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = adapterGigs
        }
    }

    private fun serviceRecyclerview() {
        binding.rvServiceCategories.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
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
//        binding.toolbar.ivToolbar.setOnClickListener {
//            hideKeyboard()
//            drawerHandler.openDrawer()
//        }
        binding.toolbar.ivToolbar.visibility = View.GONE
        binding.btnRetry.setOnClickListener {
            executeApi()
        }
        binding.autoCompleteTextView.onFocusChangeListener = focusChangeListener
        binding.autoCompleteTextView.setOnEditorActionListener(editorListener)
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
    }

    private fun setupObservers() {
        viewModel.homeData.observe(viewLifecycleOwner, {
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
        })
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
            }
            featuredGig?.let {
                adapterGigs.submitList(it)
                binding.tvPlaceholderFeaturedGigs.isVisible = it.isEmpty()
            }
            ads.let { it ->
                val adsList: List<SliderItem> = it.map { ad ->
                    SliderItem(
//                        url = "${Constants.BASE_URL}${ad.banner}",
                        url = "${ad.banner}",
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
            val id = item.id
            val action = HomeFragmentDirections.actionHomeFragmentToFeaturedGigsDetailsFragment(id)
            findNavController().navigate(action)
        }
    }

    override fun onRefresh() {
        if (swipeRefreshLayout.isRefreshing) {
            swipeRefreshLayout.isRefreshing = false
        }
        executeApi()
    }

    private val editorListener = OnEditorActionListener { v, actionId, event ->
        when (actionId) {
            EditorInfo.IME_ACTION_SEARCH -> {
                hideKeyboard()
                val query = binding.autoCompleteTextView.text.toString().trim()
                binding.autoCompleteTextView.text.clear()
                HomeFragmentDirections.actionHomeFragmentToServiceGigsFragment(
                    id = "",
                    from = 1,
                    query = query
                ).also {
                    findNavController().navigate(it)
                }
            }
        }
        false
    }

    private val focusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
        if (!hasFocus) {
            hideKeyboard()
        }
    }
}