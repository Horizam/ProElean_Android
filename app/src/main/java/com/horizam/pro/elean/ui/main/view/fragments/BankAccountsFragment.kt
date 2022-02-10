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
import com.horizam.pro.elean.data.model.requests.BankDetailResponse
import com.horizam.pro.elean.databinding.FragmentBankAccountsBinding
import com.horizam.pro.elean.databinding.FragmentSettingsBinding
import com.horizam.pro.elean.ui.base.ViewModelFactory
import com.horizam.pro.elean.ui.main.callbacks.GenericHandler
import com.horizam.pro.elean.ui.main.viewmodel.BankDetailViewModel
import com.horizam.pro.elean.utils.Status

class BankAccountsFragment : Fragment() {

    private lateinit var binding: FragmentBankAccountsBinding
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
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBankAccountsBinding.inflate(layoutInflater, container, false)

        setToolbarData()
        setupViewModel()
        setClickListener()
        setUpObserver()
        return binding.root

    }

    override fun onResume() {
        super.onResume()
        viewModel.getBankAccountDetail()
    }

    private fun setClickListener() {
        binding.toolbar.ivToolbar.setOnClickListener {
            navController.popBackStack()
        }
        binding.ivEdit.setOnClickListener {
            navController.navigate(R.id.bankDetailsFragment)
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(BankDetailViewModel::class.java)
    }

    private fun setUpObserver() {
        viewModel.getAccountDetail.observe(viewLifecycleOwner, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        genericHandler.showProgressBar(false)
                        handleResponse(it.data!!)
                    }
                    Status.ERROR -> {
                        genericHandler.showProgressBar(false)
//                        genericHandler.showErrorMessage(it.message.toString())
                    }
                    Status.LOADING -> {
                        genericHandler.showProgressBar(true)
                    }
                }
            }
        })
    }

    private fun handleResponse(data: BankDetailResponse) {
        val bankDetail = data.data
        binding.tvBankName.setText(bankDetail.bank_name)
        binding.tvAccountNameValue.setText(bankDetail.bank_title)
        binding.tvAccountNumberValue.setText(bankDetail.iban)
    }

    private fun setToolbarData() {
        binding.toolbar.ivToolbar.setImageResource(R.drawable.ic_back)
        binding.toolbar.tvToolbar.text = App.getAppContext()!!.getString(R.string.str_bank_details)
    }
}