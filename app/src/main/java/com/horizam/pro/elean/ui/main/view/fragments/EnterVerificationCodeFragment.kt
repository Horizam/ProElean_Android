package com.horizam.pro.elean.ui.main.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.horizam.pro.elean.R
import com.horizam.pro.elean.databinding.FragmentEnterVerificationCodeBinding
import com.horizam.pro.elean.databinding.FragmentResetPasswordBinding


class EnterVerificationCodeFragment : Fragment() {
    private lateinit var binding: FragmentEnterVerificationCodeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEnterVerificationCodeBinding.inflate(layoutInflater, container, false)

        initComponent()
        setClickListener()

        return binding.root
    }

    private fun setClickListener() {
        binding.btnVerifyCode.setOnClickListener {
            this.findNavController().navigate(R.id.forgetEnterNewPassword)
        }
    }

    private fun initComponent() {

    }
}