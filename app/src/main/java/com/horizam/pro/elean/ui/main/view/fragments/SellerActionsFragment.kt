package com.horizam.pro.elean.ui.main.view.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.horizam.pro.elean.App
import com.horizam.pro.elean.R
import com.horizam.pro.elean.data.model.SellerActionModel
import com.horizam.pro.elean.databinding.FragmentSellerActionsBinding
import com.horizam.pro.elean.ui.main.adapter.SellerActionAdapter
import com.horizam.pro.elean.ui.main.callbacks.OnItemClickListener
import com.horizam.pro.elean.ui.main.view.activities.ManageSalesActivity


class SellerActionsFragment : Fragment(), OnItemClickListener {

    private lateinit var binding: FragmentSellerActionsBinding
    private lateinit var navController: NavController
    private lateinit var sellerActionAdapter: SellerActionAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var sellerActionList: ArrayList<SellerActionModel>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSellerActionsBinding.inflate(layoutInflater, container, false)
        setToolbarData()
        initViews()
        setClickListeners()
        setAdapter()
        return binding.root
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
                title = "Manage Sales",
                image = R.drawable.ic_manage_service
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
                    navController.navigate(R.id.createServiceFragment)
                }
                1 -> {
                    startActivity(Intent(requireActivity(), ManageSalesActivity::class.java))
                }
                2 -> {
                    navController.navigate(R.id.buyerRequestsFragment)
                }
                3 -> {
                    navController.navigate(R.id.manageServicesFragment)
                }
                4 -> {
                    navController.navigate(R.id.analyticsFragment)
                }
                5 -> {
                    navController.navigate(R.id.earningsFragment)
                }
            }
        }
    }
}