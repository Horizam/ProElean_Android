package com.horizam.pro.elean.ui.main.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.horizam.pro.elean.R
import com.horizam.pro.elean.databinding.FragmentSelectLanguageBottomSheetBinding

class SelectLanguageBottomSheet(var languageList: List<String>) : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentSelectLanguageBottomSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSelectLanguageBottomSheetBinding.
        inflate(layoutInflater, container, false)

        loadData()
        setOnClickListener()
        setRadioGroupListener()

        return binding.root
    }

    private fun loadData() {
        for(element in languageList){
            val radioButton = RadioButton(requireContext())
            radioButton.text = element
            radioButton.id = View.generateViewId()
            val radioGroup = RadioGroup.LayoutParams(RadioGroup.LayoutParams.FILL_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT)
            binding.radioGroup.addView(radioButton, radioGroup)
        }
    }

    private fun setRadioGroupListener() {
        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            val radioButton = binding.root.findViewById<RadioButton>(checkedId)
            Toast.makeText(requireContext() , radioButton.text.toString() , Toast.LENGTH_SHORT).show()
        }
    }

    private fun setOnClickListener() {
        binding.btnSubmit.setOnClickListener{
            dismiss()
        }
    }

}