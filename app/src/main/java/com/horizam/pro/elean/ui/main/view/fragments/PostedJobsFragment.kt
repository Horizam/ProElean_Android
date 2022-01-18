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
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.horizam.pro.elean.App
import com.horizam.pro.elean.R
import com.horizam.pro.elean.data.api.ApiHelper
import com.horizam.pro.elean.data.api.RetrofitBuilder
import com.horizam.pro.elean.data.model.response.GeneralResponse
import com.horizam.pro.elean.data.model.response.PostedJob
import com.horizam.pro.elean.databinding.DialogDeleteBinding
import com.horizam.pro.elean.databinding.DialogFilterPostedJobsBinding
import com.horizam.pro.elean.databinding.FragmentPostedJobsBinding
import com.horizam.pro.elean.ui.base.ViewModelFactory
import com.horizam.pro.elean.ui.main.adapter.MyLoadStateAdapter
import com.horizam.pro.elean.ui.main.adapter.PostedJobsAdapter
import com.horizam.pro.elean.ui.main.callbacks.GenericHandler
import com.horizam.pro.elean.ui.main.callbacks.OnItemClickListener
import com.horizam.pro.elean.ui.main.callbacks.PostedJobsHandler
import com.horizam.pro.elean.ui.main.viewmodel.PostedJobsViewModel
import com.horizam.pro.elean.utils.Resource
import com.horizam.pro.elean.utils.Status


class PostedJobsFragment : Fragment(), OnItemClickListener, PostedJobsHandler,
    SwipeRefreshLayout.OnRefreshListener {

    private lateinit var binding: FragmentPostedJobsBinding
    private lateinit var adapterApproved: PostedJobsAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: PostedJobsViewModel
    private lateinit var genericHandler: GenericHandler
    private lateinit var dialogFilterJobs: Dialog
    private lateinit var dialogDelete: Dialog
    private lateinit var bindingDialog: DialogFilterPostedJobsBinding
    private lateinit var bindingDeleteDialog: DialogDeleteBinding
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onAttach(context: Context) {
        super.onAttach(context)
        genericHandler = context as GenericHandler
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostedJobsBinding.inflate(layoutInflater, container, false)
        setToolbarData()
        initViews()
        setupViewModel()
        setupObservers()
        setRecyclerView()
        setOnClickListeners()
        return binding.root
    }

    private fun exeApi(status: String = "") {
        genericHandler.showProgressBar(true)
        viewModel.getPostedJobsCall(status)
    }

    private fun initViews() {
        adapterApproved = PostedJobsAdapter(this, this)
        recyclerView = binding.rvJobs
        swipeRefreshLayout = binding.swipeRefresh
        swipeRefreshLayout.setOnRefreshListener(this)
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
        recyclerView.let {
            it.setHasFixedSize(true)
            it.layoutManager = LinearLayoutManager(requireContext())
            it.adapter = adapterApproved.withLoadStateHeaderAndFooter(
                header = MyLoadStateAdapter { adapterApproved.retry() },
                footer = MyLoadStateAdapter { adapterApproved.retry() }
            )
        }
        setAdapterLoadState(adapterApproved)
    }

    private fun setAdapterLoadState(adapter: PostedJobsAdapter) {
        adapter.addLoadStateListener { loadState ->
            binding.apply {
                genericHandler.showProgressBar(loadState.source.refresh is LoadState.Loading)
                recyclerView.isVisible = loadState.source.refresh is LoadState.NotLoading
                btnRetry.isVisible = loadState.source.refresh is LoadState.Error
                textViewError.isVisible = loadState.source.refresh is LoadState.Error
                // no results
                if (loadState.source.refresh is LoadState.NotLoading &&
                    loadState.append.endOfPaginationReached &&
                    adapter.itemCount < 1
                ) {
                    recyclerView.isVisible = false
                    tvPlaceholder.isVisible = true
                } else {
                    tvPlaceholder.isVisible = false
                }
            }
        }
    }

    private fun setOnClickListeners() {
        binding.apply {
            toolbar.ivToolbar.setOnClickListener {
                findNavController().popBackStack()
            }
            floatingActionButton.setOnClickListener {
                findNavController().popBackStack()
                findNavController().navigate(R.id.postJobFragment)
            }
            btnRetry.setOnClickListener {
                adapterApproved.retry()
            }
            toolbar.ivSecond.setOnClickListener {
                dialogFilterJobs.show()
            }
        }
        bindingDialog.rgPostJobFilter.setOnCheckedChangeListener(filterJobs)
    }

    private fun setToolbarData() {
        binding.toolbar.ivToolbar.setImageResource(R.drawable.ic_back)
        binding.toolbar.ivSecond.visibility = View.VISIBLE
        binding.toolbar.tvToolbar.text = App.getAppContext()!!.getString(R.string.str_jobs)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(PostedJobsViewModel::class.java)
    }

    private fun setupObservers() {
        viewModel.postedJobs.observe(viewLifecycleOwner) {
            adapterApproved.submitData(viewLifecycleOwner.lifecycle, it)
        }
        viewModel.deletePostedJob.observe(viewLifecycleOwner, deleteJobObserver)
    }

    private val deleteJobObserver = Observer<Resource<GeneralResponse>> {
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
    }

    private fun handleResponse(response: GeneralResponse) {
        genericHandler.showErrorMessage(response.message)
        exeApi()
    }

    private val filterJobs = RadioGroup.OnCheckedChangeListener { radioGroup, checkedId ->
        dialogFilterJobs.dismiss()
        val radioButton = radioGroup.findViewById<RadioButton>(checkedId)
        exeApi(status = radioButton.text.toString())
    }

    override fun <T> onItemClick(item: T) {
        if (item is PostedJob) {
            // add feature
        }
    }

    override fun <T> deleteItem(item: T) {
        if (item is PostedJob) {
            dialogDelete.show()
            bindingDeleteDialog.btnYes.setOnClickListener {
                dialogDelete.dismiss()
                genericHandler.showProgressBar(true)
                viewModel.deletePostedJobCall(item.id)
            }
            bindingDeleteDialog.btnNo.setOnClickListener { dialogDelete.dismiss() }
        }
    }

    override fun <T> viewOffers(item: T) {
        if (item is PostedJob) {
            if (item.total_offers > 0){
                val action = PostedJobsFragmentDirections.actionPostedJobsFragmentToViewOffersFragment(item.id)
                findNavController().navigate(action)
            }
        }
    }

    override fun onRefresh() {
        if (swipeRefreshLayout.isRefreshing) {
            swipeRefreshLayout.isRefreshing = false
        }
        exeApi()
    }
}