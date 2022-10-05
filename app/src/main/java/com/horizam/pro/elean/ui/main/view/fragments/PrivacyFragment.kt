package com.horizam.pro.elean.ui.main.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.horizam.pro.elean.App
import com.horizam.pro.elean.R
import com.horizam.pro.elean.databinding.*
import com.horizam.pro.elean.ui.main.callbacks.LockHandler


class PrivacyFragment : Fragment() {

    private lateinit var binding: FragmentPrivacyBinding
    private val args:PrivacyFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPrivacyBinding.inflate(layoutInflater,container,false)
        setToolbarData()
        setClickListeners()
        setData()
        return binding.root
    }

    private fun setData() {
        binding.tvDescTerms.text = args.policy
    }

    private fun setClickListeners() {
        binding.toolbar.ivToolbar.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setToolbarData() {
        binding.toolbar.ivToolbar.setImageResource(R.drawable.ic_back)
        binding.toolbar.ivToolbar.isVisible=true
        binding.toolbar.tvToolbar.text =getString(R.string.privacy_policy)
    }
}