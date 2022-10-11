package com.horizam.pro.elean.ui.main.view.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.horizam.pro.elean.App
import com.horizam.pro.elean.Constants
import com.horizam.pro.elean.R
import com.horizam.pro.elean.data.api.ApiHelper
import com.horizam.pro.elean.data.api.RetrofitBuilder
import com.horizam.pro.elean.data.model.MessageOffer
import com.horizam.pro.elean.data.model.SpinnerModel
import com.horizam.pro.elean.data.model.response.*
import com.horizam.pro.elean.databinding.CreateOfferBottomSheetBinding
import com.horizam.pro.elean.ui.base.ViewModelFactory
import com.horizam.pro.elean.ui.main.adapter.SpinnerAdapter
import com.horizam.pro.elean.ui.main.callbacks.CreateOfferHandler
import com.horizam.pro.elean.ui.main.callbacks.GenericHandler
import com.horizam.pro.elean.ui.main.viewmodel.ManageServicesViewModel
import com.horizam.pro.elean.utils.BaseUtils
import com.horizam.pro.elean.utils.BaseUtils.Companion.hideKeyboard
import com.horizam.pro.elean.utils.PrefManager
import com.horizam.pro.elean.utils.Status
import java.lang.Exception

class CreateOfferBottomSheet(private val createOfferHandler: CreateOfferHandler) :
    BottomSheetDialogFragment(), AdapterView.OnItemSelectedListener {

    private lateinit var binding: CreateOfferBottomSheetBinding
    private lateinit var genericHandler: GenericHandler
    private lateinit var viewModel: ManageServicesViewModel
    private lateinit var deliveryDaysArrayList: ArrayList<String>
    private lateinit var revisionsArrayList: ArrayList<String>
    private lateinit var servicesArrayList: List<SpinnerModel>
    private lateinit var generalServicesArrayList: List<ServiceDetail>
    private lateinit var daysAdapter: ArrayAdapter<String>
    private lateinit var revisonsAdapter: ArrayAdapter<String>
    private lateinit var servicesAdapter: ArrayAdapter<SpinnerModel>
    private lateinit var prefManager: PrefManager
    private var deliveryTime = ""
    private var revisions = ""
    private var serviceId = ""
    private var serviceTitle = ""

    override fun onAttach(context: Context) {
        super.onAttach(context)
        genericHandler = context as GenericHandler
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = CreateOfferBottomSheetBinding.inflate(layoutInflater, container, false)
        initViews()
        initData()
        setupViewModel()
        setupObservers()
        setClickListeners()
        exeApi()
        return binding.root
    }

    private fun initViews() {
        prefManager = PrefManager(requireContext())
        servicesArrayList = ArrayList()
        revisionsArrayList = arrayListOf("1", "2", "3")
        generalServicesArrayList = ArrayList()
        deliveryDaysArrayList = ArrayList()
        binding.spinnerDeliveryTime.onItemSelectedListener = this
        binding.spinnerServices.onItemSelectedListener = this
        binding.spinnerRevisions.onItemSelectedListener = this
    }

    private fun initData() {
        arguments?.let {

        }
    }

    private fun exeApi(status: String = "") {
        if (viewModel.userServices.value == null) {
            genericHandler.showProgressBar(true)
            viewModel.userServicesCall(status)
        }
    }

    private fun setClickListeners() {
        binding.apply {
            btnSubmitRequest.setOnClickListener {
                hideKeyboard()
                validateInput()
            }
        }
    }

    private fun validateInput() {
        binding.apply {
            if (etInfo.text.toString().trim().length < 10) {
                etInfo.error = getString(R.string.str_description_is_short)
                return
            } else if (!BaseUtils.isNumber(etPrice.text.toString().trim()) ||
                etPrice.text.toString().trim().isEmpty()
            ) {
                etPrice.error = getString(R.string.str_enter_valid_price)
                return
            } else if (etPrice.text.toString().toDouble() < Constants.MINIMUM_ORDER_PRICE) {
                var manager: PrefManager = PrefManager(App.getAppContext()!!)
                if (manager.setLanguage == "0") {
                    etPrice.error = "Minimum ${Constants.MINIMUM_ORDER_PRICE}${Constants.CURRENCY} must be entered"
                } else {
                    etPrice.error = "Minimi ${Constants.MINIMUM_ORDER_PRICE}${Constants.CURRENCY}  on syötettävä"

                }
                return
            } else if (serviceId == "") {
                this@CreateOfferBottomSheet.dismiss()
                genericHandler.showErrorMessage(getString(R.string.str_invalid_service))
                return
            } else if (deliveryTime.isEmpty()) {
                this@CreateOfferBottomSheet.dismiss()
                genericHandler.showErrorMessage(getString(R.string.str_enter_valid_delivery_time))
                return
            } else if (revisions.isEmpty()) {
                this@CreateOfferBottomSheet.dismiss()
                genericHandler.showErrorMessage(getString(R.string.str_enter_valid_revisions))
                return
            } else {
                this@CreateOfferBottomSheet.dismiss()
                sendOffer()
            }
        }
    }

    private fun sendOffer() {
        val messageOffer = MessageOffer(
            serviceId = serviceId,
            serviceTitle = serviceTitle,
            description = binding.etInfo.text.toString().trim(),
            totalOffer = binding.etPrice.text.toString().trim().toDouble(),
            deliveryDays = deliveryTime,
            revisions = revisions,
            status = Constants.OFFER_WITHDRAW,
            offerSenderId = prefManager.userId,
        )
        createOfferHandler.sendOffer(messageOffer)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(ManageServicesViewModel::class.java)
    }

    private fun setupObservers() {
        viewModel.userServices.observe(viewLifecycleOwner, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        genericHandler.showProgressBar(false)
                        resource.data?.let { response ->
                            handleResponse(response)
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

        viewModel.categoriesRevisionDeliveryTimeResponse.observe(viewLifecycleOwner, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        genericHandler.showProgressBar(false)
                        resource.data?.let { response ->
                            handleResponse(response)
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
    }

    private fun <T> handleResponse(response: T) {
        try {
            when (response) {
                is CategoriesCountriesResponse -> {
                    setRevisionAndDaysData(response)
                }
                is ServicesResponse -> {
                    setServicesData(response)
                }
            }
        } catch (e: Exception) {
            genericHandler.showErrorMessage(e.message.toString())
        }
    }

    private fun setServicesData(response: ServicesResponse) {
        if (response.serviceList.isNotEmpty()) {
            generalServicesArrayList = response.serviceList
            servicesArrayList = response.serviceList.map { spinnerServices ->
                SpinnerModel(id = spinnerServices.id, value = spinnerServices.s_description)
            }
            servicesAdapter = SpinnerAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item, servicesArrayList
            ).also {
                it.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
                binding.spinnerServices.adapter = it
            }
        }
    }

    private fun setRevisionAndDaysData(response: CategoriesCountriesResponse) {
        if (response.categoriesCountriesData.deliveryDays.isNotEmpty()) {
            deliveryDaysArrayList =
                response.categoriesCountriesData.deliveryDays as ArrayList<String>
            daysAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item, deliveryDaysArrayList
            ).also {
                it.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                binding.spinnerDeliveryTime.adapter = it
            }
        }

        if (response.categoriesCountriesData.revisions.isNotEmpty()) {
            revisionsArrayList = response.categoriesCountriesData.revisions as ArrayList<String>
            revisonsAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item, revisionsArrayList
            ).also {
                it.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                binding.spinnerRevisions.adapter = it
            }
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (parent!!.id) {
            binding.spinnerDeliveryTime.id -> {
                deliveryTime = parent.selectedItem.toString()
            }
            binding.spinnerRevisions.id -> {
                revisions = parent.selectedItem.toString()
            }
            binding.spinnerServices.id -> {
                try {
                    val spinnerModel = parent.selectedItem as SpinnerModel
                    serviceId = spinnerModel.id
                    if (!servicesArrayList.isNullOrEmpty()) {
                        setServiceImage(serviceId)
                    }
                } catch (ex: Exception) {
                    genericHandler.showErrorMessage(ex.message.toString())
                }
            }
        }
    }

    private fun setServiceImage(serviceId: String) {
        generalServicesArrayList.first {
            it.id == serviceId
        }.also { service ->
            serviceTitle = service.s_description
            if (service.service_media!=null) {
                Glide.with(requireContext())
                    .load(Constants.BASE_URL.plus(service.service_media[0].media))
                    .error(R.drawable.bg_splash)
                    .into(binding.ivService)
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    companion object {
        const val TAG = "createOfferBottomSheet"
    }
}