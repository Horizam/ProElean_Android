package com.horizam.pro.elean.ui.main.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.horizam.pro.elean.Constants
import com.horizam.pro.elean.R
import com.horizam.pro.elean.databinding.FragmentOrderBinding
import com.horizam.pro.elean.databinding.FragmentProfileBinding
import com.horizam.pro.elean.ui.main.adapter.ViewPagerManageOrdersAdapter
import com.horizam.pro.elean.ui.main.adapter.ViewPagerManageSalesAdapter
import com.horizam.pro.elean.utils.PrefManager
import kotlinx.android.synthetic.main.fragment_order.view.*


class OrderFragment : Fragment() {
    private lateinit var binding: FragmentOrderBinding
    private lateinit var listFragmentTitles: ArrayList<String>
    private lateinit var viewPagerOrdersFragmentAdapter: ViewPagerManageOrdersAdapter
    private lateinit var viewPagerSalesFragmentAdapter: ViewPagerManageSalesAdapter
    private lateinit var prefManager: PrefManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderBinding.inflate(layoutInflater, container, false)

        initViews()
        setTabs()

        return binding.root
    }

    private fun initViews() {
        binding.toolbar.iv_toolbar.visibility = View.INVISIBLE
        prefManager = PrefManager(requireContext())
        listFragmentTitles = arrayListOf(
            "Active",
            "Late",
            "Delivered",
            "Revision",
            "Completed",
            "Cancel",
            "Disputed"
        )
        if (prefManager.sellerMode == 0) {
            binding.tvUserMode.text = "Buyer"
            viewPagerOrdersFragmentAdapter = ViewPagerManageOrdersAdapter(this, listFragmentTitles)
        }else{
            binding.tvUserMode.text = "Seller"
            viewPagerSalesFragmentAdapter = ViewPagerManageSalesAdapter(this,listFragmentTitles)
        }
    }

    private fun setTabs() {
        if(prefManager.sellerMode == 0){
            binding.viewPager.adapter = viewPagerOrdersFragmentAdapter
        }else{
            binding.viewPager.adapter = viewPagerSalesFragmentAdapter
        }
        TabLayoutMediator(
            binding.tabLayout, binding.viewPager
        ) { tab: TabLayout.Tab, position: Int ->
            tab.text = listFragmentTitles[position]
        }.attach()
    }
}