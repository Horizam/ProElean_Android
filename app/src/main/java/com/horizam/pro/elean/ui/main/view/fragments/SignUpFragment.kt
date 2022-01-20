package com.horizam.pro.elean.ui.main.view.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.horizam.pro.elean.R
import com.horizam.pro.elean.data.api.ApiHelper
import com.horizam.pro.elean.data.api.RetrofitBuilder
import com.horizam.pro.elean.data.model.requests.RegisterRequest
import com.horizam.pro.elean.data.model.response.RegisterResponse
import com.horizam.pro.elean.databinding.FragmentSignUpBinding
import com.horizam.pro.elean.ui.base.ViewModelFactory
import com.horizam.pro.elean.ui.main.callbacks.GenericHandler
import com.horizam.pro.elean.ui.main.viewmodel.RegisterViewModel
import com.horizam.pro.elean.utils.BaseUtils
import com.horizam.pro.elean.utils.BaseUtils.Companion.hideKeyboard
import com.horizam.pro.elean.utils.Status
import com.horizam.pro.elean.utils.Validator


class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignUpBinding
    private lateinit var viewModel: RegisterViewModel
    private lateinit var genericHandler: GenericHandler


    override fun onAttach(context: Context) {
        super.onAttach(context)
        genericHandler = context as GenericHandler
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(layoutInflater, container, false)
        setupViewModel()
        setupUI()
        setupObservers()
        clickListeners()
        return binding.root
    }

    private fun clickListeners() {
        binding.apply {
            btnSignUp.setOnClickListener {
                hideKeyboard()
                validateData()
            }
            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }
            tvSignIn.setOnClickListener {
                findNavController().popBackStack()
            }
            tvTermsAndConditions.setOnClickListener {
                SignUpFragmentDirections.actionSignUpFragmentToSettingsFragment().also {
                    findNavController().navigate(it)
                }
            }
        }
    }

    private fun FragmentSignUpBinding.validateData() {
        if (!Validator.isValidName(etFullName)) {
            return
        } else if (etUsername.text!!.length < 5) {
            genericHandler.showErrorMessage("username must be al least 5 chracter")
            etUsername.error = "username must be al least 5 chracter"
            return
        } else if (Validator.validateUserName(etUsername.text.toString())) {
            genericHandler.showErrorMessage("username must does not contain special characters and white spaces")
            etUsername.error = "username must does not contain special characters and white spaces"
            return
        } else if (!Validator.isValidEmail(etEmail)) {
            return
        } else if (!Validator.isValidPassword(etPassword)) {
            return
        } else if (etPassword.text.toString().trim() !=
            etConfirmPassword.text.toString().trim()
        ) {
            genericHandler.showErrorMessage(getString(R.string.str_password_not_matched))
            return
        } else if (!cbTermsAndConditions.isChecked) {
            genericHandler.showErrorMessage("Please accept ${getString(R.string.str_terms_and_conditions)}")
            return
        } else {
            val referralCode = etReferral.text.toString().trim()
            if (referralCode.isNotEmpty()) {
                if (BaseUtils.isNumber(referralCode)) {
                    executeApi(referralCode)
                } else {
                    genericHandler.showErrorMessage("Referral code must be numeric")
                }
            } else {
                executeApi()
            }
        }
    }

    private fun FragmentSignUpBinding.executeApi(code: String = "") {
        genericHandler.showProgressBar(true)
        val registerRequest = RegisterRequest(
            name = etFullName.text.toString().trim(),
            email = etEmail.text.toString().trim(),
            username = etUsername.text.toString().trim(),
            password = etPassword.text.toString().trim(),
            password_confirmation = etConfirmPassword.text.toString().trim()
        )
        viewModel.registerUserCall(registerRequest, code)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(RegisterViewModel::class.java)
    }

    private fun setupUI() {
//        binding.apply {
//            ccp.registerCarrierNumberEditText(etCarrierNumber)
//        }
    }

    private fun setupObservers() {
        viewModel.registerUser.observe(viewLifecycleOwner, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        genericHandler.showProgressBar(false)
                        resource.data?.let { response ->
                            handleResponse(response)
                        }
                    }
                    Status.ERROR -> {
                        genericHandler.showProgressBar(false)
                        genericHandler.showErrorMessage(it.message.toString())
                    }
                    Status.LOADING -> {
                        genericHandler.showProgressBar(true)
                    }
                }
            }
        })
    }

    private fun handleResponse(response: RegisterResponse) {
        genericHandler.showSuccessMessage(response.message)
        findNavController().popBackStack()
    }

}