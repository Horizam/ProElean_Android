package com.horizam.pro.elean.ui.main.view.fragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.horizam.pro.elean.App
import com.horizam.pro.elean.Constants
import com.horizam.pro.elean.R
import com.horizam.pro.elean.data.api.ApiHelper
import com.horizam.pro.elean.data.api.RetrofitBuilder
import com.horizam.pro.elean.data.model.SliderItem
import com.horizam.pro.elean.data.model.response.GeneralResponse
import com.horizam.pro.elean.data.model.response.ManageServicesResponse
import com.horizam.pro.elean.data.model.response.User_services
import com.horizam.pro.elean.databinding.DialogDeleteBinding
import com.horizam.pro.elean.databinding.DialogFilterPostedJobsBinding
import com.horizam.pro.elean.databinding.FragmentManageServicesBinding
import com.horizam.pro.elean.ui.base.ViewModelFactory
import com.horizam.pro.elean.ui.main.adapter.ManageServicesAdapter
import com.horizam.pro.elean.ui.main.callbacks.GenericHandler
import com.horizam.pro.elean.ui.main.callbacks.ManageServiceHandler
import com.horizam.pro.elean.ui.main.viewmodel.ManageServicesViewModel
import com.horizam.pro.elean.utils.Resource
import com.horizam.pro.elean.utils.Status
import java.lang.Exception


class ManageServicesFragment : Fragment(), ManageServiceHandler {

    private lateinit var binding: FragmentManageServicesBinding
    private lateinit var adapter: ManageServicesAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: ManageServicesViewModel
    private lateinit var genericHandler: GenericHandler
    private lateinit var dialogFilterJobs: Dialog
    private lateinit var dialogDelete: Dialog
    private lateinit var bindingDialog: DialogFilterPostedJobsBinding
    private lateinit var bindingDeleteDialog: DialogDeleteBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        genericHandler = context as GenericHandler
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViewModel()
        viewModel.deleteUserService.observe(this, deleteUserServiceObserver)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentManageServicesBinding.inflate(layoutInflater, container, false)
        setToolbarData()
        initViews()
        //setupViewModel()
        setupObservers()
        setRecyclerView()
        setOnClickListeners()
        exeApi()
        return binding.root
    }

    private fun exeApi(status: String = "") {
        if (viewModel.userServices.value == null) {
            genericHandler.showProgressBar(true)
            viewModel.userServicesCall(status)
        }
    }

    private fun initViews() {
        adapter = ManageServicesAdapter(this)
        recyclerView = binding.rvManageServices
        initFilterDialog()
        initDeleteDialog()
    }

    private fun initFilterDialog() {
        dialogFilterJobs = Dialog(requireContext())
        bindingDialog = DialogFilterPostedJobsBinding.inflate(layoutInflater)
        dialogFilterJobs.setContentView(bindingDialog.root)
    }

    private fun initDeleteDialog() {
        dialogDelete = Dialog(requireContext())
        bindingDeleteDialog = DialogDeleteBinding.inflate(layoutInflater)
        dialogDelete.setContentView(bindingDeleteDialog.root)
    }

    private fun setRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    private fun setOnClickListeners() {
        binding.apply {
            toolbar.ivToolbar.setOnClickListener {
                findNavController().popBackStack()
            }
            btnRetry.setOnClickListener {
                viewModel.userServicesCall("")
            }
            toolbar.ivSecond.setOnClickListener {
                dialogFilterJobs.show()
            }
        }
        bindingDialog.rgPostJobFilter.setOnCheckedChangeListener(filterServices)
    }

    private val filterServices = RadioGroup.OnCheckedChangeListener { radioGroup, checkedId ->
        dialogFilterJobs.dismiss()
        val radioButton = radioGroup.findViewById<RadioButton>(checkedId)
        viewModel.userServicesCall(radioButton.text.toString())
    }

    private fun setToolbarData() {
        binding.toolbar.ivToolbar.setImageResource(R.drawable.ic_back)
        binding.toolbar.ivSecond.isVisible = true
        binding.toolbar.tvToolbar.text =
            App.getAppContext()!!.getString(R.string.str_manage_services)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(ManageServicesViewModel::class.java)
    }

    private fun setupObservers() {
        viewModel.userServices.observe(viewLifecycleOwner, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        genericHandler.showProgressBar(false)
                        resource.data?.let { response ->
                            handleResponse(response)
                            changeViewVisibility(textView = false, button = false, layout = true)
                        }
                    }
                    Status.ERROR -> {
                        genericHandler.showProgressBar(false)
                        genericHandler.showMessage(it.message.toString())
                        changeViewVisibility(textView = true, button = true, layout = false)
                    }
                    Status.LOADING -> {
                        genericHandler.showProgressBar(true)
                        changeViewVisibility(textView = false, button = false, layout = false)
                    }
                }
            }
        })
    }

    private val deleteUserServiceObserver = Observer<Resource<GeneralResponse>> {
        it?.let { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    genericHandler.showProgressBar(false)
                    resource.data?.let { response ->
                        handleDeleteResponse(response)
                    }
                }
                Status.ERROR -> {
                    genericHandler.showProgressBar(false)
                    genericHandler.showMessage(it.message.toString())
                }
                Status.LOADING -> {
                    genericHandler.showProgressBar(true)
                }
            }
        }
    }

    private fun handleDeleteResponse(response: GeneralResponse) {
        genericHandler.showMessage(response.message)
        if (response.status == Constants.STATUS_OK) {
            viewModel.userServicesCall("")
        }
    }

    private fun changeViewVisibility(textView: Boolean, button: Boolean, layout: Boolean) {
        binding.textViewError.isVisible = textView
        binding.btnRetry.isVisible = button
        binding.rvManageServices.isVisible = layout
    }

    private fun handleResponse(response: ManageServicesResponse) {
        try {
            setUIData(response.service)
        } catch (e: Exception) {
            genericHandler.showMessage(e.message.toString())
        }
    }

    private fun setUIData(serviceList: List<User_services>) {
        adapter.submitList(serviceList)
        binding.tvPlaceholder.isVisible = serviceList.isEmpty()
    }

    override fun <T> removeService(item: T) {
        if (item is User_services) {
            dialogDelete.show()
            bindingDeleteDialog.btnYes.setOnClickListener {
                dialogDelete.dismiss()
                genericHandler.showProgressBar(true)
                viewModel.deleteUserServiceCall(item.id)
            }
            bindingDeleteDialog.btnNo.setOnClickListener { dialogDelete.dismiss() }
        }
    }

    override fun <T> onItemClick(item: T) {
        if (item is User_services) {
            val uid = item.uuid
            ManageServicesFragmentDirections.actionManageServicesFragmentToServiceDetailsFragment(
                uid = uid,
                isEditable = true
            ).also {
                findNavController().navigate(it)
            }
        }
    }

}