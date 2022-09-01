package com.horizam.pro.elean.ui.main.view.fragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.horizam.pro.elean.App
import com.horizam.pro.elean.R
import com.horizam.pro.elean.data.api.ApiHelper
import com.horizam.pro.elean.data.api.RetrofitBuilder
import com.horizam.pro.elean.data.model.requests.AcceptOrderRequest
import com.horizam.pro.elean.data.model.response.GeneralResponse
import com.horizam.pro.elean.data.model.response.Offer
import com.horizam.pro.elean.databinding.DialogDeleteBinding
import com.horizam.pro.elean.databinding.DialogOrderSuccessBinding
import com.horizam.pro.elean.databinding.FragmentViewOffersBinding
import com.horizam.pro.elean.ui.base.ViewModelFactory
import com.horizam.pro.elean.ui.main.adapter.MyLoadStateAdapter
import com.horizam.pro.elean.ui.main.adapter.ViewOfferAdapter
import com.horizam.pro.elean.ui.main.callbacks.CheckoutHandler
import com.horizam.pro.elean.ui.main.callbacks.GenericHandler
import com.horizam.pro.elean.ui.main.callbacks.OnItemClickListener
import com.horizam.pro.elean.ui.main.callbacks.ViewOffersHandler
import com.horizam.pro.elean.ui.main.viewmodel.JobOffersViewModel
import com.horizam.pro.elean.utils.PrefManager
import com.horizam.pro.elean.utils.Resource
import com.horizam.pro.elean.utils.Status


class ViewOffersFragment : Fragment(), OnItemClickListener, ViewOffersHandler, CheckoutHandler {

    private lateinit var binding: FragmentViewOffersBinding
    private lateinit var adapter: ViewOfferAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: JobOffersViewModel
    private lateinit var genericHandler: GenericHandler
    private lateinit var dialogDelete: Dialog
    private lateinit var prefManager: PrefManager
    private lateinit var bindingDeleteDialog: DialogDeleteBinding
    private lateinit var dialogOrderStatus: Dialog
    private lateinit var bindingDialogOrderSuccessBinding: DialogOrderSuccessBinding
    private val args: ViewOffersFragmentArgs by navArgs()
    private var offerId: String = ""

    override fun onAttach(context: Context) {
        super.onAttach(context)
        genericHandler = context as GenericHandler
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentViewOffersBinding.inflate(layoutInflater, container, false)
        setToolbarData()
        initViews()
        setupViewModel()
        setupObservers()
        setRecyclerView()
        setOnClickListeners()
        exeApi()
        return binding.root
    }

    private fun exeApi() {
        val id = args.id
        genericHandler.showProgressBar(true)
        viewModel.getJobOffersCall(id)
    }

    private fun initViews() {
        prefManager = PrefManager(requireContext())
        adapter = ViewOfferAdapter(this, this)
        recyclerView = binding.rvViewOffers
        initDeleteDialog()
        initOrderStatusDialog()
    }

    private fun initOrderStatusDialog() {
        dialogOrderStatus = Dialog(requireContext())
        dialogOrderStatus.setCancelable(true)
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

    private fun initDeleteDialog() {
        dialogDelete = Dialog(requireContext())
        bindingDeleteDialog = DialogDeleteBinding.inflate(layoutInflater)
        dialogDelete.setContentView(bindingDeleteDialog.root)
    }

    private fun setRecyclerView() {
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

    private fun setAdapterLoadState(adapter: ViewOfferAdapter) {
        adapter.addLoadStateListener { loadState ->
            binding.apply {
                genericHandler.showProgressBar(loadState.source.refresh is LoadState.Loading)
                recyclerView.isVisible = loadState.source.refresh is LoadState.NotLoading
                btnRetry.isVisible = loadState.source.refresh is LoadState.Error
                textViewError.isVisible = loadState.source.refresh is LoadState.Error
                // no results
                if (loadState.source.refresh is LoadState.NotLoading &&
                    loadState.append.endOfPaginationReached &&
                    adapter.itemCount < 1
                ) {
                    recyclerView.isVisible = false
                    tvPlaceholder.isVisible = true
                } else {
                    tvPlaceholder.isVisible = false
                }
            }
        }
    }

    private fun setOnClickListeners() {
        binding.apply {
            toolbar.ivToolbar.setOnClickListener {
                findNavController().popBackStack()
            }
            btnRetry.setOnClickListener {
                adapter.retry()
            }
        }
        bindingDialogOrderSuccessBinding.btnContinue.setOnClickListener {
            dialogOrderStatus.dismiss()
//            startActivity(Intent(requireActivity(), ManageOrdersActivity::class.java))
        }
    }

    private fun setToolbarData() {
        binding.toolbar.ivToolbar.setImageResource(R.drawable.ic_back)
        binding.toolbar.tvToolbar.text = App.getAppContext()!!.getString(R.string.str_view_offers)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(JobOffersViewModel::class.java)
    }

    private fun setupObservers() {
        viewModel.jobOffers.observe(viewLifecycleOwner) {
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
        }
        viewModel.deleteJobOffer.observe(viewLifecycleOwner, deleteOfferObserver)
        viewModel.acceptOrder.observe(viewLifecycleOwner, acceptOfferObserver)
    }

    private val deleteOfferObserver = Observer<Resource<GeneralResponse>> {
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

    private val acceptOfferObserver = Observer<Resource<GeneralResponse>> {
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
            }}
    }


    private fun handleResponse(response: GeneralResponse) {
//        dialogOrderStatus.show()
        exeApi()
        genericHandler.showSuccessMessage(response.message)
    }

    override fun <T> onItemClick(item: T) {
        if (item is Offer) {

        }
    }

    override fun <T> deleteItem(item: T) {
        if (item is Offer) {
            dialogDelete.show()
            bindingDeleteDialog.btnYes.setOnClickListener {
                dialogDelete.dismiss()
                genericHandler.showProgressBar(true)
                viewModel.deletePostedJobCall(item.id)
            }
            bindingDeleteDialog.btnNo.setOnClickListener { dialogDelete.dismiss() }
        }
    }

    override fun <T> viewProfile(item: T) {
        if (item is Offer) {
//            Intent(requireActivity(), UserAboutActivity::class.java).also {
//                it.putExtra("id", item.profile.id)
//                startActivity(it)
//            }
        }
    }

    override fun <T> askQuestion(item: T) {
        if (item is Offer) {
            if (prefManager.userId != item.profile.id) {
                ViewOffersFragmentDirections.actionViewOffersFragmentToMessagesFragment(
                    userName = item.profile.name,
                    photo = item.profile.image,
                    id = item.profile.id
                )
                    .also {
                        findNavController().navigate(it)
                    }
            }
        }
    }

    override fun <T> order(item: T) {
        if (item is Offer) {
            bindingDeleteDialog.tvTitle.text = getString(R.string.str_are_you_sure_to_place_order)
            dialogDelete.show()
            bindingDeleteDialog.btnYes.setOnClickListener {
                dialogDelete.dismiss()
                offerId = item.id
                val checkoutBottomSheet = CheckoutBottomSheet(this)
                checkoutBottomSheet.show(
                    requireActivity().supportFragmentManager,
                    CheckoutBottomSheet.TAG
                )
            }
            bindingDeleteDialog.btnNo.setOnClickListener { dialogDelete.dismiss() }
        }
    }

    override fun sendToken(token: String) {
        if (offerId != "") {
            genericHandler.showProgressBar(true)
            val acceptOrderRequest = AcceptOrderRequest(
                offer_id = offerId,
                token = token
            )
            viewModel.acceptOrderCall(acceptOrderRequest)
        }
    }

}