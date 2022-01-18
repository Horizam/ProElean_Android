package com.horizam.pro.elean.ui.main.view.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.horizam.pro.elean.Constants
import com.horizam.pro.elean.R
import com.horizam.pro.elean.data.model.requests.SendOfferRequest
import com.horizam.pro.elean.databinding.DialogCustomOrderBinding
import com.horizam.pro.elean.ui.main.callbacks.GenericHandler
import com.horizam.pro.elean.ui.main.callbacks.SendOfferHandler
import com.horizam.pro.elean.utils.BaseUtils

class SendOfferBottomSheet(private val sendOfferHandler: SendOfferHandler) : BottomSheetDialogFragment(), AdapterView.OnItemSelectedListener {

    private lateinit var binding: DialogCustomOrderBinding
    private lateinit var genericHandler: GenericHandler
    private lateinit var deliveryDaysArrayList: ArrayList<String>
    private lateinit var daysAdapter: ArrayAdapter<String>
    private var deliveryTime = ""
    private var serviceId = ""
    private var jobId = ""

    override fun onAttach(context: Context) {
        super.onAttach(context)
        genericHandler = context as GenericHandler
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogCustomOrderBinding.inflate(layoutInflater, container, false)
        initViews()
        initData()
        setClickListeners()
        return binding.root
    }

    private fun initViews() {
        binding.spinnerDeliveryTime.onItemSelectedListener = this
        binding.textFieldPrice.isVisible = true
        binding.linearLayoutDeliveryTime.isVisible = true
    }

    private fun initData() {
        arguments?.let {
            serviceId = it.getString(Constants.USER_SERVICE_ID , "")
            jobId = it.getString(Constants.JOB_ID , "")
            deliveryDaysArrayList = it.getStringArrayList(Constants.DAYS_LIST)!!
            setDeliveryDays()
        }
    }

    private fun setDeliveryDays() {
        daysAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item, deliveryDaysArrayList
        ).also {
            it.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            binding.spinnerDeliveryTime.adapter = it
        }
    }

    private fun setClickListeners() {
        binding.apply {
            btnSubmitRequest.setOnClickListener {
                confirmPaymentMethod()
            }
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (parent!!.id) {
            binding.spinnerDeliveryTime.id -> {
                deliveryTime = parent.selectedItem.toString()
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    private fun confirmPaymentMethod() {
        validateInput()
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
                etPrice.error =
                    "Minimum ${Constants.MINIMUM_ORDER_PRICE}${Constants.CURRENCY} must be entered"
                return
            } else if (serviceId == "") {
                this@SendOfferBottomSheet.dismiss()
                genericHandler.showErrorMessage(getString(R.string.str_invalid_service))
                return
            }else if (jobId == "") {
                this@SendOfferBottomSheet.dismiss()
                genericHandler.showErrorMessage(getString(R.string.str_invalid_job))
                return
            } else if (deliveryTime.isEmpty()) {
                this@SendOfferBottomSheet.dismiss()
                genericHandler.showErrorMessage(getString(R.string.str_enter_valid_delivery_time))
                return
            } else {
                this@SendOfferBottomSheet.dismiss()
                sendOffer()
            }
        }
    }

    private fun sendOffer() {
        val sendOfferRequest = SendOfferRequest(
            service_id = serviceId,
            job_id = jobId,
            description = binding.etInfo.text.toString().trim(),
            price = binding.etPrice.text.toString().trim().toDouble(),
            delivery_time = deliveryTime
        )
        sendOfferHandler.sendOffer(sendOfferRequest)
    }

    companion object {
        const val TAG = "SendOfferBottomSheet"
    }
}