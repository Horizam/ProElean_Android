package com.horizam.pro.elean.ui.main.view.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.horizam.pro.elean.App
import com.horizam.pro.elean.R
import com.horizam.pro.elean.data.api.ApiHelper
import com.horizam.pro.elean.data.api.RetrofitBuilder
import com.horizam.pro.elean.data.model.requests.SubmitQueryRequest
import com.horizam.pro.elean.data.model.response.GeneralResponse
import com.horizam.pro.elean.databinding.FragmentSupportBinding
import com.horizam.pro.elean.ui.base.ViewModelFactory
import com.horizam.pro.elean.ui.main.callbacks.GenericHandler
import com.horizam.pro.elean.ui.main.viewmodel.SupportViewModel
import com.horizam.pro.elean.utils.BaseUtils.Companion.hideKeyboard
import com.horizam.pro.elean.utils.Status


class SupportFragment : Fragment() {

    private lateinit var binding: FragmentSupportBinding
    private lateinit var viewModel: SupportViewModel
    private lateinit var genericHandler: GenericHandler

    override fun onAttach(context: Context) {
        super.onAttach(context)
        genericHandler = context as GenericHandler
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSupportBinding.inflate(layoutInflater, container, false)
        setToolbarData()
        setupViewModel()
        setupObservers()
        setClickListeners()
        return binding.root
    }

    private fun setClickListeners() {
        binding.apply {
            toolbar.ivToolbar.setOnClickListener {
                findNavController().popBackStack()
            }
            btnSubmit.setOnClickListener {
                etSubject.clearFocus()
                etDescription.clearFocus()
                hideKeyboard()
                validateData()
            }
        }
    }

    private fun validateData() {
        removeAllTextFieldErrors()
        when {
            binding.etSubject.text.toString().trim().isEmpty() -> {
                binding.textFieldSubject.error = getString(R.string.str_enter_valid_subject)
                return
            }
            binding.etSubject.text.toString().trim().length < 4 -> {
                binding.textFieldSubject.error = getString(R.string.str_subject_minimum_character)
                return
            }
            binding.etDescription.text.toString().trim().isEmpty() -> {
                binding.textFieldDescription.error = getString(R.string.str_enter_valid_description)
                return
            }
            binding.etDescription.text.toString().trim().length < 15 -> {
                binding.textFieldDescription.error = getString(R.string.str_description_minimum)
                return
            }
            else -> {
                executeApi()
            }
        }
    }

    private fun removeAllTextFieldErrors() {
        binding.apply {
            textFieldDescription.error = null
            textFieldSubject.error = null
        }
    }

    private fun executeApi() {
        genericHandler.showProgressBar(true)
        val request = SubmitQueryRequest(
            subject = binding.etSubject.text.toString().trim(),
            description = binding.etDescription.text.toString().trim()
        )
        viewModel.submitQueryCall(request)
    }

    private fun setToolbarData() {
        binding.toolbar.ivToolbar.setImageResource(R.drawable.ic_back)
        binding.toolbar.ivToolbar.isVisible=true
        binding.toolbar.tvToolbar.text = App.getAppContext()!!.getString(R.string.str_support)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(SupportViewModel::class.java)
    }

    private fun setupObservers() {
        viewModel.submitQuery.observe(viewLifecycleOwner, {
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
        findNavController().popBackStack()
    }

}