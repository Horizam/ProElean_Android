package com.horizam.pro.elean.ui.main.view.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.horizam.pro.elean.R
import com.horizam.pro.elean.data.api.ApiHelper
import com.horizam.pro.elean.data.api.RetrofitBuilder
import com.horizam.pro.elean.data.model.requests.ForgotPasswordRequest
import com.horizam.pro.elean.data.model.response.GeneralResponse
import com.horizam.pro.elean.databinding.FragmentResetPasswordBinding
import com.horizam.pro.elean.ui.base.ViewModelFactory
import com.horizam.pro.elean.ui.main.callbacks.GenericHandler
import com.horizam.pro.elean.ui.main.viewmodel.ForgotPasswordViewModel
import com.horizam.pro.elean.utils.BaseUtils.Companion.hideKeyboard
import com.horizam.pro.elean.utils.Status
import com.horizam.pro.elean.utils.Validator


class ResetPasswordFragment : Fragment() {

    private lateinit var binding: FragmentResetPasswordBinding
    private lateinit var viewModel: ForgotPasswordViewModel
    private lateinit var genericHandler: GenericHandler

    override fun onAttach(context: Context) {
        super.onAttach(context)
        genericHandler = context as GenericHandler
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentResetPasswordBinding.inflate(layoutInflater, container, false)
        setupViewModel()
        setupObservers()
        setClickListeners()
        return binding.root
    }

    private fun setClickListeners() {
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.tvLogin.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnResetPassword.setOnClickListener {
            hideKeyboard()
            validateData()
        }
    }

    private fun validateData() {
        if (!Validator.isValidEmail(binding.etEmail)) {
            return
        } else {
            executeApi()
        }
    }

    private fun executeApi() {
        genericHandler.showProgressBar(true)
        val request = ForgotPasswordRequest(
            email = binding.etEmail.text.toString().trim()
        )
        viewModel.forgotPasswordCall(request)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(ForgotPasswordViewModel::class.java)
    }

    private fun setupObservers() {
        viewModel.forgotPassword.observe(viewLifecycleOwner,  {
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

    private fun handleResponse(response: GeneralResponse) {
        genericHandler.showSuccessMessage(response.message)
        val action =
            ResetPasswordFragmentDirections.actionResetPasswordFragmentToEnterVerificationCodeFragment(
                binding.etEmail.text.toString()
            )
        findNavController().navigate(action)
    }
}