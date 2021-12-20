package com.horizam.pro.elean.ui.main.view.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.horizam.pro.elean.App
import com.horizam.pro.elean.Constants
import com.horizam.pro.elean.R
import com.horizam.pro.elean.data.model.SpinnerModel
import com.horizam.pro.elean.data.model.requests.CustomOrderRequest
import com.horizam.pro.elean.data.model.requests.SendOfferRequest
import com.horizam.pro.elean.databinding.CheckoutBottomSheetBinding
import com.horizam.pro.elean.databinding.DialogCustomOrderBinding
import com.horizam.pro.elean.ui.main.callbacks.CheckoutHandler
import com.horizam.pro.elean.ui.main.callbacks.GenericHandler
import com.horizam.pro.elean.ui.main.callbacks.SendOfferHandler
import com.horizam.pro.elean.ui.main.view.activities.CheckoutActivity
import com.horizam.pro.elean.utils.BaseUtils
import com.horizam.pro.elean.utils.BaseUtils.Companion.hideKeyboard
import com.stripe.android.ApiResultCallback
import com.stripe.android.PaymentConfiguration
import com.stripe.android.Stripe
import com.stripe.android.model.Token

class CheckoutBottomSheet(private val checkoutHandler: CheckoutHandler) :
    BottomSheetDialogFragment() {

    private lateinit var binding: CheckoutBottomSheetBinding
    private lateinit var stripe: Stripe
    private lateinit var genericHandler: GenericHandler

    override fun onAttach(context: Context) {
        super.onAttach(context)
        genericHandler = context as GenericHandler
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = CheckoutBottomSheetBinding.inflate(layoutInflater, container, false)
        initViews()
        initData()
        setClickListeners()
        return binding.root
    }

    private fun initViews() {
        stripe = Stripe(
            requireContext(),
            PaymentConfiguration.getInstance(requireContext()).publishableKey
        )
    }

    private fun initData() {
        arguments?.let {

        }
    }

    private fun setClickListeners() {
        binding.apply {
            btnPay.setOnClickListener {
                hideKeyboard()
                if (cardInputWidget.cardParams != null) {
                    genericHandler.showProgressBar(true)
                    btnPay.isEnabled = false
                    stripe.createCardToken(
                        cardInputWidget.cardParams!!,
                        callback = stripeTokenCallback
                    )
                } else {
                    showMessage(getString(R.string.str_enter_all_card_details))
                }
            }
        }
    }

    private val stripeTokenCallback = object : ApiResultCallback<Token> {
        override fun onError(e: Exception) {
            genericHandler.showProgressBar(false)
            binding.btnPay.isEnabled = true
            showMessage(e.message.toString())
        }

        override fun onSuccess(result: Token) {
            genericHandler.showProgressBar(false)
            binding.btnPay.isEnabled = true
            sendToken(result.id)
        }

    }

    fun showMessage(message: String) {
        Snackbar.make(
            requireActivity().findViewById(android.R.id.content),
            message, Snackbar.LENGTH_LONG
        ).show()
    }

    private fun sendToken(token: String) {
        checkoutHandler.sendToken(token)
        this@CheckoutBottomSheet.dismiss()
    }

    companion object {
        const val TAG = "checkoutBottomSheet"
    }
}