package com.horizam.pro.elean.ui.main.view.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.horizam.pro.elean.R
import com.horizam.pro.elean.databinding.FragmentEnterVerificationCodeBinding
import com.horizam.pro.elean.databinding.FragmentForgetEnterNewPasswordBinding
import com.horizam.pro.elean.ui.main.callbacks.GenericHandler

class ForgetEnterNewPassword : Fragment() {
    private lateinit var binding: FragmentForgetEnterNewPasswordBinding
    private lateinit var genericHandler: GenericHandler

    override fun onAttach(context: Context) {
        super.onAttach(context)
        genericHandler = context as GenericHandler
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentForgetEnterNewPasswordBinding.inflate(layoutInflater, container, false)

        initComponent()
        setClickListener()

        return binding.root
    }

    private fun setClickListener() {
        binding.btnChangePassword.setOnClickListener {
            if (binding.etEnterNewPassword.text.toString().length < 6) {
                genericHandler.showErrorMessage(getString(R.string.str_password_contain))
            } else if (binding.etEnterNewPassword.text.toString() != binding.etEnterNewPassword.text.toString()) {
                genericHandler.showErrorMessage(getString(R.string.str_password_not_matched))
            } else {

            }
        }
    }

    private fun initComponent() {
    }
}