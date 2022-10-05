package com.horizam.pro.elean.ui.main.view.fragments.manageOrders

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.Gson
import com.horizam.pro.elean.BuyerOrders
import com.horizam.pro.elean.Constants
import com.horizam.pro.elean.data.api.ApiHelper
import com.horizam.pro.elean.data.api.RetrofitBuilder
import com.horizam.pro.elean.data.model.response.Order
import com.horizam.pro.elean.databinding.FragmentOrdersGenericBinding
import com.horizam.pro.elean.ui.base.ViewModelFactory
import com.horizam.pro.elean.ui.main.adapter.ActiveOrdersAdapter
import com.horizam.pro.elean.ui.main.adapter.MyLoadStateAdapter
import com.horizam.pro.elean.ui.main.callbacks.GenericHandler
import com.horizam.pro.elean.ui.main.callbacks.OnItemClickListener
import com.horizam.pro.elean.ui.main.view.activities.OrderDetailsActivity
import com.horizam.pro.elean.ui.main.viewmodel.BuyersOrdersViewModel


class ActiveOrdersFragment : Fragment(), OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    private lateinit var binding: FragmentOrdersGenericBinding
    private lateinit var adapter: ActiveOrdersAdapter
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
        if (viewModel.buyerOrders.value == null)
        viewModel.getBuyerOrdersCall(BuyerOrders.Active)
    }
    private fun initViews() {
        adapter = ActiveOrdersAdapter(this)
        recyclerView = binding.rvOrders
        swipeRefreshLayout = binding.swipeRefresh
        swipeRefreshLayout.setOnRefreshListener(this)
    }

    private fun setRecyclerView() {
//        recyclerView.layoutManager = LinearLayoutManager(requireContext())
//        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext()).also { layoutManager ->
            layoutManager.reverseLayout = false
            layoutManager.stackFromEnd = true
        }
        recyclerView.let {
            it.setHasFixedSize(true)
            it.layoutManager = recyclerView.layoutManager
            it.adapter = adapter.withLoadStateHeaderAndFooter(
                header = MyLoadStateAdapter { adapter.retry() },
                footer = MyLoadStateAdapter { adapter.retry() }
            )
        }
        setAdapterLoadState(adapter)
        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                if (positionStart == 0) {
                    (recyclerView.layoutManager as LinearLayoutManager).scrollToPosition(0)
                }
            }
        })
        setupObservers()
    }
    private fun setAdapterLoadState(adapter: ActiveOrdersAdapter) {
        adapter.addLoadStateListener { loadState ->
            binding.apply {
//                genericHandler.showProgressBar(loadState.source.refresh is LoadState.Loading)
//                recyclerView.isVisible = loadState.source.refresh is LoadState.NotLoading
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
            btnRetry.setOnClickListener {
                viewModel.getBuyerOrdersCall(BuyerOrders.Active)
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
                adapter.submitData(viewLifecycleOwner.lifecycle, it)
    })
    }

    override fun <T> onItemClick(item: T) {
        if (item is Order) {
            Intent(requireContext(), OrderDetailsActivity::class.java).also {
                val gson = Gson()
                it.putExtra(Constants.ORDER, gson.toJson(item))
                it.putExtra(Constants.ORDER_USER_ROLE, Constants.BUYER_USER)
                it.putExtra(Constants.ORDER_USER_ACTION, BuyerOrders.Active)
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