package com.horizam.pro.elean.ui.main.view.fragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.Gson
import com.horizam.pro.elean.App
import com.horizam.pro.elean.R
import com.horizam.pro.elean.data.api.ApiHelper
import com.horizam.pro.elean.data.api.RetrofitBuilder
import com.horizam.pro.elean.data.model.MessageGig
import com.horizam.pro.elean.data.model.requests.FavouriteRequest
import com.horizam.pro.elean.data.model.response.GeneralResponse
import com.horizam.pro.elean.data.model.response.ServiceDetail
import com.horizam.pro.elean.databinding.DialogDeleteBinding
import com.horizam.pro.elean.databinding.FragmentSavedBinding
import com.horizam.pro.elean.ui.base.ViewModelFactory
import com.horizam.pro.elean.ui.main.adapter.MyLoadStateAdapter
import com.horizam.pro.elean.ui.main.adapter.SavedAdapter
import com.horizam.pro.elean.ui.main.callbacks.ContactSellerHandler
import com.horizam.pro.elean.ui.main.callbacks.GenericHandler
import com.horizam.pro.elean.ui.main.callbacks.SavedGigsHandler
import com.horizam.pro.elean.ui.main.viewmodel.SavedViewModel
import com.horizam.pro.elean.utils.PrefManager
import com.horizam.pro.elean.utils.Resource
import com.horizam.pro.elean.utils.Status


class SavedFragment : Fragment(), SavedGigsHandler, SwipeRefreshLayout.OnRefreshListener,
    ContactSellerHandler {

    private lateinit var binding: FragmentSavedBinding
    private lateinit var adapter: SavedAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: SavedViewModel
    private lateinit var genericHandler: GenericHandler
    private lateinit var prefManager: PrefManager
    private lateinit var dialogDelete: Dialog
    private lateinit var bindingDeleteDialog: DialogDeleteBinding
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onAttach(context: Context) {
        super.onAttach(context)
        genericHandler = context as GenericHandler
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViewModel()
        setupFavoritesObservers()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSavedBinding.inflate(layoutInflater, container, false)
        setToolbarData()
        initViews()
        //setupViewModel()
        setupObservers()
        setRecyclerview()
        setOnClickListeners()
        exeApi()
        return binding.root
    }

    private fun exeApi() {
        if (viewModel.savedGigs.value == null) {
            viewModel.getSavedGigsCall()
        }
    }

    private fun initViews() {
        prefManager = PrefManager(requireContext())
        adapter = SavedAdapter(this)
        recyclerView = binding.rvSaved
        swipeRefreshLayout = binding.swipeRefresh
        swipeRefreshLayout.setOnRefreshListener(this)
        initDeleteDialog()
    }

    private fun initDeleteDialog() {
        dialogDelete = Dialog(requireContext())
        bindingDeleteDialog = DialogDeleteBinding.inflate(layoutInflater)
        dialogDelete.setContentView(bindingDeleteDialog.root)
    }

    private fun setRecyclerview() {
        recyclerView.let {
            it.setHasFixedSize(true)
            it.layoutManager = LinearLayoutManager(requireContext())
            it.adapter = adapter.withLoadStateHeaderAndFooter(
                header = MyLoadStateAdapter { adapter.retry() },
                footer = MyLoadStateAdapter { adapter.retry() }
            )
        }
        setAdapterLoadState(adapter)
    }

    private fun setAdapterLoadState(adapter: SavedAdapter) {
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
            btnRetry.setOnClickListener {
                adapter.retry()
            }
        }
    }

    private fun setToolbarData() {
        binding.toolbar.ivToolbar.setImageResource(R.drawable.ic_back)
        binding.toolbar.tvToolbar.text = App.getAppContext()!!.getString(R.string.str_saved)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(SavedViewModel::class.java)
    }

    private fun setupObservers() {
        viewModel.savedGigs.observe(viewLifecycleOwner) {
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
        }
    }

    private fun setupFavoritesObservers() {
        viewModel.makeFavourite.observe(this, makeFavouriteObserver)
    }

    private val makeFavouriteObserver = Observer<Resource<GeneralResponse>> {
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
        genericHandler.showSuccessMessage(response.message)
        viewModel.getSavedGigsCall()
    }

    override fun <T> addRemoveWishList(item: T) {
        if (item is ServiceDetail) {
            bindingDeleteDialog.tvTitle.text =
                getString(R.string.str_are_you_sure_to_undo_favourite)
            dialogDelete.show()
            bindingDeleteDialog.btnYes.setOnClickListener {
                dialogDelete.dismiss()
                genericHandler.showProgressBar(true)
                viewModel.addToWishlistCall(FavouriteRequest(item.id))
            }
            bindingDeleteDialog.btnNo.setOnClickListener { dialogDelete.dismiss() }
        }
    }

    override fun <T> onItemClick(item: T) {

        if (item is ServiceDetail) {
            val gson = Gson()
            SavedFragmentDirections.actionSavedFragmentToGigsDetailsFragment(
                serviceDetail = gson.toJson(item).toString()
            ).also {
                findNavController().navigate(it)
            }
        }
    }

    override fun <T> contactSeller(item: T) {
        if (item is ServiceDetail) {
            try {
                if (prefManager.userId != item.service_user.id && item.service_user.id != "") {
                    val messageGig = MessageGig(
                        gigId = item.id,
                        gigImage = item.service_media[0].media,
                        gigTitle = item.s_description,
                        gigUsername = item.service_user.username
                    )
                    SavedFragmentDirections.actionSavedFragmentToMessagesFragment(
                        userName = item.service_user.name,
                        photo = item.service_user.image,
                        id = item.service_user.id,
                        refersGig = true,
                        messageGig = messageGig
                    ).also {
                        findNavController().navigate(it)
                    }
                }
            } catch (e: Exception) {
                genericHandler.showErrorMessage(e.message.toString())
            }
        }
    }

    override fun onRefresh() {
        if (swipeRefreshLayout.isRefreshing) {
            swipeRefreshLayout.isRefreshing = false
        }
        viewModel.getSavedGigsCall()
    }

}