package com.horizam.pro.elean.ui.main.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.horizam.pro.elean.App
import com.horizam.pro.elean.R
import com.horizam.pro.elean.databinding.FragmentAnalyticsBinding
import com.horizam.pro.elean.databinding.FragmentBuyerRequestsBinding
import com.horizam.pro.elean.databinding.FragmentManageServicesBinding
import com.horizam.pro.elean.databinding.FragmentNotificationsBinding
import com.horizam.pro.elean.ui.main.adapter.BuyerRequestsAdapter
import com.horizam.pro.elean.ui.main.adapter.ManageServicesAdapter
import com.horizam.pro.elean.ui.main.adapter.NotificationsAdapter


class AnalyticsFragment : Fragment() {

    private lateinit var binding: FragmentAnalyticsBinding
    private val navController: NavController by lazy {
        this.findNavController()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAnalyticsBinding.inflate(layoutInflater, container, false)
        setToolbarData()
        initViews()
        setOnClickListeners()
        return binding.root
    }

    private fun initViews() {

    }

    private fun setOnClickListeners() {
        binding.toolbar.ivToolbar.setOnClickListener {
            navController.popBackStack()
        }
    }

    private fun setToolbarData() {
        binding.toolbar.ivToolbar.setImageResource(R.drawable.ic_back)
        binding.toolbar.tvToolbar.text = App.getAppContext()!!.getString(R.string.str_analytics)
    }

}