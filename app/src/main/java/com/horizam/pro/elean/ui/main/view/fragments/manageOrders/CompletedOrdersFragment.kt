package com.horizam.pro.elean.ui.main.view.fragments.manageOrders

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.Gson
import com.horizam.pro.elean.BuyerOrders
import com.horizam.pro.elean.Constants
import com.horizam.pro.elean.data.api.ApiHelper
import com.horizam.pro.elean.data.api.RetrofitBuilder
import com.horizam.pro.elean.data.model.response.Order
import com.horizam.pro.elean.data.model.response.OrdersResponse
import com.horizam.pro.elean.databinding.FragmentOrdersGenericBinding
import com.horizam.pro.elean.ui.base.ViewModelFactory
import com.horizam.pro.elean.ui.main.adapter.CompletedOrdersAdapter
import com.horizam.pro.elean.ui.main.callbacks.GenericHandler
import com.horizam.pro.elean.ui.main.callbacks.OnItemClickListener
import com.horizam.pro.elean.ui.main.view.activities.OrderDetailsActivity
import com.horizam.pro.elean.ui.main.viewmodel.BuyersOrdersViewModel
import com.horizam.pro.elean.utils.Status
import java.lang.Exception


class CompletedOrdersFragment : Fragment(), OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    private lateinit var binding: FragmentOrdersGenericBinding
    private lateinit var adapter: CompletedOrdersAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: BuyersOrdersViewModel
    private lateinit var genericHandler: GenericHandler
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onAttach(context: Context) {
        super.onAttach(context)
        genericHandler = context as GenericHandler
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrdersGenericBinding.inflate(layoutInflater, container, false)
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
        viewModel.getBuyerOrdersCall(BuyerOrders.Completed)
    }

    private fun initViews() {
        adapter = CompletedOrdersAdapter(this)
        recyclerView = binding.rvOrders
        swipeRefreshLayout = binding.swipeRefresh
        swipeRefreshLayout.setOnRefreshListener(this)
    }

    private fun setRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    private fun setOnClickListeners() {
        binding.apply {
            btnRetry.setOnClickListener {
                viewModel.getBuyerOrdersCall(BuyerOrders.Completed)
            }
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(BuyersOrdersViewModel::class.java)
    }

    private fun setupObservers() {
        viewModel.buyerOrders.observe(viewLifecycleOwner, {
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
                        genericHandler.showMessage(it.message.toString())
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
            genericHandler.showMessage(e.message.toString())
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
                it.putExtra(Constants.ORDER_USER_ROLE, Constants.BUYER_USER)
                it.putExtra(Constants.ORDER_USER_ACTION, BuyerOrders.Completed)
                resultLauncher.launch(it)
            }
        }
    }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
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