package com.horizam.pro.elean.ui.main.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.horizam.pro.elean.R
import com.horizam.pro.elean.databinding.FragmentSelectCurrencyBottomSheetBinding
import android.widget.RadioGroup




class SelectCurrencyBottomSheet(var currencyList: List<String>) : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentSelectCurrencyBottomSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSelectCurrencyBottomSheetBinding.inflate(layoutInflater, container, false)
        loadData()
        setOnClickListener()
        setRadioGroupListener()

        return binding.root
    }

    private fun loadData() {
        for(element in currencyList){
            val radioButton = RadioButton(requireContext())
            radioButton.text = buildString {
        append(getString(R.string.str_currency))
        append(element)
    }
            radioButton.id = View.generateViewId()
            val radioGroup = RadioGroup.LayoutParams(RadioGroup.LayoutParams.FILL_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT)
            binding.radioGroup.addView(radioButton, radioGroup)
        }
    }

    private fun setOnClickListener() {
        binding.btnSubmit.setOnClickListener {
            dismiss()
        }
    }

    private fun setRadioGroupListener() {
        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            val radioButton = binding.root.findViewById<RadioButton>(checkedId)
            Toast.makeText(requireContext() , radioButton.text.toString() , Toast.LENGTH_SHORT).show()
        }
    }
}