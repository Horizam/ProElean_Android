package com.horizam.pro.elean.ui.main.view.fragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.horizam.pro.elean.App
import com.horizam.pro.elean.Constants
import com.horizam.pro.elean.R
import com.horizam.pro.elean.data.api.ApiHelper
import com.horizam.pro.elean.data.api.RetrofitBuilder
import com.horizam.pro.elean.data.model.response.BuyerRequest
import com.horizam.pro.elean.data.model.response.GeneralResponse
import com.horizam.pro.elean.data.model.response.PostedJob
import com.horizam.pro.elean.databinding.*
import com.horizam.pro.elean.ui.base.ViewModelFactory
import com.horizam.pro.elean.ui.main.adapter.BuyerRequestsAdapter
import com.horizam.pro.elean.ui.main.adapter.MyLoadStateAdapter
import com.horizam.pro.elean.ui.main.adapter.NotificationsAdapter
import com.horizam.pro.elean.ui.main.adapter.PostedJobsAdapter
import com.horizam.pro.elean.ui.main.callbacks.BuyerRequestsHandler
import com.horizam.pro.elean.ui.main.callbacks.GenericHandler
import com.horizam.pro.elean.ui.main.callbacks.OnItemClickListener
import com.horizam.pro.elean.ui.main.viewmodel.BuyerRequestsViewModel
import com.horizam.pro.elean.ui.main.viewmodel.PostedJobsViewModel
import com.horizam.pro.elean.utils.Resource
import com.horizam.pro.elean.utils.Status


class BuyerRequestsFragment : Fragment(), OnItemClickListener, BuyerRequestsHandler {

    private lateinit var binding: FragmentBuyerRequestsBinding
    private lateinit var adapter: BuyerRequestsAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: BuyerRequestsViewModel
    private lateinit var genericHandler: GenericHandler
    private lateinit var dialogDelete: Dialog
    private lateinit var bindingDeleteDialog: DialogDeleteBinding
    private lateinit var dialogFilterBuyerRequests: Dialog
    private lateinit var bindingDialog: DialogFilterBuyerRequestsBinding


    override fun onAttach(context: Context) {
        super.onAttach(context)
        genericHandler = context as GenericHandler
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBuyerRequestsBinding.inflate(layoutInflater,container,false)
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
        genericHandler.showProgressBar(true)
        viewModel.getBuyerRequestsCall()
    }

    private fun initViews() {
        adapter = BuyerRequestsAdapter(this,this)
        recyclerView = binding.rvBuyerRequests
        initDeleteDialog()
        initFilterDialog()
    }

    private fun initFilterDialog() {
        dialogFilterBuyerRequests = Dialog(requireContext())
        bindingDialog = DialogFilterBuyerRequestsBinding.inflate(layoutInflater)
        dialogFilterBuyerRequests.setContentView(bindingDialog.root)
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

    private fun setAdapterLoadState(adapter: BuyerRequestsAdapter) {
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
            toolbar.ivSecond.setOnClickListener {
                dialogFilterBuyerRequests.show()
            }
        }
        bindingDialog.rgBuyerRequest.setOnCheckedChangeListener(filterBuyerRequests)
    }

    private val filterBuyerRequests = RadioGroup.OnCheckedChangeListener { radioGroup, checkedId ->
        dialogFilterBuyerRequests.dismiss()
        val radioButton = radioGroup.findViewById<RadioButton>(checkedId)
        viewModel.getBuyerRequestsCall(radioButton.text.toString().trim().replace(" ","_"))
    }

    private fun setToolbarData() {
        binding.toolbar.ivToolbar.setImageResource(R.drawable.ic_back)
        binding.toolbar.ivSecond.isVisible = true
        binding.toolbar.tvToolbar.text = App.getAppContext()!!.getString(R.string.str_buyer_requests)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(BuyerRequestsViewModel::class.java)
    }

    private fun setupObservers() {
        viewModel.buyerRequests.observe(viewLifecycleOwner) {
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
        }
       viewModel.deleteBuyerRequest.observe(viewLifecycleOwner, deleteBuyerRequestObserver)
    }

    private val deleteBuyerRequestObserver = Observer<Resource<GeneralResponse>> {
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
    }

    private fun handleResponse(response: GeneralResponse) {
        genericHandler.showMessage(response.message)
        if (response.status == Constants.STATUS_OK) {
            exeApi()
        }
    }

    override fun <T> onItemClick(item: T) {
        if (item is BuyerRequest) {
            /*BuyerRequestsFragmentDirections.actionBuyerRequestsFragmentToSellerServicesFragment(item.id).also {
                findNavController().navigate(it)
            }*/
        }
    }

    override fun <T> cancelOffer(item: T) {
        if (item is BuyerRequest) {
            dialogDelete.show()
            bindingDeleteDialog.btnYes.setOnClickListener {
                dialogDelete.dismiss()
                genericHandler.showProgressBar(true)
                viewModel.deleteBuyerRequestCall(item.uuid)
            }
            bindingDeleteDialog.btnNo.setOnClickListener { dialogDelete.dismiss() }
        }
    }

    override fun <T> sendOffer(item: T) {
        if (item is BuyerRequest) {
            BuyerRequestsFragmentDirections.actionBuyerRequestsFragmentToSellerServicesFragment(item.id).also {
                findNavController().navigate(it)
            }
        }
    }

}