package com.horizam.pro.elean.ui.main.view.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.horizam.pro.elean.Constants
import com.horizam.pro.elean.data.api.ApiHelper
import com.horizam.pro.elean.data.api.RetrofitBuilder
import com.horizam.pro.elean.data.model.response.Subcategory
import com.horizam.pro.elean.databinding.FragmentServiceCategoriesBinding
import com.horizam.pro.elean.ui.base.ViewModelFactory
import com.horizam.pro.elean.ui.main.adapter.CategoryAdapter
import com.horizam.pro.elean.ui.main.adapter.MyLoadStateAdapter
import com.horizam.pro.elean.ui.main.callbacks.GenericHandler
import com.horizam.pro.elean.ui.main.callbacks.OnItemClickListener
import com.horizam.pro.elean.ui.main.viewmodel.ServiceCategoriesViewModel
import com.horizam.pro.elean.utils.BaseUtils.Companion.hideKeyboard


class ServiceCategoriesFragment : Fragment(), OnItemClickListener,
    SwipeRefreshLayout.OnRefreshListener {

    private lateinit var binding: FragmentServiceCategoriesBinding
    private lateinit var adapter: CategoryAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: ServiceCategoriesViewModel
    private lateinit var genericHandler: GenericHandler
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private val args: ServiceCategoriesFragmentArgs by navArgs()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        genericHandler = context as GenericHandler
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentServiceCategoriesBinding.inflate(layoutInflater, container, false)
        initViews()
        setupViewModel()
        setupObservers()
        setRecyclerView()
        setOnClickListeners()
        executeApi()
        return binding.root
    }

    private fun executeApi() {
        if (viewModel.subcategories.value == null) {
            viewModel.getSubcategories(args.id)
        }
    }

    override fun onRefresh() {
        if (swipeRefreshLayout.isRefreshing) {
            swipeRefreshLayout.isRefreshing = false
        }
        viewModel.getSubcategories(args.id)
    }

    private fun initViews() {
        adapter = CategoryAdapter(this)
        recyclerView = binding.rvServiceCategories
        swipeRefreshLayout = binding.swipeRefresh
        swipeRefreshLayout.setOnRefreshListener(this)
    }

    private fun setRecyclerView() {
        recyclerView.let {
            it.setHasFixedSize(true)
            val gridLayoutManager = GridLayoutManager(requireContext(), 1)
            it.layoutManager = gridLayoutManager
            it.adapter = adapter.withLoadStateHeaderAndFooter(
                header = MyLoadStateAdapter { adapter.retry() },
                footer = MyLoadStateAdapter { adapter.retry() }
            )
            gridLayoutManager.spanSizeLookup = object : SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    // If progress will be shown then span size will be 1 otherwise it will be 2
                    return if (adapter.getItemViewType(position) == Constants.LOADING_ITEM) 1 else 2
                }
            }
        }
        setAdapterLoadState(adapter)
    }

    private fun setAdapterLoadState(adapter: CategoryAdapter) {
        adapter.addLoadStateListener { loadState ->
            binding.apply {
                genericHandler.showProgressBar(loadState.source.refresh is LoadState.Loading)
                rvServiceCategories.isVisible = loadState.source.refresh is LoadState.NotLoading
                btnRetry.isVisible = loadState.source.refresh is LoadState.Error
                textViewError.isVisible = loadState.source.refresh is LoadState.Error
                // no results
                if (loadState.source.refresh is LoadState.NotLoading &&
                    loadState.append.endOfPaginationReached &&
                    adapter.itemCount < 1
                ) {
                    rvServiceCategories.isVisible = false
                    tvPlaceholder.isVisible = true
                } else {
                    tvPlaceholder.isVisible = false
                }
            }
        }
    }

    private fun setOnClickListeners() {
        binding.apply {
            ivToolbar.setOnClickListener {
                findNavController().popBackStack()
            }
            btnRetry.setOnClickListener {
                adapter.retry()
            }
            etSearchCategories.onFocusChangeListener = focusChangeListener
            binding.etSearchCategories.setOnEditorActionListener(editorListener)
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(ServiceCategoriesViewModel::class.java)
    }

    private fun setupObservers() {
        viewModel.subcategories.observe(viewLifecycleOwner) {
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
        }
    }

    override fun <T> onItemClick(item: T) {
        if (item is Subcategory) {
            val id = item.id
            val action =
                ServiceCategoriesFragmentDirections.actionServiceCategoriesFragmentToServiceGigsFragment(
                    id = id,
                    from = 0
                )
            findNavController().navigate(action)
        }
    }

    private val focusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
        if (!hasFocus) {
            hideKeyboard()
        }
    }

    private val editorListener = TextView.OnEditorActionListener { v, actionId, event ->
        when (actionId) {
            EditorInfo.IME_ACTION_SEARCH -> {
                hideKeyboard()
                val query = binding.etSearchCategories.text.toString().trim()
                //binding.etSearchCategories.text.clear()
                binding.etSearchCategories.clearFocus()
                viewModel.getSubcategories(args.id, query)
            }
        }
        false
    }
}