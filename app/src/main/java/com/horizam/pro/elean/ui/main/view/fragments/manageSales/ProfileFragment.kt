package com.horizam.pro.elean.ui.main.view.fragments.manageSales

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.airbnb.lottie.utils.Utils
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.horizam.pro.elean.databinding.FragmentProfileBinding
import com.horizam.pro.elean.ui.main.adapter.ViewPagerFragmentAdapter
import com.horizam.pro.elean.ui.main.callbacks.GenericHandler
import com.horizam.pro.elean.ui.main.view.activities.AuthenticationActivity
import com.horizam.pro.elean.utils.BaseUtils
import com.horizam.pro.elean.utils.PrefManager

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var listFragmentTitles: ArrayList<String>
    private lateinit var viewPagerFragmentAdapter: ViewPagerFragmentAdapter
    private lateinit var genericHandler: GenericHandler
    private lateinit var prefManager: PrefManager

    override fun onAttach(context: Context) {
        super.onAttach(context)
        genericHandler = context as GenericHandler
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BaseUtils.isUserProfileScreen = true
    }

    override fun onDestroy() {
        super.onDestroy()
        BaseUtils.isUserProfileScreen = false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater, container, false)

        initViews()
        if (prefManager.accessToken.isEmpty()) {
            this.findNavController().popBackStack()
            var intent = Intent(activity, AuthenticationActivity::class.java)
            startActivity(intent)
        } else {
            setTabs()
            setClickListeners()
        }

        return binding.root
    }

    private fun initViews() {
        binding.ivToolbar.visibility = View.INVISIBLE
        prefManager = PrefManager(requireContext())
        if (prefManager.sellerMode == 0) {
            listFragmentTitles = arrayListOf("About", "Reviews")
        } else if (prefManager.sellerMode == 1) {
            listFragmentTitles = arrayListOf("About", "Services", "Reviews")
        }
        viewPagerFragmentAdapter = ViewPagerFragmentAdapter(this, listFragmentTitles, prefManager)
    }


    private fun setTabs() {
        binding.viewPager.adapter = viewPagerFragmentAdapter
        TabLayoutMediator(
            binding.tabLayout, binding.viewPager
        ) { tab: TabLayout.Tab, position: Int ->
            tab.text = listFragmentTitles[position]
        }.attach()
    }

    private fun setClickListeners() {
        binding.ivToolbar.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}