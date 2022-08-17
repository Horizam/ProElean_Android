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
import com.horizam.pro.elean.data.model.BottomNotification
import com.horizam.pro.elean.databinding.FragmentOrderBinding
import com.horizam.pro.elean.databinding.FragmentProfileBinding
import com.horizam.pro.elean.ui.main.adapter.ViewPagerManageOrdersAdapter
import com.horizam.pro.elean.ui.main.adapter.ViewPagerManageSalesAdapter
import com.horizam.pro.elean.utils.PrefManager
import kotlinx.android.synthetic.main.fragment_order.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


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

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().post(BottomNotification(Constants.ORDER , 0))
    }

    private fun initViews() {
        binding.toolbar.iv_toolbar.visibility = View.INVISIBLE
        prefManager = PrefManager(requireContext())
        listFragmentTitles = arrayListOf(
            getString(R.string.str_active),
            getString(R.string.str_late),
            getString(R.string.str_delivered),
            getString(R.string.str_revision),
            getString(R.string.str_completed),
            getString(R.string.str_cancel),
            getString(R.string.str_disputed)
        )
        if (prefManager.sellerMode == 0) {
            binding.tvUserMode.text = getString(R.string.str_buyer)
            viewPagerOrdersFragmentAdapter = ViewPagerManageOrdersAdapter(this, listFragmentTitles)
        }else{
            binding.tvUserMode.text = getString(R.string.str_seller)
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