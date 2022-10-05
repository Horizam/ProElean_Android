package com.horizam.pro.elean.ui.main.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.horizam.pro.elean.App
import com.horizam.pro.elean.R
import com.horizam.pro.elean.databinding.FragmentExtendDeliveryTimeBottomSheetBinding
import com.horizam.pro.elean.utils.PrefManager

class ExtendDeliveryTimeBottomSheetFragment(var orderDetailsFragment: OrderDetailsFragment,var userType: Int ,var status: Int) :
    BottomSheetDialogFragment(),
    AdapterView.OnItemSelectedListener {
    private lateinit var binding: FragmentExtendDeliveryTimeBottomSheetBinding
    private lateinit var daysAdapter: ArrayAdapter<String>
    private lateinit var daysArrayList: ArrayList<String>
    var selectedDays = ""

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

    private fun initComponent() {
        daysArrayList = ArrayList()
        binding.spinnerDeliveryTime.onItemSelectedListener = this
    }

    private fun setNoOfDaysSpinner() {
        var manager: PrefManager = PrefManager(App.getAppContext()!!)
        for (pos in 0 until 59) {
            if (manager.setLanguage == "0") {
                daysArrayList.add("${pos}day")
            } else {
                daysArrayList.add("${pos}päivä")
            }
        }
        val selectedDaysPosition: Int = daysArrayList.indexOfFirst {
            it == selectedDays
        }
            daysAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item, daysArrayList
            ).also {
                it.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                binding.spinnerDeliveryTime.adapter = it
                binding.spinnerDeliveryTime.setSelection(selectedDaysPosition)
            }
        }
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (parent!!.id) {
            binding.spinnerDeliveryTime.id -> {
                if (position == 0) {
                } else {
                    selectedDays = parent.selectedItemId.toString()
                }
            }
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
                    userType, status
                )
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }
}