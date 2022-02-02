package com.horizam.pro.elean.ui.main.view.fragments.manageSales

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.Gson
import com.horizam.pro.elean.Constants
import com.horizam.pro.elean.R
import com.horizam.pro.elean.SellerOrders
import com.horizam.pro.elean.data.api.ApiHelper
import com.horizam.pro.elean.data.api.RetrofitBuilder
import com.horizam.pro.elean.data.model.response.Order
import com.horizam.pro.elean.data.model.response.OrdersResponse
import com.horizam.pro.elean.databinding.DialogFilterOrdersBinding
import com.horizam.pro.elean.databinding.FragmentSalesBinding
import com.horizam.pro.elean.ui.base.ViewModelFactory
import com.horizam.pro.elean.ui.main.adapter.ActiveSalesAdapter
import com.horizam.pro.elean.ui.main.callbacks.GenericHandler
import com.horizam.pro.elean.ui.main.callbacks.OnItemClickListener
import com.horizam.pro.elean.ui.main.view.activities.OrderDetailsActivity
import com.horizam.pro.elean.ui.main.viewmodel.SellerOrdersViewModel
import com.horizam.pro.elean.utils.PrefManager
import com.horizam.pro.elean.utils.Status
import java.lang.Exception

class SalesFragment : Fragment(), OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {
        private lateinit var binding: FragmentSalesBinding
        private lateinit var adapter: ActiveSalesAdapter
        private lateinit var recyclerView: RecyclerView
        private lateinit var viewModel: SellerOrdersViewModel
        private lateinit var genericHandler: GenericHandler
        private lateinit var swipeRefreshLayout: SwipeRefreshLayout
        private lateinit var prefManager: PrefManager
        private lateinit var dialogFilterJobs: Dialog
        private lateinit var bindingDialog: DialogFilterOrdersBinding
        private var currentOrders: Int = 0

        override fun onAttach(context: Context) {
            super.onAttach(context)
            genericHandler = context as GenericHandler
        }

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            // Inflate the layout for this fragment
            binding = FragmentSalesBinding.inflate(layoutInflater, container, false)
            initViews()
            setupViewModel()
            setupObservers()
            setRecyclerView()
            setOnClickListeners()
            return binding.root
        }

        override fun onResume() {
            super.onResume()
            exeApi()
        }

        private fun exeApi() {
            viewModel.getSellerOrdersCall(currentOrders)
        }

        private fun initViews() {
            adapter = ActiveSalesAdapter(this)
            recyclerView = binding.rvOrders
            swipeRefreshLayout = binding.swipeRefresh
            swipeRefreshLayout.setOnRefreshListener(this)
            initFilterDialog()
            prefManager = PrefManager(requireActivity())
        }

        private fun initFilterDialog() {
            dialogFilterJobs = Dialog(requireContext())
            bindingDialog = DialogFilterOrdersBinding.inflate(layoutInflater)
            dialogFilterJobs.setContentView(bindingDialog.root)
        }

        private fun setRecyclerView() {
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.adapter = adapter
        }

        private fun setOnClickListeners() {
            binding.apply {
                btnRetry.setOnClickListener {
                    viewModel.getSellerOrdersCall(SellerOrders.Active)
                }
                tvUserMode.setOnClickListener {
                    dialogFilterJobs.show()
                }
            }
            bindingDialog.rgPostJobFilter.setOnCheckedChangeListener(filterServices)
        }

        private val filterServices = RadioGroup.OnCheckedChangeListener { radioGroup, checkedId ->
            dialogFilterJobs.dismiss()
            val radioButton = radioGroup.findViewById<RadioButton>(checkedId)
            when (radioButton.text) {
                getString(R.string.str_all) -> {
                    viewModel.getSellerOrdersCall(SellerOrders.all)
                    currentOrders = SellerOrders.all
                }
                getString(R.string.str_active) -> {
                    viewModel.getSellerOrdersCall(SellerOrders.Active)
                    currentOrders = SellerOrders.Active
                }
                getString(R.string.str_delivered) -> {
                    viewModel.getSellerOrdersCall(SellerOrders.Delivered)
                    currentOrders = SellerOrders.Delivered
                }
                getString(R.string.str_revision) -> {
                    viewModel.getSellerOrdersCall(SellerOrders.Revision)
                    currentOrders = SellerOrders.Revision
                }
                getString(R.string.str_completed) -> {
                    viewModel.getSellerOrdersCall(SellerOrders.Completed)
                    currentOrders = SellerOrders.Completed
                }
                getString(R.string.str_disputed) -> {
                    viewModel.getSellerOrdersCall(SellerOrders.Disputed)
                    currentOrders = SellerOrders.Disputed
                }
                getString(R.string.str_late) -> {
                    viewModel.getSellerOrdersCall(SellerOrders.Late)
                    currentOrders = SellerOrders.Late
                }
                getString(R.string.str_cancel) -> {
                    viewModel.getSellerOrdersCall(SellerOrders.Cancel)
                    currentOrders = SellerOrders.Cancel
                }
            }
        }

        private fun setupViewModel() {
            viewModel = ViewModelProviders.of(
                this,
                ViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
            ).get(SellerOrdersViewModel::class.java)
        }

        private fun setupObservers() {
            viewModel.sellerOrders.observe(viewLifecycleOwner, {
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
        }

        private fun changeViewVisibility(textView: Boolean, button: Boolean, layout: Boolean) {
            binding.textViewError.isVisible = textView
            binding.btnRetry.isVisible = button
            binding.rvOrders.isVisible = layout
        }

        private fun handleResponse(response: OrdersResponse) {
            try {
                setUIData(response.orderList)
            } catch (e: Exception) {
                genericHandler.showErrorMessage(e.message.toString())
            }
        }

        private fun setUIData(list: List<Order>) {
            adapter.submitList(list)
            binding.tvPlaceholder.isVisible = list.isEmpty()
        }

        override fun <T> onItemClick(item: T) {
            if (item is Order) {
                Intent(requireContext(), OrderDetailsActivity::class.java).also {
                    val gson = Gson()
                    it.putExtra(Constants.ORDER, gson.toJson(item))
                    if (prefManager.sellerMode == 0) {
                        it.putExtra(Constants.ORDER_USER_ROLE, Constants.BUYER_USER)
                    } else {
                        it.putExtra(Constants.ORDER_USER_ROLE, Constants.SELLER_USER)
                    }
                    it.putExtra(Constants.ORDER_USER_ACTION, item.status_id)
                    resultLauncher.launch(it)
                }
            }
        }

        private val resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    exeApi()
                }
            }

        override fun onRefresh() {
            if (swipeRefreshLayout.isRefreshing) {
                swipeRefreshLayout.isRefreshing = false
            }
            exeApi()
        }
}
