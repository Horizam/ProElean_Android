package com.horizam.pro.elean.ui.main.view.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.horizam.pro.elean.R
import com.horizam.pro.elean.data.api.ApiHelper
import com.horizam.pro.elean.data.api.RetrofitBuilder
import com.horizam.pro.elean.data.model.requests.ChangePasswordRequest
import com.horizam.pro.elean.data.model.response.GeneralResponse
import com.horizam.pro.elean.databinding.FragmentChangePasswordBinding
import com.horizam.pro.elean.ui.base.ViewModelFactory
import com.horizam.pro.elean.ui.main.callbacks.GenericHandler
import com.horizam.pro.elean.ui.main.viewmodel.SettingsViewModel
import com.horizam.pro.elean.utils.BaseUtils.Companion.hideKeyboard
import com.horizam.pro.elean.utils.Status


class ChangePasswordFragment : Fragment() {
    private lateinit var binding: FragmentChangePasswordBinding
    private lateinit var navController: NavController
    private lateinit var genericHandler: GenericHandler
    private lateinit var viewModel: SettingsViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        genericHandler = context as GenericHandler
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentChangePasswordBinding.inflate(layoutInflater, container, false)

        initComponent()
        setOnClickListener()
        setupViewModel()
        setupObservers()

        return binding.root
    }

    private fun setupObservers() {
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
        hideKeyboard()
        genericHandler.showErrorMessage(response.message)
        navController.popBackStack()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(SettingsViewModel::class.java)
    }

    private fun setOnClickListener() {
        binding.btnChangePassword.setOnClickListener {

            checkValidation(
                binding.etCurrentPassword.text.toString(),
                binding.etNewPassword.text.toString(),
                binding.etConfirmNewPassword.text.toString()
            )
        }
    }

    private fun checkValidation(currentPassword: String, newPassword: String, confirmNewPassword: String) {
        if(newPassword != confirmNewPassword){
            genericHandler.showErrorMessage("New Password and Confirm New Password Mismatched")
        }else{
            val changePasswordRequest = ChangePasswordRequest(currentPassword , newPassword , confirmNewPassword)
            exeChangePasswordApi(changePasswordRequest)
        }
    }

    private fun exeChangePasswordApi(changePasswordRequest: ChangePasswordRequest) {
        viewModel.changePasswordCall(changePasswordRequest)
    }

    private fun initComponent() {
        navController = this.findNavController()
        binding.toolbar.ivToolbar.visibility = View.GONE
        binding.toolbar.tvToolbar.text = getString(R.string.str_change_password)
    }
}