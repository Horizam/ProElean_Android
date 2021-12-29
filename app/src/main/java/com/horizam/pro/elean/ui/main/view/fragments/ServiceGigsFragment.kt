package com.horizam.pro.elean.ui.main.view.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.horizam.pro.elean.Constants
import com.horizam.pro.elean.R
import com.horizam.pro.elean.data.api.ApiHelper
import com.horizam.pro.elean.data.api.RetrofitBuilder
import com.horizam.pro.elean.data.model.MessageGig
import com.horizam.pro.elean.data.model.SpinnerPriceModel
import com.horizam.pro.elean.data.model.requests.FavouriteRequest
import com.horizam.pro.elean.data.model.requests.SearchGigsRequest
import com.horizam.pro.elean.data.model.response.GeneralResponse
import com.horizam.pro.elean.data.model.response.ServiceDetail
import com.horizam.pro.elean.databinding.FragmentServiceGigsBinding
import com.horizam.pro.elean.ui.base.ViewModelFactory
import com.horizam.pro.elean.ui.main.adapter.GigsAdapter
import com.horizam.pro.elean.ui.main.adapter.MyLoadStateAdapter
import com.horizam.pro.elean.ui.main.adapter.PriceAdapter
import com.horizam.pro.elean.ui.main.callbacks.ContactSellerHandler
import com.horizam.pro.elean.ui.main.callbacks.FavouriteHandler
import com.horizam.pro.elean.ui.main.callbacks.GenericHandler
import com.horizam.pro.elean.ui.main.callbacks.OnItemClickListener
import com.horizam.pro.elean.ui.main.viewmodel.ServiceGigsViewModel
import com.horizam.pro.elean.utils.BaseUtils.Companion.hideKeyboard
import com.horizam.pro.elean.utils.PrefManager
import com.horizam.pro.elean.utils.Resource
import com.horizam.pro.elean.utils.Status


class ServiceGigsFragment : Fragment(), OnItemClickListener, FavouriteHandler,
    ContactSellerHandler, AdapterView.OnItemSelectedListener {

    private lateinit var binding: FragmentServiceGigsBinding
    private lateinit var adapter: GigsAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: ServiceGigsViewModel
    private lateinit var genericHandler: GenericHandler
    private lateinit var prefManager: PrefManager
    private lateinit var priceArrayList: List<String>
    private lateinit var priceValueArrayList: List<String>
    private lateinit var priceAdapter: ArrayAdapter<SpinnerPriceModel>
    private var filter = ""
    private var filterValue = ""
    private val args: ServiceGigsFragmentArgs by navArgs()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        genericHandler = context as GenericHandler
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViewModel()
        setupFavoritesObservers()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentServiceGigsBinding.inflate(layoutInflater, container, false)
        initViews()
//        setupViewModel()
        setupObservers()
        setRecyclerview()
        setOnClickListeners()
        executeApi()
        return binding.root
    }

    private fun executeApi() {
        if (args.from == Constants.NORMAL_FLOW) {
            if (viewModel.sellers.value == null) {
                viewModel.getServicesBySubCategories(args.id)
            }
        } else {
            binding.autoCompleteTextView.setText(args.query)
            exeSearch()
        }
    }

    private fun initViews() {
        prefManager = PrefManager(requireContext())
        adapter = GigsAdapter(this, this, this)
        recyclerView = binding.rvServiceGigs
        setPriceSpinner()
    }

    private fun setPriceSpinner() {
        priceArrayList = arrayListOf("5+", "5-20", "21-50", "51-100", "101-500", "500+")
        priceValueArrayList = arrayListOf("5", "5,20", "21,50", "51,100", "101,500", "500")
        val spinnerList = priceArrayList.map { price ->
            SpinnerPriceModel(
                filterValue = priceValueArrayList[priceArrayList.indexOf(price)],
                value = price
            )
        }
        priceAdapter = PriceAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item, spinnerList
        ).also {
            it.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            binding.priceSpinner.adapter = it
        }
        binding.priceSpinner.onItemSelectedListener = this
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (parent!!.id) {
            binding.priceSpinner.id -> {
                val spinnerPriceModel = parent.selectedItem as SpinnerPriceModel
                when {
                    spinnerPriceModel.value.contains("+") -> {
                        filter = "price>"
                        filterValue = spinnerPriceModel.filterValue
                    }
                    else -> {
                        filter = "price"
                        filterValue = spinnerPriceModel.filterValue
                    }
                }
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    private fun setRecyclerview() {
        recyclerView.let {
            it.setHasFixedSize(true)
            it.layoutManager = LinearLayoutManager(requireContext())
            it.adapter = adapter.withLoadStateHeaderAndFooter(
                header = MyLoadStateAdapter { adapter.retry() },
                footer = MyLoadStateAdapter { adapter.retry() }
            )
        }
        setAdapterLoadState(adapter)
    }

    private fun setAdapterLoadState(adapter: GigsAdapter) {
        adapter.addLoadStateListener { loadState ->
            binding.apply {
                genericHandler.showProgressBar(loadState.source.refresh is LoadState.Loading)
                rvServiceGigs.isVisible = loadState.source.refresh is LoadState.NotLoading
                btnRetry.isVisible = loadState.source.refresh is LoadState.Error
                textViewError.isVisible = loadState.source.refresh is LoadState.Error
                // no results
                if (loadState.source.refresh is LoadState.NotLoading &&
                    loadState.append.endOfPaginationReached &&
                    adapter.itemCount < 1
                ) {
                    rvServiceGigs.isVisible = false
                    tvPlaceholder.isVisible = true
                } else {
                    tvPlaceholder.isVisible = false
                }
            }
        }
    }

    private fun setOnClickListeners() {
        binding.apply {
            ivToolbar.setOnClickListener {
                findNavController().popBackStack()
            }
            btnRetry.setOnClickListener {
                adapter.retry()
            }
            btnSearch.setOnClickListener {
                hideKeyboard()
                exeSearch()
            }
        }
        binding.autoCompleteTextView.onFocusChangeListener = focusChangeListener
    }

    private fun exeSearch() {
        val query = binding.autoCompleteTextView.text.toString().trim()
        val distance = binding.slider.value.toString()
        val request = SearchGigsRequest(
            query = query,
            distance = distance,
            filter = filter,
            filterValue = filterValue,
        )
        viewModel.searchGigs(request)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(ServiceGigsViewModel::class.java)
    }

    private fun setupObservers() {
        viewModel.sellers.observe(viewLifecycleOwner) {
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
        }
        viewModel.searchSellers.observe(viewLifecycleOwner) {
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
        }
    }

    private fun setupFavoritesObservers() {
        viewModel.makeFavourite.observe(this, makeFavouriteObserver)
    }

    private val makeFavouriteObserver = Observer<Resource<GeneralResponse>> {
        it?.let { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    genericHandler.showProgressBar(false)
                    viewModel.getServicesBySubCategories(args.id)
                }
                Status.ERROR -> {
                    genericHandler.showProgressBar(false)
                    genericHandler.showMessage(it.message.toString())
                }
                Status.LOADING -> {
                    genericHandler.showProgressBar(true)
                }
            }
        }
    }

    override fun <T> onItemClick(item: T) {
        if (item is ServiceDetail) {
            val gson = Gson()
            val serviceData = gson.toJson(item)
            val action =
                ServiceGigsFragmentDirections.actionServiceGigsFragmentToGigDetailsFragment(
                    serviceData
                )
            findNavController().navigate(action)
        }
    }

    override fun <T> addRemoveWishList(item: T) {
        if (item is ServiceDetail) {
            viewModel.addToWishlistCall(FavouriteRequest(item.id))
        }
    }

    override fun <T> contactSeller(item: T) {
        if (item is ServiceDetail){
            try {
                if (prefManager.userId != item.user_id && item.user_id != ""){
                    val messageGig = MessageGig(
                        gigId = item.id,
                        gigImage = item.service_media[0].media,
                        gigTitle = item.s_description,
                        gigUsername = item.service_user.username
                    )
                    ServiceGigsFragmentDirections.actionServiceGigsFragmentToMessagesFragment(
                        id = item.user_id,
                        refersGig = true,
                        messageGig = messageGig
                    ).also {
                        findNavController().navigate(it)
                    }
                }
            }catch (e: Exception){
                genericHandler.showMessage(e.message.toString())
            }
        }
    }

    private val focusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
        if (!hasFocus) {
            hideKeyboard()
        }
    }

}