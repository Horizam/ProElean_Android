package com.horizam.pro.elean.ui.main.view.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.horizam.pro.elean.App
import com.horizam.pro.elean.Constants
import com.horizam.pro.elean.R
import com.horizam.pro.elean.data.api.ApiHelper
import com.horizam.pro.elean.data.api.RetrofitBuilder
import com.horizam.pro.elean.data.model.requests.SendOfferRequest
import com.horizam.pro.elean.data.model.response.*
import com.horizam.pro.elean.databinding.FragmentSellerServicesBinding
import com.horizam.pro.elean.ui.base.ViewModelFactory
import com.horizam.pro.elean.ui.main.adapter.SellerServicesAdapter
import com.horizam.pro.elean.ui.main.callbacks.GenericHandler
import com.horizam.pro.elean.ui.main.callbacks.OnItemClickListener
import com.horizam.pro.elean.ui.main.callbacks.SendOfferHandler
import com.horizam.pro.elean.ui.main.viewmodel.SellerServicesViewModel
import com.horizam.pro.elean.utils.Resource
import com.horizam.pro.elean.utils.Status
import java.lang.Exception


class SellerServicesFragment : Fragment(), OnItemClickListener, SendOfferHandler {

    private lateinit var binding: FragmentSellerServicesBinding
    private lateinit var adapter: SellerServicesAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: SellerServicesViewModel
    private lateinit var genericHandler: GenericHandler
    private lateinit var deliveryDaysList: ArrayList<String>
    private val args: SellerServicesFragmentArgs by navArgs()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        genericHandler = context as GenericHandler
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSellerServicesBinding.inflate(layoutInflater, container, false)
        setToolbarData()
        initViews()
        setupViewModel()
        setupObservers()
        setRecyclerView()
        setOnClickListeners()
        exeApi()
        return binding.root
    }

    private fun exeApi(status: String = "") {
        if (viewModel.userServices.value == null) {
            genericHandler.showProgressBar(true)
            viewModel.userServicesCall(status)
        }
    }

    private fun initViews() {
        deliveryDaysList = ArrayList()
        adapter = SellerServicesAdapter(this)
        recyclerView = binding.rvUserServices
    }

    private fun setRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                recyclerView.context,
                (recyclerView.layoutManager as LinearLayoutManager).orientation
            )
        )
        recyclerView.adapter = adapter
    }

    private fun setOnClickListeners() {
        binding.apply {
            toolbar.ivToolbar.setOnClickListener {
                findNavController().popBackStack()
            }
            btnRetry.setOnClickListener {
                viewModel.userServicesCall("")
            }
        }
    }

    private fun setToolbarData() {
        binding.toolbar.ivToolbar.setImageResource(R.drawable.ic_back)
        binding.toolbar.tvToolbar.text =
            App.getAppContext()!!.getString(R.string.str_seller_services)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(SellerServicesViewModel::class.java)
    }

    private fun setupObservers() {
        viewModel.userServices.observe(viewLifecycleOwner, {
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

        viewModel.categoriesRevisionDeliveryTimeResponse.observe(viewLifecycleOwner, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        genericHandler.showProgressBar(false)
                        resource.data?.let { response ->
                            handleDeliveryTimeResponse(response)
                        }
                    }
                    Status.ERROR -> {
                        genericHandler.showProgressBar(false)
                        genericHandler.showErrorMessage(it.message.toString())
                    }
                    Status.LOADING -> {
                        genericHandler.showProgressBar(true)
                    }
                }
            }
        })

        viewModel.sendOffer.observe(viewLifecycleOwner, sendOfferObserver)
    }

    private val sendOfferObserver = Observer<Resource<BuyerRequest>> {
        it?.let { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    genericHandler.showProgressBar(false)
                    resource.data?.let { response ->
                        findNavController().popBackStack()
                    }
                }
                Status.ERROR -> {
                    genericHandler.showProgressBar(false)
                    genericHandler.showErrorMessage(it.message.toString())
                }
                Status.LOADING -> {
                    genericHandler.showProgressBar(true)
                }
            }
        }
    }

    private fun changeViewVisibility(textView: Boolean, button: Boolean, layout: Boolean) {
        binding.textViewError.isVisible = textView
        binding.btnRetry.isVisible = button
        binding.rvUserServices.isVisible = layout
    }

    private fun handleResponse(serviceResponse: ServicesResponse) {
        try {
            setUIData(serviceResponse.serviceList)
//            if (response.days != null) {
//                deliveryDaysList = response.days as ArrayList<String>
//            }
        } catch (e: Exception) {
            genericHandler.showErrorMessage(e.message.toString())
        }
    }

    private fun handleDeliveryTimeResponse(categoriesCountriesResponse: CategoriesCountriesResponse) {
        try {
            if (categoriesCountriesResponse.categoriesCountriesData.deliveryDays.isNotEmpty()) {
                deliveryDaysList =
                    categoriesCountriesResponse.categoriesCountriesData.deliveryDays as ArrayList<String>
            }
        } catch (e: Exception) {
            genericHandler.showErrorMessage(e.message.toString())
        }
    }

    private fun setUIData(serviceList: List<ServiceDetail>) {
        adapter.submitList(serviceList)
        binding.tvPlaceholder.isVisible = serviceList.isEmpty()
    }

    override fun <T> onItemClick(item: T) {
        if (item is ServiceDetail) {
            val jobId: String = args.id
            val sendOfferBottomSheet = SendOfferBottomSheet(this)
            val bundle = Bundle()
            bundle.putString(Constants.USER_SERVICE_ID, item.id)
            bundle.putString(Constants.JOB_ID, jobId)
            bundle.putStringArrayList(Constants.DAYS_LIST, deliveryDaysList)
            sendOfferBottomSheet.arguments = bundle
            sendOfferBottomSheet.show(
                requireActivity().supportFragmentManager,
                SendOfferBottomSheet.TAG
            )
        }
    }

    override fun <T> sendOffer(item: T) {
        if (item is SendOfferRequest) {
            viewModel.sendOfferCall(item)
        }
    }

}