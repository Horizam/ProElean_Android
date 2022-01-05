package com.horizam.pro.elean.ui.main.view.fragments.manageSales

import android.annotation.SuppressLint
import android.content.res.Resources
import android.os.Bundle
import android.provider.ContactsContract
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.load.engine.Resource
import com.horizam.pro.elean.R
import com.horizam.pro.elean.databinding.FragmentHomeBinding
import com.horizam.pro.elean.databinding.FragmentProfileBinding


class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater, container, false)

        initView()
        setOnClickListener()

        return binding.root
    }

    private fun setOnClickListener() {
        binding.toolbar.ivToolbar.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initView() {
        binding.toolbar.ivToolbar.setImageResource(R.drawable.ic_back)
        binding.toolbar.tvToolbar.text = "Profile"
    }
}