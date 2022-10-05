package com.horizam.pro.elean.ui.main.view.activities

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.horizam.pro.elean.Constants
import com.horizam.pro.elean.R
import com.horizam.pro.elean.data.api.ApiHelper
import com.horizam.pro.elean.data.api.RetrofitBuilder
import com.horizam.pro.elean.data.model.requests.CardModel
import com.horizam.pro.elean.data.model.requests.CustomOrderRequest
import com.horizam.pro.elean.data.model.response.GeneralResponse
import com.horizam.pro.elean.data.model.response.TokenModel
import com.horizam.pro.elean.databinding.ActivityCheckoutBinding
import com.horizam.pro.elean.databinding.DialogFileUploadingBinding
import com.horizam.pro.elean.databinding.DialogOrderSuccessBinding
import com.horizam.pro.elean.ui.base.ViewModelFactory
import com.horizam.pro.elean.ui.main.viewmodel.CheckOutViewModel
import com.horizam.pro.elean.utils.BaseUtils.Companion.hideKeyboard
import com.horizam.pro.elean.utils.Status
import com.stripe.android.ApiResultCallback
import com.stripe.android.PaymentConfiguration
import com.stripe.android.Stripe
import com.stripe.android.model.Token
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class CheckoutActivity : AppCompatActivity(), CoroutineScope {

    private lateinit var binding: ActivityCheckoutBinding
    private lateinit var viewModel: CheckOutViewModel
    private lateinit var stripe: Stripe
    private var customOrderRequest: CustomOrderRequest? = null
    private lateinit var dialogOrderStatus: Dialog
    private lateinit var bindingDialogOrderSuccessBinding: DialogOrderSuccessBinding
    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main + handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarColor()
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
        setupViewModel()
        setupObservers()
        getData()
        setOnClickListeners()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(CheckOutViewModel::class.java)
    }

    private fun setupObservers() {
        viewModel.customOrder.observe(this) {
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
        }

        viewModel.getToken.observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        showProgressBar(false)
                        resource.data?.let { response ->
                            sendToken(response.token)
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
        }
    }

    private fun handleResponse(response: GeneralResponse) {
        showMessage(response.message)
        dialogOrderStatus.show()
    }

    private fun setOnClickListeners() {
        binding.apply {
            btnPay.setOnClickListener {
                var cardNumber = binding.cardInputWidget.cardNumberEditText.text!!.toString().trim()
                    .replace(" ", "")
                var cvc = binding.cardInputWidget.cvcEditText.text.toString()
                var date = binding.cardInputWidget.expiryDateEditText.text.toString()
                var dateList = date.split("/")
                var cardModel: CardModel = CardModel()
                cardModel.number = cardNumber
                cardModel.cvc = cvc
                cardModel.exp_month = dateList[0].toString().toInt()
                cardModel.exp_year = dateList[1].toString().toInt() + 2000
                viewModel.getToken(cardModel)
            }

            bindingDialogOrderSuccessBinding.btnContinue.setOnClickListener {
                dialogOrderStatus.dismiss()
                startActivity(Intent(this@CheckoutActivity, HomeActivity::class.java).apply {
                    putExtra("order", 1)
                })
                finish()
            }
        }
    }

    private fun sendToken(token: String) {
        customOrderRequest?.let {
            it.token = token
            viewModel.customOrderCall(it)
        }
    }

    fun showProgressBar(show: Boolean) {
        binding.progressLayout.isVisible = show
    }

    fun showMessage(message: String) {
        Snackbar.make(
            findViewById(android.R.id.content),
            message, Snackbar.LENGTH_LONG
        ).show()
    }

    private fun getData() {
        val gson = Gson()
        customOrderRequest = gson.fromJson(
            intent.getStringExtra(Constants.CUSTOM_ORDER_KEY),
            CustomOrderRequest::class.java
        )

        intent?.let {
            binding.tvServiceName.text = it.getStringExtra("service_name").toString()
          if( it.getStringExtra("seller_name").toString().isEmpty())
          {
              binding.tvSellerName.text =it.getStringExtra("seller_username").toString()
          }
            else{
              binding.tvSellerName.text =it.getStringExtra("seller_name").toString()
          }
            binding.tvPrice.text = it.getStringExtra("price").toString()
        }
    }

    private fun initViews() {
        initOrderStatusDialog()
        job = Job()
        stripe = Stripe(
            applicationContext,
            PaymentConfiguration.getInstance(applicationContext).publishableKey
        )
    }

    private fun initOrderStatusDialog() {
        dialogOrderStatus = Dialog(this)
        dialogOrderStatus.setCancelable(false)
        bindingDialogOrderSuccessBinding = DialogOrderSuccessBinding.inflate(layoutInflater)
        dialogOrderStatus.setContentView(bindingDialogOrderSuccessBinding.root)
        setDialogWidth()
    }

    private fun setDialogWidth() {
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialogOrderStatus.window!!.attributes)
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        dialogOrderStatus.window!!.attributes = layoutParams
    }

    private fun setStatusBarColor() {
        val window: Window = this.window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorOne)
    }

    private val handler = CoroutineExceptionHandler { _, exception ->
        Log.d("TAG", "$exception handled !")
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }

}