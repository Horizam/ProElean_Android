package com.horizam.pro.elean.ui.main.view.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.horizam.pro.elean.data.api.ApiHelper
import com.horizam.pro.elean.data.api.RetrofitBuilder
import com.horizam.pro.elean.data.model.requests.OrderTimelineRequest
import com.horizam.pro.elean.data.model.response.Action
import com.horizam.pro.elean.data.model.response.OrderTimelineResponse
import com.horizam.pro.elean.databinding.*
import com.horizam.pro.elean.ui.base.ViewModelFactory
import com.horizam.pro.elean.ui.main.adapter.OrderTimelineAdapter
import com.horizam.pro.elean.ui.main.callbacks.GenericHandler
import com.horizam.pro.elean.ui.main.callbacks.OnItemClickListener
import com.horizam.pro.elean.ui.main.viewmodel.SellerOrdersViewModel
import com.horizam.pro.elean.utils.Status
import java.lang.Exception


class TimelineFragment(private val orderNo: String) : Fragment(), OnItemClickListener {

    private lateinit var binding: FragmentTimelineBinding
    private lateinit var adapter: OrderTimelineAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: SellerOrdersViewModel
    private lateinit var genericHandler: GenericHandler

    override fun onAttach(context: Context) {
        super.onAttach(context)
        genericHandler = context as GenericHandler
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTimelineBinding.inflate(layoutInflater, container, false)
        initViews()
        setupViewModel()
        setupObservers()
        setRecyclerView()
        setOnClickListeners()
        exeApi()
        return binding.root
    }

    private fun exeApi() {
        val orderTimelineRequest = OrderTimelineRequest(
            order_no = orderNo
        )
        viewModel.orderTimelineCall(orderTimelineRequest)
    }

    private fun initViews() {
        adapter = OrderTimelineAdapter(this)
        recyclerView = binding.rvTimeline
    }

    private fun setRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    private fun setOnClickListeners() {
        binding.apply {
            btnRetry.setOnClickListener {
                val orderTimelineRequest = OrderTimelineRequest(
                    order_no = orderNo
                )
                viewModel.orderTimelineCall(orderTimelineRequest)
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
        viewModel.orderTimeline.observe(viewLifecycleOwner, {
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
        binding.rvTimeline.isVisible = layout
    }

    private fun handleResponse(response: OrderTimelineResponse) {
        try {
            setUIData(response.actionList)
        } catch (e: Exception) {
            genericHandler.showMessage(e.message.toString())
        }
    }

    private fun setUIData(list: List<Action>) {
        adapter.submitList(list)
        binding.tvPlaceholder.isVisible = list.isEmpty()
    }

    override fun <T> onItemClick(item: T) {
        if (item is Action) {
            // to be implemented
        }
    }
}