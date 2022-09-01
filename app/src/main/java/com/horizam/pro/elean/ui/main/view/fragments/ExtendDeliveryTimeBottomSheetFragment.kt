package com.horizam.pro.elean.ui.main.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.horizam.pro.elean.R
import com.horizam.pro.elean.databinding.FragmentExtendDeliveryTimeBottomSheetBinding

class ExtendDeliveryTimeBottomSheetFragment(var orderDetailsFragment: OrderDetailsFragment,var userType: Int ,var status: Int) :
    BottomSheetDialogFragment(),
    AdapterView.OnItemSelectedListener {
    private lateinit var binding: FragmentExtendDeliveryTimeBottomSheetBinding
    private lateinit var daysAdapter: ArrayAdapter<String>
    private lateinit var daysArrayList: ArrayList<String>
    private var selectedDays: String = "2day"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            FragmentExtendDeliveryTimeBottomSheetBinding.inflate(layoutInflater, container, false)

        initComponent()
        setListener()
        setNoOfDaysSpinner()

        return binding.root
    }

    private fun setNoOfDaysSpinner() {
        for (pos in 0 until 59) {
            daysArrayList.add("${pos}day")
        }
        daysAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item, daysArrayList
        ).also {
            it.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            binding.spinnerDeliveryTime.adapter = it
        }
    }

    private fun setListener() {
        binding.btnSubmit.setOnClickListener {
            if (binding.etDescription.text!!.isEmpty()) {
                binding.tiDescription.error = getString(R.string.str_please_enter_description)
            } else {
                dismiss()
                orderDetailsFragment.extendDeliveryTime(
                    selectedDays,
                    binding.etDescription.text.toString(),
                    userType,status
                )
            }
        }
    }
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (parent!!.id) {
            binding.spinnerDeliveryTime.id -> {
                selectedDays = parent.selectedItem.toString()
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    private fun initComponent() {
        daysArrayList = ArrayList()
    }

}