package com.horizam.pro.elean.ui.main.view.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.horizam.pro.elean.App
import com.horizam.pro.elean.Constants
import com.horizam.pro.elean.R
import com.horizam.pro.elean.data.api.ApiHelper
import com.horizam.pro.elean.data.api.RetrofitBuilder
import com.horizam.pro.elean.data.model.response.PrivacyPolicyResponse
import com.horizam.pro.elean.databinding.FragmentVisitorTermsConditionsBinding
import com.horizam.pro.elean.ui.base.ViewModelFactory
import com.horizam.pro.elean.ui.main.callbacks.GenericHandler
import com.horizam.pro.elean.ui.main.viewmodel.SettingsViewModel
import com.horizam.pro.elean.utils.Status


class VisitorTermsConditionsFragment : Fragment() {

    private lateinit var binding: FragmentVisitorTermsConditionsBinding
    private lateinit var viewModel: SettingsViewModel
    private lateinit var genericHandler: GenericHandler
    private lateinit var privacyPolicyResponse: PrivacyPolicyResponse

    override fun onAttach(context: Context) {
        super.onAttach(context)
        genericHandler = context as GenericHandler
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentVisitorTermsConditionsBinding.inflate(layoutInflater, container, false)
        setToolbarData()
        setupViewModel()
        setupObservers()
        setClickListeners()
        return binding.root
    }

    private fun setClickListeners() {
        binding.tvTerms.setOnClickListener {
            val openURL = Intent(Intent.ACTION_VIEW)
            openURL.data = Uri.parse("https://dex.proelean.com/general/en/terms-conditions")
            startActivity(openURL)

        }
        binding.tvPrivacy.setOnClickListener {
            val openURL = Intent(Intent.ACTION_VIEW)
            openURL.data = Uri.parse("https://dex.proelean.com/general/en/privacy-policy")
            startActivity(openURL)
        }
        binding.toolbar.ivToolbar.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setToolbarData() {
        binding.toolbar.ivToolbar.setImageResource(R.drawable.ic_back)
        binding.toolbar.ivToolbar.isVisible=true
        binding.toolbar.tvToolbar.text = getString(R.string.str_settings)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(SettingsViewModel::class.java)
    }

    private fun setupObservers() {
        viewModel.policyTerms.observe(viewLifecycleOwner, {
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

    private fun handleResponse(response: PrivacyPolicyResponse) {
        //genericHandler.showMessage(response.message)
        if (response.status == Constants.STATUS_OK){
            privacyPolicyResponse = response
        }
    }

}