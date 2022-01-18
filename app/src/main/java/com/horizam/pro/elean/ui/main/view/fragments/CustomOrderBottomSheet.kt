package com.horizam.pro.elean.ui.main.view.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.horizam.pro.elean.Constants
import com.horizam.pro.elean.R
import com.horizam.pro.elean.data.model.requests.CustomOrderRequest
import com.horizam.pro.elean.databinding.DialogCustomOrderBinding
import com.horizam.pro.elean.ui.main.callbacks.GenericHandler
import com.horizam.pro.elean.ui.main.view.activities.CheckoutActivity

class CustomOrderBottomSheet : BottomSheetDialogFragment(), AdapterView.OnItemSelectedListener {

    private lateinit var binding: DialogCustomOrderBinding
    private lateinit var genericHandler: GenericHandler
    private lateinit var deliveryDaysArrayList: ArrayList<String>
    private lateinit var daysAdapter: ArrayAdapter<String>
    private var deliveryTime = ""
    private var serviceId = ""


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
    }

    private fun initData() {
        arguments?.let {
            serviceId = it.getString(Constants.SERVICE_ID) ?: ""
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
        //val dialogPaymentMethod = PaymentMethodDialogFragment()
        //dialogPaymentMethod.show(requireActivity().supportFragmentManager,"paymentDialog")
        validateInput()
    }

    private fun validateInput() {
        binding.apply {
            if (etInfo.text.toString().trim().length < 10) {
                etInfo.error = getString(R.string.str_description_is_short)
                return
            } /*else if (!BaseUtils.isNumber(etPrice.text.toString().trim()) ||
                etPrice.text.toString().trim().isEmpty()
            ) {
                etPrice.error = getString(R.string.str_enter_valid_price)
                return
            } else if (etPrice.text.toString().toDouble() < Constants.MINIMUM_ORDER_PRICE) {
                etPrice.error =
                    "Minimum ${Constants.MINIMUM_ORDER_PRICE}${Constants.CURRENCY} must be entered"
                return
            }*/ else if (serviceId.isEmpty()) {
                this@CustomOrderBottomSheet.dismiss()
                genericHandler.showErrorMessage(getString(R.string.str_invalid_service))
                return
            } /*else if (deliveryTime.isEmpty()) {
                this@CustomOrderBottomSheet.dismiss()
                genericHandler.showMessage(getString(R.string.str_enter_valid_delivery_time))
                return
            }*/ else {
                this@CustomOrderBottomSheet.dismiss()
                proceedToPayment()
            }
        }
    }

    private fun proceedToPayment() {
        val customOrderRequest = CustomOrderRequest(
            service_id = serviceId,
            description = binding.etInfo.text.toString().trim(),
            token = ""
        )
        val gson = Gson()
        Intent(requireActivity(), CheckoutActivity::class.java).also {
            it.putExtra(Constants.CUSTOM_ORDER_KEY, gson.toJson(customOrderRequest))
            it.putExtra("service_name" ,  arguments?.getString("service_name"))
            it.putExtra("seller_name" ,  arguments?.getString("seller_name"))
            it.putExtra("price" ,  arguments?.getString("price"))
            startActivity(it)
        }
    }

    companion object {
        const val TAG = "CustomOrderBottomSheet"
    }
}