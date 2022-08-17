package com.horizam.pro.elean.ui.main.view.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.horizam.pro.elean.R
import com.horizam.pro.elean.databinding.FragmentDescriptionBottomSheetBinding
import com.horizam.pro.elean.ui.main.callbacks.DescriptionHandler
import com.horizam.pro.elean.ui.main.callbacks.GenericHandler
import com.horizam.pro.elean.utils.BaseUtils.Companion.hideKeyboard

class DescriptionBottomSheet(var fragment: OrderDetailsFragment, var userType: Int, var type: Int) :
    BottomSheetDialogFragment() {

    private lateinit var binding: FragmentDescriptionBottomSheetBinding
    private lateinit var genericHandler: GenericHandler
    private lateinit var descriptionHandler: DescriptionHandler

    override fun onAttach(context: Context) {
        super.onAttach(context)
        genericHandler = context as GenericHandler
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDescriptionBottomSheetBinding.inflate(layoutInflater, container, false)

        initComponent()
        setOnClickListener()

        return binding.root
    }

    private fun initComponent() {
        descriptionHandler = fragment as DescriptionHandler
    }

    private fun setOnClickListener() {
        binding.btnSubmit.setOnClickListener {
            hideKeyboard()
            validateData()
        }
    }

    private fun validateData() {
        removeTextFieldsErrors()
        if (binding.etDescription.length() < 10) {
            genericHandler.showErrorMessage(getString(R.string.str_description_is_short))
            binding.textFieldDescription.error = getString(R.string.str_description_is_short)
        } else {
            descriptionHandler.getDescription(binding.etDescription.text.toString(), userType, type)
            hideKeyboard()
            dismiss()
        }
    }

    private fun removeTextFieldsErrors() {
        binding.textFieldDescription.error = null
    }
}