package com.horizam.pro.elean.ui.main.view.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import com.horizam.pro.elean.BuyerOrders
import com.horizam.pro.elean.Constants
import com.horizam.pro.elean.R
import com.horizam.pro.elean.data.api.ApiHelper
import com.horizam.pro.elean.data.api.RetrofitBuilder
import com.horizam.pro.elean.data.model.requests.BuyerActionsRequest
import com.horizam.pro.elean.data.model.requests.SellerActionsRequest
import com.horizam.pro.elean.data.model.response.GeneralResponse
import com.horizam.pro.elean.data.model.response.Order
import com.horizam.pro.elean.databinding.FragmentOrderDetailsBinding
import com.horizam.pro.elean.ui.base.ViewModelFactory
import com.horizam.pro.elean.ui.main.callbacks.GenericHandler
import com.horizam.pro.elean.ui.main.viewmodel.SellerOrdersViewModel
import com.horizam.pro.elean.utils.PrefManager
import com.horizam.pro.elean.utils.Status
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.horizam.pro.elean.SellerOrders
import com.horizam.pro.elean.data.model.requests.RatingOrderRequest
import com.horizam.pro.elean.data.model.requests.SellerActionRequestMultipart
import com.horizam.pro.elean.ui.main.callbacks.DeliverOrderFileHandler
import com.horizam.pro.elean.ui.main.callbacks.DescriptionHandler
import com.horizam.pro.elean.ui.main.callbacks.RatingHandler
import com.horizam.pro.elean.ui.main.view.activities.HomeActivity
import com.horizam.pro.elean.utils.BaseUtils
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import java.util.*
import kotlin.collections.HashMap


class OrderDetailsFragment(private val order: Order, private val pair: Pair<Int, Int>) :
    Fragment(), SwipeRefreshLayout.OnRefreshListener, DeliverOrderFileHandler, RatingHandler,
    DescriptionHandler {

    private lateinit var binding: FragmentOrderDetailsBinding
    private lateinit var genericHandler: GenericHandler
    private lateinit var viewModel: SellerOrdersViewModel
    private lateinit var prefManager: PrefManager
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout


    override fun onAttach(context: Context) {
        super.onAttach(context)
        genericHandler = context as GenericHandler
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderDetailsBinding.inflate(layoutInflater, container, false)
        initViews()
        setData()
        setupViewModel()
        setupObservers()
        setOnClickListeners()
        return binding.root
    }

    private fun initViews() {
        prefManager = PrefManager(requireContext())
        swipeRefreshLayout = binding.swipeRefresh
        swipeRefreshLayout.setOnRefreshListener(this)
    }

    private fun setData() {
        binding.apply {
            try {
                tvOrder.text = order.orderNo
                tvDescription.text =
                    if (!order.description.isNullOrEmpty()) order.description else getString(
                        R.string.str_no_description
                    )
                if (prefManager.userId == order.seller_id) {
                    tvBuyer.text = order.username
                    tvBuyer.isVisible = true
                    tvBuyerTitle.isVisible = true
                    btnDispute.text = "REQUEST BUYER TO CANCEL ORDER"
                } else {
                    tvSeller.text = order.username
                    tvSeller.isVisible = true
                    tvSellerTitle.isVisible = true
                }
                tvPrice.text = order.amount.toString().plus(order.currency)
                tvDuration.text = order.delivery_time
                tvRevisions.text = order.revision.toString()
                tvDelivery.text =
                    if (order.delivery_note.isNotEmpty()) order.delivery_note else getString(
                        R.string.str_no_delivery_note
                    )
                when (pair.first) {
                    Constants.BUYER_USER -> {
                        setBuyerData(pair)
                    }
                    Constants.SELLER_USER -> {
                        setSellerData(pair)
                    }
                    else -> {
                        genericHandler.showMessage(getString(R.string.str_something_went_wrong))
                        requireActivity().finish()
                    }
                }
            } catch (e: Exception) {
                genericHandler.showMessage(e.message.toString())
                changeViewsVisibility(
                    deliveryNote = false,
                    btnResubmit = false,
                    buttonDispute = false,
                    buttonProceedDispute = false,
                    buttonRevision = false,
                    buttonCompleted = false,
                    buttonRateOrder = false
                )
            }
        }
    }

    private fun setSellerData(pair: Pair<Int, Int>) {
        binding.apply {
            when (pair.second) {
                SellerOrders.Active -> {
                    changeViewsVisibility(
                        deliveryNote = false,
                        btnResubmit = false,
                        buttonDispute = true,
                        buttonProceedDispute = false,
                        buttonRevision = false,
                        buttonCompleted = true,
                        buttonRateOrder = false
                    )
                    startTimer()
                    binding.countdownTimer.isVisible = true
                    btnCompleted.text = getString(R.string.str_deliver_work)
                }
                SellerOrders.Late -> {
                    changeViewsVisibility(
                        deliveryNote = false,
                        btnResubmit = false,
                        buttonDispute = true,
                        buttonProceedDispute = false,
                        buttonRevision = false,
                        buttonCompleted = true,
                        buttonRateOrder = false
                    )
                    startTimer()
                    binding.countdownTimer.isVisible = true
                    btnCompleted.text = getString(R.string.str_deliver_work)
                }
                SellerOrders.Delivered -> {
                    changeViewsVisibility(
                        deliveryNote = false,
                        btnResubmit = false,
                        buttonDispute = false,
                        buttonProceedDispute = false,
                        buttonRevision = false,
                        buttonCompleted = false,
                        buttonRateOrder = false
                    )
                }
                SellerOrders.Revision -> {
                    changeViewsVisibility(
                        deliveryNote = false,
                        btnResubmit = true,
                        buttonDispute = false,
                        buttonProceedDispute = false,
                        buttonRevision = false,
                        buttonCompleted = false,
                        buttonRateOrder = false
                    )
                }
                SellerOrders.Completed -> {
                    changeViewsVisibility(
                        deliveryNote = false,
                        btnResubmit = false,
                        buttonDispute = false,
                        buttonProceedDispute = false,
                        buttonRevision = false,
                        buttonCompleted = false,
                        buttonRateOrder = false
                    )
                }
                SellerOrders.Cancel -> {
                    changeViewsVisibility(
                        deliveryNote = false,
                        btnResubmit = false,
                        buttonDispute = false,
                        buttonProceedDispute = false,
                        buttonRevision = false,
                        buttonCompleted = false,
                        buttonRateOrder = false
                    )
                }
                SellerOrders.Disputed -> {
                    if (prefManager.userId == order.disputed_by) {
                        changeViewsVisibility(
                            deliveryNote = false,
                            btnResubmit = false,
                            buttonDispute = false,
                            buttonProceedDispute = false,
                            buttonRevision = false,
                            buttonCompleted = false,
                            buttonRateOrder = false
                        )
                        btnAgree.isVisible = false
                        btnCancelRequest.isVisible = true
                    } else {
                        changeViewsVisibility(
                            deliveryNote = false,
                            btnResubmit = false,
                            buttonDispute = false,
                            buttonProceedDispute = true,
                            buttonRevision = false,
                            buttonCompleted = false,
                            buttonRateOrder = false
                        )
                        btnAgree.isVisible = true
                        btnCancelRequest.isVisible = false
                    }
                }
            }
        }
    }

    private fun startTimer() {
        val startTime = Date().time
        val endTime = BaseUtils.getMillisecondsFromUtc(order.end_date)
        val remainingTime = endTime.minus(startTime)
        binding.countdownTimer.start(remainingTime)
    }

    private fun setBuyerData(pair: Pair<Int, Int>) {
        binding.apply {
            when (pair.second) {
                BuyerOrders.Active -> {
                    changeViewsVisibility(
                        deliveryNote = false,
                        btnResubmit = false,
                        buttonDispute = true,
                        buttonProceedDispute = false,
                        buttonRevision = false,
                        buttonCompleted = false,
                        buttonRateOrder = false
                    )
                    startTimer()
                    binding.countdownTimer.isVisible = true
                }
                BuyerOrders.Late -> {
                    changeViewsVisibility(
                        deliveryNote = false,
                        btnResubmit = false,
                        buttonDispute = true,
                        buttonProceedDispute = false,
                        buttonRevision = false,
                        buttonCompleted = false,
                        buttonRateOrder = false
                    )
                    startTimer()
                    binding.countdownTimer.isVisible = true
                }
                BuyerOrders.Delivered -> {
                    changeViewsVisibility(
                        deliveryNote = true,
                        btnResubmit = false,
                        buttonDispute = true,
                        buttonProceedDispute = false,
                        buttonRevision = true,
                        buttonCompleted = true,
                        buttonRateOrder = false
                    )
                }
                BuyerOrders.Revision -> {
                    changeViewsVisibility(
                        deliveryNote = false,
                        btnResubmit = false,
                        buttonDispute = false,
                        buttonProceedDispute = false,
                        buttonRevision = false,
                        buttonCompleted = true,
                        buttonRateOrder = false
                    )
                }
                BuyerOrders.Completed -> {
                    changeViewsVisibility(
                        deliveryNote = false,
                        btnResubmit = false,
                        buttonDispute = false,
                        buttonProceedDispute = false,
                        buttonRevision = false,
                        buttonCompleted = false,
                        buttonRateOrder = checkRating()
                    )
                }
                BuyerOrders.Cancel -> {
                    changeViewsVisibility(
                        deliveryNote = false,
                        btnResubmit = false,
                        buttonDispute = false,
                        buttonProceedDispute = false,
                        buttonRevision = false,
                        buttonCompleted = false,
                        buttonRateOrder = false
                    )
                }
                BuyerOrders.Disputed -> {
                    if (prefManager.userId == order.disputed_by) {
                        changeViewsVisibility(
                            deliveryNote = false,
                            btnResubmit = false,
                            buttonDispute = false,
                            buttonProceedDispute = false,
                            buttonRevision = false,
                            buttonCompleted = false,
                            buttonRateOrder = false
                        )
                        btnAgree.isVisible = false
                        btnCancelRequest.isVisible = true
                    } else {
                        changeViewsVisibility(
                            deliveryNote = false,
                            btnResubmit = false,
                            buttonDispute = false,
                            buttonProceedDispute = true,
                            buttonRevision = false,
                            buttonCompleted = false,
                            buttonRateOrder = false
                        )
                        btnAgree.isVisible = true
                        btnCancelRequest.isVisible = false
                    }
                }
            }
        }
    }

    private fun FragmentOrderDetailsBinding.changeViewsVisibility(
        deliveryNote: Boolean,
        btnResubmit: Boolean, buttonDispute: Boolean, buttonProceedDispute: Boolean,
        buttonRevision: Boolean, buttonCompleted: Boolean, buttonRateOrder: Boolean
    ) {
        cardViewDeliveryNote.isVisible = deliveryNote
        btnResubmitOrder.isVisible = btnResubmit
        btnDispute.isVisible = buttonDispute
        btnProceedWithSupport.isVisible = buttonProceedDispute
        btnRevision.isVisible = buttonRevision
        btnCompleted.isVisible = buttonCompleted
        btnRateOrder.isVisible = buttonRateOrder
    }

    private fun checkRating(): Boolean {
        return order.is_rated == 0
    }

    private fun setOnClickListeners() {
        /*binding.toolbar.ivToolbar.setOnClickListener {
            navController.popBackStack()
        }*/
        binding.apply {
            btnCompleted.setOnClickListener {
                when (pair.first) {
                    Constants.BUYER_USER -> {
                        // Completed order
                        val hashMap: HashMap<String, Any> = HashMap()
                        hashMap["order_no"] = order.orderNo
                        hashMap["type"] = 4
                        viewModel.buyerActionsCall(hashMap)
                    }
                    Constants.SELLER_USER -> {
                        // deliver your work
                        val deliverFileBottomSheetFragment =
                            DeliverFileBottomSheetFragment(this@OrderDetailsFragment)
                        deliverFileBottomSheetFragment.show(
                            requireActivity().supportFragmentManager,
                            ""
                        )
                    }
                }
            }
            btnDispute.setOnClickListener {
                when (pair.first) {
                    Constants.BUYER_USER -> {
                        // cancel & create dispute
                        val descriptionBottomSheet = DescriptionBottomSheet(
                            this@OrderDetailsFragment,
                            Constants.BUYER_USER,
                            5
                        )
                        descriptionBottomSheet.show(
                            requireActivity().supportFragmentManager,
                            ""
                        )
                    }
                    Constants.SELLER_USER -> {
                        // request buyer to cancel order
                        val descriptionBottomSheet = DescriptionBottomSheet(
                            this@OrderDetailsFragment,
                            Constants.SELLER_USER,
                            5
                        )
                        descriptionBottomSheet.show(
                            requireActivity().supportFragmentManager,
                            ""
                        )
                    }
                }
            }
            btnRevision.setOnClickListener {
                when (pair.first) {
                    Constants.BUYER_USER -> {
                        // revision
                        val descriptionBottomSheet = DescriptionBottomSheet(
                            this@OrderDetailsFragment,
                            Constants.BUYER_USER,
                            3
                        )
                        descriptionBottomSheet.show(
                            requireActivity().supportFragmentManager,
                            ""
                        )
                    }
                    Constants.SELLER_USER -> {

                    }
                }
            }
            btnRateOrder.setOnClickListener {
                when (pair.first) {
                    Constants.BUYER_USER -> {
                        val ratingBottomSheet =
                            RatingBottomSheet(this@OrderDetailsFragment, genericHandler)
                        ratingBottomSheet.show(
                            requireActivity().supportFragmentManager,
                            ""
                        )
                    }
                }
            }
            btnAgree.setOnClickListener {
                when (pair.first) {
                    Constants.BUYER_USER -> {
                        val hashMap: HashMap<String, Any> = HashMap()
                        hashMap["order_no"] = order.orderNo
                        hashMap["type"] = 9
                        viewModel.buyerActionsCall(hashMap)
                    }
                    Constants.SELLER_USER -> {
                        val hashMap: HashMap<String, Any> = HashMap()
                        hashMap["order_no"] = order.orderNo
                        hashMap["type"] = 9
                        viewModel.sellerActionsCall(hashMap)
                    }
                }
            }
            btnCancelRequest.setOnClickListener {
                when (pair.first) {
                    Constants.BUYER_USER -> {
                        val hashMap: HashMap<String, Any> = HashMap()
                        hashMap["order_no"] = order.orderNo
                        hashMap["type"] = 8
                        viewModel.buyerActionsCall(hashMap)
                    }
                    Constants.SELLER_USER -> {
                        val hashMap: HashMap<String, Any> = HashMap()
                        hashMap["order_no"] = order.orderNo
                        hashMap["type"] = 8
                        viewModel.sellerActionsCall(hashMap)
                    }
                }
            }
            btnResubmitOrder.setOnClickListener {
                when (pair.first) {
                    Constants.BUYER_USER -> {

                    }
                    Constants.SELLER_USER -> {
                        // resubmit order
                        val deliverFileBottomSheetFragment =
                            DeliverFileBottomSheetFragment(this@OrderDetailsFragment)
                        deliverFileBottomSheetFragment.show(
                            requireActivity().supportFragmentManager,
                            ""
                        )
                    }
                }
            }
            btnProceedWithSupport.setOnClickListener {
                openWebUrl("https://app.prolean.com/support")
            }
            tvDownload.setOnClickListener {
                when (pair.first) {
                    Constants.BUYER_USER -> {
                        // Download source code
                        genericHandler.showMessage("Coming soon")
                    }
                    Constants.SELLER_USER -> {

                    }
                }
            }
//            tvSeller.setOnClickListener {
//                Intent(requireActivity(), HomeActivity::class.java).also {
//                    it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                    it.putExtra("startChat", 0)
//                    it.putExtra("id", order.seller_id)
//                    startActivity(it)
//                }
//            }
//            tvBuyer.setOnClickListener {
//                Intent(requireActivity(), HomeActivity::class.java).also {
//                    it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                    it.putExtra("startChat", 0)
//                    it.putExtra("id", order.buyer_id)
//                    startActivity(it)
//                }
//            }
        }
    }

    private fun openWebUrl(webUrl: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(webUrl))
        startActivity(browserIntent)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(SellerOrdersViewModel::class.java)
    }

    private fun setupObservers() {
        viewModel.sellerActions.observe(viewLifecycleOwner, {
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
                        genericHandler.showMessage(it.message.toString())
                    }
                    Status.LOADING -> {
                        genericHandler.showProgressBar(true)
                    }
                }
            }
        })

        viewModel.sellerActionWithFile.observe(viewLifecycleOwner, {
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
                        genericHandler.showMessage(it.message.toString())
                    }
                    Status.LOADING -> {
                        genericHandler.showProgressBar(true)
                    }
                }
            }
        })

        viewModel.buyerActions.observe(viewLifecycleOwner, {
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
                        genericHandler.showMessage(it.message.toString())
                    }
                    Status.LOADING -> {
                        genericHandler.showProgressBar(true)
                    }
                }
            }
        })

        viewModel.ratingOrder.observe(viewLifecycleOwner, {
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
                        genericHandler.showMessage(it.message.toString())
                    }
                    Status.LOADING -> {
                        genericHandler.showProgressBar(true)
                    }
                }
            }
        })
    }

    private fun handleResponse(response: GeneralResponse) {
        try {
            genericHandler.showMessage(response.message)
            requireActivity().apply {
                setResult(Activity.RESULT_OK)
                finish()
            }
        } catch (e: java.lang.Exception) {
            genericHandler.showMessage(e.message.toString())
        }
    }

    override fun onResume() {
        super.onResume()
        startTimer()
    }

    override fun onRefresh() {
        if (swipeRefreshLayout.isRefreshing) {
            swipeRefreshLayout.isRefreshing = false
        }
        // refresh order details at this time no api available
    }

    override fun sendFilePath(filePath: String, description: String) {
        lifecycleScope.launch {
            val orderNumber = BaseUtils.createRequestBodyFromString(order.orderNo)
            val typeUser = BaseUtils.createRequestBodyFromString("2")
            val deliveryNote = BaseUtils.createRequestBodyFromString(description)
            val image: MultipartBody.Part = BaseUtils.compressAndCreateImageData(
                filePath,
                "delivered_file",
                requireContext()
            )

            val request = SellerActionRequestMultipart(orderNumber, typeUser, deliveryNote, image)
            viewModel.sellerActionsCallWithFile(request)
        }
    }

    override fun getRatingData(rating: Float, description: String) {
        val request = RatingOrderRequest(
            type = 10,
            order_no = order.orderNo,
            rating = rating,
            description = description
        )
        viewModel.ratingOrderCall(request)
    }

    override fun getDescription(description: String, userType: Int, type: Int) {
        val hashMap: HashMap<String, Any> = HashMap()
        if (userType == Constants.BUYER_USER) {
            if (type == 3) {
                hashMap["revision_description"] = description
                hashMap["order_no"] = order.orderNo
                hashMap["type"] = type
                viewModel.buyerActionsCall(hashMap)

            } else {
                hashMap["dispute_description"] = description
                hashMap["order_no"] = order.orderNo
                hashMap["type"] = type
                viewModel.buyerActionsCall(hashMap)
            }
        } else if (userType == Constants.SELLER_USER) {
            hashMap["order_no"] = order.orderNo
            hashMap["type"] = type
            hashMap["dispute_description"] = description
            viewModel.sellerActionsCall(hashMap)
        }
    }
}