package com.horizam.pro.elean.ui.main.view.fragments.manageSales

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.horizam.pro.elean.databinding.FragmentProfileBinding
import com.horizam.pro.elean.ui.main.adapter.ViewPagerFragmentAdapter
import com.horizam.pro.elean.ui.main.callbacks.GenericHandler
import com.horizam.pro.elean.ui.main.viewmodel.ProfileViewModel
import com.horizam.pro.elean.utils.PrefManager
import kotlinx.android.synthetic.main.fragment_order.view.*

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater, container, false)

        initViews()
        setTabs()
        setClickListeners()

        return binding.root
    }

    private fun initViews() {
        binding.ivToolbar.visibility = View.INVISIBLE
        prefManager = PrefManager(requireContext())
        listFragmentTitles = arrayListOf("About", "Services", "Reviews")
        viewPagerFragmentAdapter = ViewPagerFragmentAdapter(this, listFragmentTitles)
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