package com.horizam.pro.elean.ui.main.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.horizam.pro.elean.R
import com.horizam.pro.elean.databinding.FragmentMessagesBinding
import com.horizam.pro.elean.databinding.FragmentSelectDaysBottomSheetBinding

class SelectDaysBottomSheetFragment : Fragment() {
    private lateinit var binding: FragmentSelectDaysBottomSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSelectDaysBottomSheetBinding.inflate(layoutInflater, container, false)

        initComponent()
        setListener()

        return binding.root
    }

    private fun setListener() {

    }

    private fun initComponent() {

    }
}