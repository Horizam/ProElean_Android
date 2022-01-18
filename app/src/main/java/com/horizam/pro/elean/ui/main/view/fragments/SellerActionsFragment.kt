package com.horizam.pro.elean.ui.main.view.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.horizam.pro.elean.App
import com.horizam.pro.elean.R
import com.horizam.pro.elean.R.color.white_grey_color
import com.horizam.pro.elean.data.api.ApiHelper
import com.horizam.pro.elean.data.api.RetrofitBuilder
import com.horizam.pro.elean.data.model.SellerActionModel
import com.horizam.pro.elean.data.model.response.SellerDataModel
import com.horizam.pro.elean.databinding.FragmentSellerActionsBinding
import com.horizam.pro.elean.ui.base.ViewModelFactory
import com.horizam.pro.elean.ui.main.adapter.SellerActionAdapter
import com.horizam.pro.elean.ui.main.callbacks.GenericHandler
import com.horizam.pro.elean.ui.main.callbacks.OnItemClickListener
import com.horizam.pro.elean.ui.main.view.activities.ManageSalesActivity
import com.horizam.pro.elean.ui.main.viewmodel.SellerViewModel
import com.horizam.pro.elean.utils.BaseUtils
import com.horizam.pro.elean.utils.Status


class SellerActionsFragment : Fragment(), OnItemClickListener {

    private lateinit var binding: FragmentSellerActionsBinding
    private lateinit var navController: NavController
    private lateinit var viewModel: SellerViewModel
    private lateinit var sellerActionAdapter: SellerActionAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var genericHandler: GenericHandler
    private lateinit var sellerActionList: ArrayList<SellerActionModel>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSellerActionsBinding.inflate(layoutInflater, container, false)
        setToolbarData()
        setupViewModel()
        initViews()
        setClickListeners()
        setAdapter()
        exeApi()
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        genericHandler = context as GenericHandler
    }

    private fun exeApi() {
        viewModel.sellerData.observe(viewLifecycleOwner, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        genericHandler.showProgressBar(false)
                        handleResponse(resource.data)
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

    private fun <T> handleResponse(item: T) {
        when (item) {
            is SellerDataModel -> {
                binding.apply {
                    item.apply {
                        tvPersonalBalanceValue.text = "$$availabe_balance"
                        tvAvgSellingPriceValue.text = "$$average_selling"
                        tvPendingClearanceValue.text = "$$pending_balance"
                        tvEarningInDecemberValue.text = "$$monthly_selling"
                        tvActiveOrdersrValue.apply {
                            text = ""
                            append("$active_orders")
                            val spannable = SpannableStringBuilder(" ($$active_orders_balance)")
                            spannable.setSpan(
                                ForegroundColorSpan(ContextCompat.getColor(context, white_grey_color)),
                                0, spannable.length,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                            )
                            append(spannable)
                        }
                        tvCancelledOrdersValue.apply {
                            text = ""
                            append("$cancelled_orders")
                            val spannable = SpannableStringBuilder(" (-$$cancelled_orders_balance)")
                            spannable.setSpan(
                                ForegroundColorSpan(ContextCompat.getColor(context, white_grey_color)),
                                0, spannable.length,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                            )
                            append(spannable)
                        }
                    }
                }
            }
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(SellerViewModel::class.java)
    }

    private fun setAdapter() {
        setDetailSellerActionList()
        sellerActionAdapter = SellerActionAdapter(sellerActionList, this)
        linearLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvSellerAction.layoutManager = linearLayoutManager
        binding.rvSellerAction.adapter = sellerActionAdapter
    }

    private fun setDetailSellerActionList() {
        sellerActionList.add(
            SellerActionModel(
                title = "Create Service",
                image = R.drawable.ic_create_service
            )
        )
        sellerActionList.add(
            SellerActionModel(
                title = "Buyer Requests",
                image = R.drawable.ic_buyer_request
            )
        )
        sellerActionList.add(
            SellerActionModel(
                title = "Manage Services",
                image = R.drawable.ic_list
            )
        )
        sellerActionList.add(
            SellerActionModel(
                title = "Analytics",
                image = R.drawable.ic_analytics
            )
        )
        sellerActionList.add(SellerActionModel(title = "Earnings", image = R.drawable.ic_budget))
    }

    private fun initViews() {
        binding.toolbar.ivToolbar.visibility = View.INVISIBLE
        navController = this.findNavController()
        sellerActionList = ArrayList()
    }

    private fun setClickListeners() {
        binding.apply {
            toolbar.ivToolbar.setOnClickListener {
                navController.popBackStack()
            }
            cardCreateService.setOnClickListener {
                navController.navigate(R.id.createServiceFragment)
            }
            cardManageSales.setOnClickListener {
                startActivity(Intent(requireActivity(), ManageSalesActivity::class.java))
            }
            cardBuyerRequests.setOnClickListener {
                navController.navigate(R.id.buyerRequestsFragment)
            }
            cardManageServices.setOnClickListener {
                navController.navigate(R.id.manageServicesFragment)
            }
            cardAnalytics.setOnClickListener {
                navController.navigate(R.id.analyticsFragment)
            }
            cardEarnings.setOnClickListener {
                navController.navigate(R.id.earningsFragment)
            }
        }
    }

    private fun setToolbarData() {
        binding.toolbar.ivToolbar.setImageResource(R.drawable.ic_back)
        binding.toolbar.tvToolbar.text =
            App.getAppContext()!!.getString(R.string.str_seller_actions)
    }

    override fun <T> onItemClick(item: T) {
        if (item is Int) {
            when (item) {
                0 -> {
                    navController.navigate(
                        R.id.createServiceFragment,
                        null,
                        BaseUtils.animationOpenScreen()
                    )
                }
                1 -> {
                    navController.navigate(
                        R.id.buyerRequestsFragment,
                        null,
                        BaseUtils.animationOpenScreen()
                    )
                }
                2 -> {
                    navController.navigate(
                        R.id.manageServicesFragment,
                        null,
                        BaseUtils.animationOpenScreen()
                    )
                }
                3 -> {
//                    navController.navigate(
//                        R.id.analyticsFragment,
//                        null,
//                        BaseUtils.animationOpenScreen()
//                    )
                }
                4 -> {
//                    navController.navigate(
//                        R.id.earningsFragment,
//                        null,
//                        BaseUtils.animationOpenScreen()
//                    )
                }
            }
        }
    }

}