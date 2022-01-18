package com.horizam.pro.elean.ui.main.view.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.horizam.pro.elean.App
import com.horizam.pro.elean.Constants
import com.horizam.pro.elean.R
import com.horizam.pro.elean.data.api.ApiHelper
import com.horizam.pro.elean.data.api.RetrofitBuilder
import com.horizam.pro.elean.data.model.response.EarningsResponse
import com.horizam.pro.elean.data.model.response.SellerEarning
import com.horizam.pro.elean.databinding.*
import com.horizam.pro.elean.ui.base.ViewModelFactory
import com.horizam.pro.elean.ui.main.callbacks.GenericHandler
import com.horizam.pro.elean.ui.main.viewmodel.EarningsViewModel
import com.horizam.pro.elean.utils.Status
import java.lang.Exception


class EarningsFragment : Fragment() {

    private lateinit var binding: FragmentEarningsBinding
    private lateinit var viewModel: EarningsViewModel
    private lateinit var genericHandler: GenericHandler

    override fun onAttach(context: Context) {
        super.onAttach(context)
        genericHandler = context as GenericHandler
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEarningsBinding.inflate(layoutInflater, container, false)
        setToolbarData()
        initViews()
        setupViewModel()
        setupObservers()
        setOnClickListeners()
        return binding.root
    }

    private fun initViews() {
        // init views here
    }

    private fun setOnClickListeners() {
        binding.apply {
            toolbar.ivToolbar.setOnClickListener {
                findNavController().popBackStack()
            }
            cardViewPaypal.setOnClickListener {
                findNavController().navigate(R.id.bankDetailsFragment)
            }
            cardViewPaystack.setOnClickListener {
                findNavController().navigate(R.id.bankDetailsFragment)
            }
            cardViewPayoneer.setOnClickListener {
                findNavController().navigate(R.id.bankDetailsFragment)
            }
        }
    }

    private fun setToolbarData() {
        binding.toolbar.ivToolbar.setImageResource(R.drawable.ic_back)
        binding.toolbar.tvToolbar.text = App.getAppContext()!!.getString(R.string.str_earnings)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(EarningsViewModel::class.java)
    }

    private fun setupObservers() {
        viewModel.earnings.observe(viewLifecycleOwner, {
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
        })
    }

    private fun handleResponse(response: EarningsResponse) {
        try {
            setUIData(response.sellerEarning)
        } catch (e: Exception) {
            genericHandler.showErrorMessage(e.message.toString())
        }
    }

    private fun setUIData(sellerEarning: SellerEarning) {
        binding.tvTotalEarnings.text = sellerEarning.totalEarning.toString().plus(Constants.CURRENCY)
        binding.tvTotalCompletedOrders.text = sellerEarning.totalOrders.toString()
        binding.tvAvgSelling.text = sellerEarning.avgEarning.toString().plus(Constants.CURRENCY)
        binding.tvEarnedMonth.text = sellerEarning.lastMonthEarn.toString().plus(Constants.CURRENCY)
    }

}