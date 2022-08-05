package com.horizam.pro.elean.ui.main.view.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.horizam.pro.elean.App
import com.horizam.pro.elean.Constants
import com.horizam.pro.elean.R
import com.horizam.pro.elean.data.api.ApiHelper
import com.horizam.pro.elean.data.api.RetrofitBuilder
import com.horizam.pro.elean.data.model.response.LanguageAndCurrencyResponse
import com.horizam.pro.elean.data.model.response.PrivacyPolicyResponse
import com.horizam.pro.elean.databinding.FragmentSettingsBinding
import com.horizam.pro.elean.ui.base.ViewModelFactory
import com.horizam.pro.elean.ui.main.callbacks.GenericHandler
import com.horizam.pro.elean.ui.main.viewmodel.SettingsViewModel
import com.horizam.pro.elean.utils.Status

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private lateinit var viewModel: SettingsViewModel
    private lateinit var genericHandler: GenericHandler
    private lateinit var privacyPolicyResponse: PrivacyPolicyResponse
    private lateinit var languageAndCurrencyResponse: LanguageAndCurrencyResponse

    override fun onAttach(context: Context) {
        super.onAttach(context)
        genericHandler = context as GenericHandler
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(layoutInflater, container, false)
        setToolbarData()
        setupViewModel()
        setupObservers()
        exeApi()
        setClickListeners()
        return binding.root
    }

    private fun exeApi() {
        viewModel.getLanguageAndCurrency("data")
    }

    private fun setClickListeners() {
        binding.tvTerms.setOnClickListener {
            if (this::privacyPolicyResponse.isInitialized) {
                val termsConditions = privacyPolicyResponse.data.termConditions.description
                SettingsFragmentDirections.actionSettingsFragmentToTermsFragment(termsConditions)
                    .also { navDirections ->
                        findNavController().navigate(navDirections)
                    }
            }
        }
        binding.tvPrivacy.setOnClickListener {
            if (this::privacyPolicyResponse.isInitialized) {
                val privacyPolicy = privacyPolicyResponse.data.privacyPolicy.description
                SettingsFragmentDirections.actionSettingsFragmentToPrivacyFragment(privacyPolicy)
                    .also { navDirections ->
                        findNavController().navigate(navDirections)
                    }
            }
        }
        binding.tvEditProfile.setOnClickListener {
            findNavController().navigate(R.id.editProfileFragment)
        }
        binding.toolbar.ivToolbar.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.tvSelectCurrency.setOnClickListener {
            val currencyList = languageAndCurrencyResponse.languageAndCurrencyData.currencies
            showSelectCurrencyBottomSheet(currencyList)
        }
        binding.tvSelectLanguage.setOnClickListener {
            val languageList = languageAndCurrencyResponse.languageAndCurrencyData.languages
            showSelectLanguageBottomSheet(languageList)
        }
        binding.tvChangePassword.setOnClickListener {
            findNavController().navigate(R.id.changePasswordFragment)
        }
        binding.tvAddAccount.setOnClickListener {
//            this.findNavController().navigate(R.id.bankAccountsFragment)
            Toast.makeText(requireContext(), "You can perform this action only on Desktop", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showSelectCurrencyBottomSheet(currencyList: List<String>) {
        val selectCurrencyBottomSheet = SelectCurrencyBottomSheet(currencyList)
        selectCurrencyBottomSheet.show(
            requireActivity().supportFragmentManager,
            "SelectCurrencyBottomSheet"
        )
    }

    private fun showSelectLanguageBottomSheet(languageList: List<String>) {
        val selectLanguageBottomSheet = SelectLanguageBottomSheet(languageList)
        selectLanguageBottomSheet.show(
            requireActivity().supportFragmentManager,
            "SelectLanguageBottomSheet"
        )
    }

    private fun setToolbarData() {
        binding.toolbar.ivToolbar.setImageResource(R.drawable.ic_back)
        binding.toolbar.tvToolbar.text = App.getAppContext()!!.getString(R.string.str_settings)
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

        viewModel.languageAndCurrency.observe(viewLifecycleOwner, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        genericHandler.showProgressBar(false)
                        resource.data?.let { response ->
                            languageAndCurrencyResponse = response
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
        if (response.status == Constants.STATUS_OK) {
            privacyPolicyResponse = response
        }
    }

}