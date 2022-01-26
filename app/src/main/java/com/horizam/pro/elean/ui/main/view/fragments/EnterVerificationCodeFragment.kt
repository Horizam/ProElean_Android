package com.horizam.pro.elean.ui.main.view.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.horizam.pro.elean.R
import com.horizam.pro.elean.data.api.ApiHelper
import com.horizam.pro.elean.data.api.RetrofitBuilder
import com.horizam.pro.elean.data.model.requests.ForgetChangePasswordRequest
import com.horizam.pro.elean.data.model.response.GeneralResponse
import com.horizam.pro.elean.databinding.FragmentEnterVerificationCodeBinding
import com.horizam.pro.elean.ui.base.ViewModelFactory
import com.horizam.pro.elean.ui.main.callbacks.GenericHandler
import com.horizam.pro.elean.ui.main.viewmodel.ForgotPasswordViewModel
import com.horizam.pro.elean.utils.Status
import com.horizam.pro.elean.utils.Validator
import kotlinx.android.synthetic.main.fragment_enter_verification_code.*


class EnterVerificationCodeFragment : Fragment() {
    private lateinit var binding: FragmentEnterVerificationCodeBinding
    private lateinit var genericHandler: GenericHandler
    private lateinit var viewModel: ForgotPasswordViewModel
    val args: EnterVerificationCodeFragmentArgs by navArgs()


    override fun onAttach(context: Context) {
        super.onAttach(context)
        genericHandler = context as GenericHandler
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEnterVerificationCodeBinding.inflate(layoutInflater, container, false)

        setupViewModel()
        initComponent()
        setClickListener()
        setUpObserver()

        return binding.root
    }

    private fun setUpObserver() {
        viewModel.changePassword.observe(viewLifecycleOwner, {
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
        this.findNavController().popBackStack()
        this.findNavController().popBackStack()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(ForgotPasswordViewModel::class.java)
    }

    private fun setClickListener() {
        binding.btnVerifyCode.setOnClickListener {
            validateInputs()
        }
        binding.ivBack.setOnClickListener {
            this.findNavController().popBackStack()
        }
    }

    private fun validateInputs() {
        if (Validator.validateVerificationCode(et_verification_code)) {

        } else if (!Validator.isValidPassword(binding.etNewPassword)) {
            return
        } else if (!Validator.newPasswordConfirmNewPasswordValidation(
                binding.etNewPassword,
                binding.etConfirmNewPassword
            )
        ) {
            return
        } else {
            val forgetChangePasswordRequest = ForgetChangePasswordRequest(
                email = args.email,
                token = binding.etVerificationCode.text.toString(),
                password = binding.etNewPassword.text.toString(),
                password_confirmation = binding.etConfirmNewPassword.text.toString()
            )
            exeApi(forgetChangePasswordRequest)
        }
    }

    private fun exeApi(forgetChangePasswordRequest: ForgetChangePasswordRequest) {
        viewModel.forgetChangePasswordVerificationCodeCall(forgetChangePasswordRequest)
    }

    private fun initComponent() {
    }
}