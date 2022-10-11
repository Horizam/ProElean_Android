package com.horizam.pro.elean.ui.main.view.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.request.RequestOptions
import com.horizam.pro.elean.BuyerOrders
import com.horizam.pro.elean.Constants
import com.horizam.pro.elean.R
import com.horizam.pro.elean.SellerOrders
import com.horizam.pro.elean.data.api.ApiHelper
import com.horizam.pro.elean.data.api.RetrofitBuilder
import com.horizam.pro.elean.data.model.BuyerActionRequestMultipart
import com.horizam.pro.elean.data.model.MessageGig
import com.horizam.pro.elean.data.model.requests.BuyerRevisionAction
import com.horizam.pro.elean.data.model.requests.ExtendDeliveryTimeModel
import com.horizam.pro.elean.data.model.requests.RatingOrderRequest
import com.horizam.pro.elean.data.model.requests.SellerActionRequestMultipart
import com.horizam.pro.elean.data.model.response.GeneralResponse
import com.horizam.pro.elean.data.model.response.Order
import com.horizam.pro.elean.data.model.response.ServiceDetail
import com.horizam.pro.elean.databinding.FragmentOrderDetailsBinding
import com.horizam.pro.elean.ui.base.ViewModelFactory
import com.horizam.pro.elean.ui.main.callbacks.*
import com.horizam.pro.elean.ui.main.view.activities.AuthenticationActivity
import com.horizam.pro.elean.ui.main.view.activities.CheckoutActivity
import com.horizam.pro.elean.ui.main.view.activities.HomeActivity
import com.horizam.pro.elean.ui.main.view.fragments.manageOrders.ManageOrdersFragment
import com.horizam.pro.elean.ui.main.viewmodel.SellerOrdersViewModel
import com.horizam.pro.elean.utils.BaseUtils
import com.horizam.pro.elean.utils.PrefManager
import com.horizam.pro.elean.utils.Status
import kotlinx.android.synthetic.main.fragment_order_details.*
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import java.util.*


class OrderDetailsFragment(private val order: Order, private val pair: Pair<Int, Int>) :
    Fragment(), SwipeRefreshLayout.OnRefreshListener, DeliverOrderFileHandler, RatingHandler,
    DescriptionHandler, ExtendDeliveryTimeHandler {

    private lateinit var binding: FragmentOrderDetailsBinding
    private lateinit var genericHandler: GenericHandler
    private lateinit var viewModel: SellerOrdersViewModel
    private lateinit var prefManager: PrefManager
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private var userId: String = ""
    private var gig: ServiceDetail? = null
    private val bundle = Bundle()


    override fun onAttach(context: Context) {
        super.onAttach(context)
        genericHandler = context as GenericHandler
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentOrderDetailsBinding.inflate(layoutInflater,
            container, false)
        initViews()
        setData()
        setupViewModel()
        setupObservers()
        setOnClickListeners()
        return binding.root
    }

    private fun executeApi(code: String, description: String) {
        genericHandler.showProgressBar(true)
        val request = BuyerActionRequestMultipart(
            description = description
        )

        viewModel.buyerActionsCall(order.id, request)
    }

    private fun executeCancelDisputeApi(code: String) {
        genericHandler.showProgressBar(true)

        viewModel.cancelRequest(order.id)
    }

    private fun executeAcceptApi(code: String) {
        genericHandler.showProgressBar(true)
        viewModel.acceptExtensionRequest(order.id)
    }
    private fun executeRejectApi(code: String) {
        genericHandler.showProgressBar(true)
        viewModel.rejectExtensionRequest(order.id)
    }

    private fun executeCompleteApi(code: String, description: String) {
        genericHandler.showProgressBar(true)
        val request = BuyerActionRequestMultipart(
            description = description
        )

        viewModel.buyerCompleteActions(order.id, request)
    }
    private fun executeRevisionApi(code: String, description: String) {
        genericHandler.showProgressBar(true)
        val request =BuyerRevisionAction(
            revision_description = description
        )

        viewModel.buyerRevisionActions(order.id, request)
    }

    private fun executeExtendedApi(code: String, description: String,extended_days:String) {
        genericHandler.showProgressBar(true)
        val request = ExtendDeliveryTimeModel(
            description = description,
            extended_delivery_days = extended_days
        )
        viewModel.requestExtendTime(order.id, request)
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
                    if (!order.description.isNullOrEmpty())
                        order.description else getString(
                        R.string.str_no_description
                    )
                if (prefManager.userId == order.seller_id) {
                    tvBuyer.text = order.username
                    tvBuyer.isVisible = true
                    tvBuyerTitle.isVisible = true
                    btnDispute.text = "REQUEST BUYER TO CANCEL ORDER"
//                    btnAgree.isVisible=true
                } else {
                    tvSeller.text = order.username
                    tvSeller.isVisible = true
                    tvSellerTitle.isVisible = true
                }
                tvPrice.text = order.amount.toString().plus(order.currency)
                tvDuration.text = order.delivery_time
                tvRevisions.text = order.revision.toString()
                if(order.revision_left.toString() == (0).toString()) {
                    tvRevisionsLeft.text =getString(R.string.str_revision_finished)
                    btnRevision.isVisible=false
                }
                else
                {
                    tvRevisionsLeft.text=order.revision_left.toString()
                    btnRevision.isVisible=true
                }
                tvDelivery.text = if (order.delivery_note.isNotEmpty())
                    order.delivery_note else getString(R.string.str_no_delivery_note
                )
                when (pair.first) {
                    Constants.BUYER_USER -> {
                        setBuyerData(pair)
                    }
                    Constants.SELLER_USER -> {
                        setSellerData(pair)
                    }
                    else -> {
                        genericHandler.showErrorMessage(getString(R.string.str_something_went_wrong))
                        requireActivity().finish()
                    }
                }
            } catch (e: Exception) {
                genericHandler.showSuccessMessage(e.message.toString())
                changeViewsVisibility(
                    deliveryNote = false,
                    btnResubmit = false,
                    buttonDispute = false,
                    buttonProceedDispute = false,
                    buttonRevision = false,
                    buttonCompleted = false,
                    buttonRateOrder = false,
                    extendTime = false,
                    btnlate = false
                )
            }
        }
    }
    private fun revisionAvailable(): Boolean {
        return order.revision > 0
    }
    @SuppressLint("ResourceAsColor")
    private fun setSellerData(pair: Pair<Int, Int>) {
        binding.apply {
            when (pair.second) {
                SellerOrders.Active -> {
                    changeViewsVisibility(
                        deliveryNote = false,
                        btnResubmit = false,
                        buttonDispute = false,
                        buttonProceedDispute = false,
                        buttonRevision = false,
                        buttonCompleted = true,
                        buttonRateOrder = false,
                        extendTime = true,
                        btnlate = false)
                    val startTime = Date().time
                    val endTime = BaseUtils.getMillisecondsFromUtc(order.end_date)
                    val remainingTime = endTime.minus(startTime)
                    binding.countdownTimer.start(remainingTime)
                    if (endTime == remainingTime) {
                        binding.countdownTimer.setBackgroundColor(R.color.color_red)
                        btnCompleted.text = "Late Order Deliver"
                        btnCompleted.isVisible = true
                    }
                        else if(order.revision!=order.revision_left)
                        {
                            binding.countdownTimer.isVisible = true
                            btnCompleted.text = getString(R.string.str_redeliver_work)
                            btnExtendTime.text = getString(R.string.str_extend_time)
                        }
                     else {
                        binding.countdownTimer.isVisible = true
                        btnCompleted.text = getString(R.string.str_deliver_work)
                        btnExtendTime.text = getString(R.string.str_extend_time)
                    }
                }
                SellerOrders.Late -> {
                    changeViewsVisibility(
                        deliveryNote = false,
                        btnResubmit = false,
                        buttonDispute = false,
                        buttonProceedDispute = false,
                        buttonRevision = false,
                        buttonCompleted = true,
                        buttonRateOrder = false,
                        extendTime = false,
                        btnlate = true
                    )
                    startTimer()
                    binding.countdownTimer.isVisible = true
                    btnLate.text="Late Order Delivered"
                    btnLate.isVisible=true
                }
                SellerOrders.Delivered -> {
                    changeViewsVisibility(
                        deliveryNote = false,
                        btnResubmit = false,
                        buttonDispute = false,
                        buttonProceedDispute = false,
                        buttonRevision = false,
                        buttonCompleted = false,
                        buttonRateOrder = false,
                        extendTime = false,
                        btnlate = false
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
                        buttonRateOrder = false,
                        extendTime = false,
                        btnlate = false
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
                        buttonRateOrder = false,
                        extendTime = false,
                        btnlate = false
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
                        buttonRateOrder = false,
                        extendTime = false,
                        btnlate = false
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
                            buttonRateOrder = false,
                            extendTime = false,
                            btnlate = false
                        )
                        btnAgree.isVisible = false
                        btnCancelRequest.isVisible = true
                    } else {
                        changeViewsVisibility(
                            deliveryNote = false,
                            btnResubmit = false,
                            buttonDispute = false,
                            buttonProceedDispute = false,
                            buttonRevision = false,
                            buttonCompleted = false,
                            buttonRateOrder = false ,
                            extendTime = false,
                            btnlate = false
                        )
                        btnAgree.isVisible = true
                        btnCancelRequest.isVisible = true
                    }
                }
            }
        }
    }

    @SuppressLint("ResourceAsColor")
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
                        buttonRateOrder = false,
                        extendTime = false,
                        btnlate = false
                    )
                    startTimer()
                    if (order.pending_req == 1) {
                        btnExtendTime.isVisible = true
                        btnExtendTime.text = getString(R.string.str_accept_extension)
                        btnLate.isVisible = true
                        btnLate.text = getString(R.string.str_reject_extension)
                    }
                }
                BuyerOrders.Late -> {
                    changeViewsVisibility(
                        deliveryNote = false,
                        btnResubmit = true,
                        buttonDispute = false,
                        buttonProceedDispute = false,
                        buttonRevision = false,
                        buttonCompleted = false,
                        buttonRateOrder = false,
                        extendTime = false,
                        btnlate = true

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
                        buttonRevision = revisionAvailable(),
                        buttonCompleted = true,
                        buttonRateOrder = false,
                        extendTime = false,
                        btnlate = false
                    )
                    if(order.revision_left.toString() == (0).toString()) {
                        binding.btnRevision.isVisible=false
                    }
                }
                BuyerOrders.Revision -> {
                    changeViewsVisibility(
                        deliveryNote = false,
                        btnResubmit = false,
                        buttonDispute = false,
                        buttonProceedDispute = false,
                        buttonRevision = false,
                        buttonCompleted = true,
                        buttonRateOrder = false,
                        extendTime = false,
                        btnlate = false
                    )
                    if(order.revision_left.toString() == (0).toString()) {
                        binding.btnRevision.isVisible=false
                    }
                }
                BuyerOrders.Completed -> {
                    changeViewsVisibility(
                        deliveryNote = false,
                        btnResubmit = false,
                        buttonDispute = false,
                        buttonProceedDispute = false,
                        buttonRevision = false,
                        buttonCompleted = false,
                        extendTime = false,
                        btnlate = false,
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
                        buttonRateOrder = false,
                        extendTime = false,
                        btnlate = false
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
                            buttonRateOrder = false,
                            extendTime = false,
                            btnlate = false
                        )
                        btnAgree.isVisible = false
//                        btnCancelRequest.isVisible = true
                    } else {
                        changeViewsVisibility(
                            deliveryNote = false,
                            btnResubmit = false,
                            buttonDispute = false,
                            buttonProceedDispute = true,
                            buttonRevision = false,
                            buttonCompleted = false,
                            buttonRateOrder = false,
                            extendTime = false,
                            btnlate = false
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
        btnResubmit: Boolean,
        buttonDispute: Boolean,
        buttonProceedDispute: Boolean,
        buttonRevision: Boolean,
        buttonCompleted: Boolean,
        buttonRateOrder: Boolean,
        extendTime:Boolean,
        btnlate:Boolean
    ) {
        cardViewDeliveryNote.isVisible = deliveryNote
        btnResubmitOrder.isVisible = btnResubmit
        btnDispute.isVisible = buttonDispute
        btnProceedWithSupport.isVisible = buttonProceedDispute
        btnRevision.isVisible = buttonRevision
        btnCompleted.isVisible = buttonCompleted
        btnRateOrder.isVisible = buttonRateOrder
        btnExtendTime.isVisible = extendTime
        btnLate.isVisible=btnlate
    }

    private fun checkRating(): Boolean {
        return order.is_rated == 0
    }

    private fun setOnClickListeners() {
        binding.apply {
            btnCompleted.setOnClickListener {
                when (pair.first) {
                    Constants.BUYER_USER -> {
                        //Complete order
                        val descriptionBottomSheet = DescriptionBottomSheet(
                            this@OrderDetailsFragment,
                            Constants.BUYER_USER,
                            4
                        )
                        descriptionBottomSheet.show(
                            requireActivity().supportFragmentManager,
                            ""
                        )
                    }
                    Constants.SELLER_USER -> {
                        // deliver your work
                        val deliverFileBottomSheetFragment =
                            DeliverFileBottomSheetFragment(
                                this@OrderDetailsFragment
                            )
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
                        order.type == Constants.TYPE_ORDER
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
                        viewModel.AcceptDisputeRequest(order.id)
                        // request buyer to cancel order
//                        val descriptionBottomSheet = DescriptionBottomSheet(
//                            this@OrderDetailsFragment,
//                            Constants.SELLER_USER,
//                            5
//                        )
//                        descriptionBottomSheet.show(
//                            requireActivity().supportFragmentManager,
//                            ""
//                        )
                    }
                }
            }
            btnExtendTime.setOnClickListener {
                when (pair.first) {
                    Constants.SELLER_USER -> {
                        val descriptionBottomSheet =
                            ExtendDeliveryTimeBottomSheetFragment(this@OrderDetailsFragment,
                                Constants.SELLER_USER,
                                5
                            )
                        descriptionBottomSheet.show(
                            requireActivity().supportFragmentManager,
                            ""
                        )
                    }
                    Constants.BUYER_USER -> {
                        btnExtendTime.setOnClickListener {
                            btnExtendTime.isVisible = true
                            executeAcceptApi(order.id)
                            btnExtendTime.isVisible = false
                            btnLate.isVisible = false
                        }
                    }
                }
            }
            btnLate.setOnClickListener {
                executeRejectApi(order.id)
                btnExtendTime.isVisible = false
                btnLate.isVisible = false
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
                        hashMap["type"] = 5
////                         viewModel.buyerActionsCall(hashMap)
//                        executeCancelDisputeApi(order.id)
                    }
                    Constants.SELLER_USER -> {
                        val hashMap: HashMap<String, Any> = HashMap()
                        hashMap["order_no"] = order.orderNo
                        hashMap["type"] = 5
                        viewModel.AcceptDisputeRequest(order.id)

                    }
                }
            }
            btnCancelRequest.setOnClickListener {
                when (pair.first) {
                    Constants.BUYER_USER -> {
                        val hashMap: HashMap<String, Any> = HashMap()
                        hashMap["order_no"] = order.orderNo
                        hashMap["type"] = 1

                    }
                    Constants.SELLER_USER -> {
                        val hashMap: HashMap<String, Any> = HashMap()
                        hashMap["order_no"] = order.orderNo
                        hashMap["type"] = 1
                        executeCancelDisputeApi(order.id)
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
//            btnProceedWithSupport.setOnClickListener {
//                executeRejectDisputeApi(order.id)
//            }
            tvDownload.setOnClickListener {
                when (pair.first) {
                    Constants.BUYER_USER -> {
                        // Download source code
                        genericHandler.showSuccessMessage(getString(R.string.str_coming_soon))
                    }
                    Constants.SELLER_USER -> {

                    }
                }
            }
            tvSeller.setOnClickListener {
                if (prefManager.setLanguage == "0") {
                    Toast.makeText(requireActivity(),
                        "Please go to Desktop to download your file",
                        Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireActivity(),
                        "Lataa tiedosto siirtymällä työpöydälle",
                        Toast.LENGTH_SHORT).show()
                }
//                Intent(requireActivity(), HomeActivity::class.java).also {
//                    it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                    it.putExtra("startChat", 0)
//                    it.putExtra("id", order.seller_id)
//                    startActivity(it)
            }
            tvBuyer.setOnClickListener {
                if (prefManager.setLanguage == "0") {
                    Toast.makeText(requireActivity(),
                        "Please go to Desktop to download your file",
                        Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireActivity(),
                        "Lataa tiedosto siirtymällä työpöydälle",
                        Toast.LENGTH_SHORT).show()
                }
//                Intent(requireActivity(), HomeActivity::class.java).also {
//                    it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                    it.putExtra("startChat", 0)
//                    it.putExtra("id", order.buyer_id)
//                    startActivity(it)
//                }
            }
            tvInbox.setOnClickListener {

        }
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
        viewModel.sellerActions.observe(viewLifecycleOwner) {
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
        }
        viewModel.extendTime.observe(viewLifecycleOwner) {
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
        }
        viewModel.accept.observe(viewLifecycleOwner) {
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
        }
        viewModel.reject.observe(viewLifecycleOwner) {
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
        }
        viewModel.sellerActionWithFile.observe(viewLifecycleOwner) {
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
        }

        viewModel.User.observe(viewLifecycleOwner) {
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
        }
        viewModel.buyerComplete.observe(viewLifecycleOwner) {
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
        }
        viewModel.buyerRevision.observe(viewLifecycleOwner) {
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
        }
        viewModel.buyerCancelDispute.observe(viewLifecycleOwner) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        genericHandler.showProgressBar(false)
                        resource.data?.let { response ->
                            handleResponse(response)
                            startActivity(Intent(requireActivity(), HomeActivity::class.java).apply {
                                putExtra("order", 1)
                            })
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
//        viewModel.rejectDispute.observe(viewLifecycleOwner) {
//            it?.let { resource ->
//                when (resource.status) {
//                    Status.SUCCESS -> {
//                        genericHandler.showProgressBar(false)
//                        resource.data?.let { response ->
//                            handleResponse(response)
//                        }
//                    }
//                    Status.ERROR -> {
//                        genericHandler.showProgressBar(false)
//                        genericHandler.showErrorMessage(it.message.toString())
//                    }
//                    Status.LOADING -> {
//                        genericHandler.showProgressBar(true)
//                    }
//                }
//            }
//        }
        viewModel.acceptDispute.observe(viewLifecycleOwner) {
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
        }

        viewModel.ratingOrder.observe(viewLifecycleOwner) {
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
        }
    }

    private fun handleResponse(response: GeneralResponse) {
        try {
            genericHandler.showSuccessMessage(response.message)
                    startActivity(Intent(requireActivity(), HomeActivity::class.java).apply {
                        putExtra("order", 1)
                    })

        } catch (e: java.lang.Exception) {
            genericHandler.showErrorMessage(e.message.toString())
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
            val deliveryNote = description
            val image: MultipartBody.Part = BaseUtils.compressAndCreateImageData(
                filePath,
                "delivered_file",
                requireContext()
            )

            val request = SellerActionRequestMultipart(
                deliveryNote, image, typeUser, orderNumber)
            viewModel.sellerActionsCallWithFile(order.id, request)
        }
    }

    override fun getRatingData(rating: Float, description: String) {
        val request = RatingOrderRequest(
            type = 10,
            order_no = order.orderNo,
            rating = rating,
            description = description
        )
        viewModel.ratingOrderCall(order.id, request)
    }

    override fun getDescription(description: String, userType: Int, type: Int) {
        val hashMap: HashMap<String, Any> = HashMap()
        val request = BuyerActionRequestMultipart(
            description = description)
        val requestRevision = BuyerRevisionAction(
            revision_description = description)
        var refferal = order.id
        if (userType == Constants.BUYER_USER) {
            if (type == 5) {
                hashMap["description"] = description
                hashMap["order_no"] = order.orderNo
                hashMap["type"] = type
                executeApi(order.id, request.description)
            }

            else if(type==3)
            {
                hashMap["revision_description"] = description
                hashMap["order"] = order.orderNo
                hashMap["type"] = type
                executeRevisionApi(order.id,requestRevision.revision_description)
            }
            else
                executeCompleteApi(order.id, request.description)
        }
    }
    override fun extendDeliveryTime(selectedDays: String, description: String, userType: Int, status: Int) {
        val hashMap: HashMap<String, Any> = HashMap()
        val request = ExtendDeliveryTimeModel(
            description = description, extended_delivery_days = selectedDays
        )
        hashMap["description"] = description
        hashMap["order_no"] = order.orderNo
        hashMap["type"] = status
        hashMap["extended_delivery_days"]=selectedDays
        executeExtendedApi(order.id,request.description,request.extended_delivery_days)
    }
}