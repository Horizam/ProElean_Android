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
import com.horizam.pro.elean.App
import com.horizam.pro.elean.R
import com.horizam.pro.elean.data.api.ApiHelper
import com.horizam.pro.elean.data.api.RetrofitBuilder
import com.horizam.pro.elean.data.model.requests.BankDetail
import com.horizam.pro.elean.data.model.requests.BankDetailResponse
import com.horizam.pro.elean.databinding.FragmentBankDetailsBinding
import com.horizam.pro.elean.ui.base.ViewModelFactory
import com.horizam.pro.elean.ui.main.callbacks.GenericHandler
import com.horizam.pro.elean.ui.main.viewmodel.BankDetailViewModel
import com.horizam.pro.elean.utils.BaseUtils.Companion.hideKeyboard
import com.horizam.pro.elean.utils.Status


class BankDetailsFragment : Fragment() {

    private lateinit var binding: FragmentBankDetailsBinding
    private lateinit var viewModel: BankDetailViewModel
    private lateinit var genericHandler: GenericHandler

    override fun onAttach(context: Context) {
        super.onAttach(context)
        genericHandler = context as GenericHandler
    }

    private val navController: NavController by lazy {
        this.findNavController()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBankDetailsBinding.inflate(layoutInflater, container, false)
        setToolbarData()
        setupViewModel()
        setUpObserver()
        setOnClickListeners()
        return binding.root
    }


    private fun setUpObserver() {
        viewModel.addAccountDetail.observe(viewLifecycleOwner, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        genericHandler.showProgressBar(false)
                        genericHandler.showSuccessMessage(it.data!!.message.toString())
                        this.findNavController().popBackStack()
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

        viewModel.getAccountDetail.observe(viewLifecycleOwner, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        genericHandler.showProgressBar(false)
                        handleResponse(it.data!!)
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

    private fun handleResponse(bankDetailResponse: BankDetailResponse) {
        val bankDetail = bankDetailResponse.data
        binding.etBankName.setText(bankDetail.bank_name)
        binding.etAccountTitle.setText(bankDetail.bank_title)
        binding.etIban.setText(bankDetail.iban)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(BankDetailViewModel::class.java)
    }

    private fun setOnClickListeners() {
        binding.toolbar.ivToolbar.setOnClickListener {
            navController.popBackStack()
        }
        binding.btnSubmit.setOnClickListener {
            val bankName = binding.etBankName.text.toString()
            val accountTitle = binding.etAccountTitle.text.toString()
            val iban = binding.etIban.text.toString()
            removeAllEditTextError()
            if (bankName.isEmpty()) {
                binding.textFieldBankName.error = "bank name is empty"
            } else if (accountTitle.isEmpty()) {
                binding.textFieldAccountTitle.error = "account title is empty"
            } else if (iban.isEmpty()) {
                binding.textFieldIban.error = "iban is empty"
            } else {
                hideKeyboard()
                exeApi(BankDetail(bank_name = bankName, bank_title = accountTitle, iban = iban))
            }
        }
    }

    private fun removeAllEditTextError() {
        binding.textFieldBankName.error = null
        binding.textFieldAccountTitle.error = null
        binding.textFieldIban.error = null
    }

    private fun exeApi(bankDetail: BankDetail) {
        viewModel.addAccountDetailRequest(bankDetail)
    }

    private fun setToolbarData() {
        binding.toolbar.ivToolbar.setImageResource(R.drawable.ic_back)
        binding.toolbar.tvToolbar.text = App.getAppContext()!!.getString(R.string.str_bank_details)
    }
}