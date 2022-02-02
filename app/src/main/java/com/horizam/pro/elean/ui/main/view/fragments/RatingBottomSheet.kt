package com.horizam.pro.elean.ui.main.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.horizam.pro.elean.R
import com.horizam.pro.elean.databinding.FragmentRatingBottomSheetBinding
import com.horizam.pro.elean.ui.main.callbacks.GenericHandler
import com.horizam.pro.elean.ui.main.callbacks.RatingHandler


class RatingBottomSheet(var ratingHandler: RatingHandler , var genericHandler: GenericHandler) : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentRatingBottomSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRatingBottomSheetBinding.inflate(layoutInflater, container, false)

        setOnClickListener()

        return binding.root
    }

    private fun setOnClickListener() {
        binding.btnSubmit.setOnClickListener{
            val description = binding.etDescription.text.toString()
            val rating = binding.ratingBar.rating
            if(description.length < 10){
                binding.etDescription.error = getString(R.string.str_description_is_too_short)
            }else if(rating <= 0.0){
                genericHandler.showErrorMessage("please rate your experience")
            }else{
                dismiss()
                ratingHandler.getRatingData(rating , description)
            }
        }
    }
}