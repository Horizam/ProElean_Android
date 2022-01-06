package com.horizam.pro.elean.ui.main.view.fragments.aboutUser

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.horizam.pro.elean.Constants
import com.horizam.pro.elean.R
import com.horizam.pro.elean.data.api.ApiHelper
import com.horizam.pro.elean.data.api.RetrofitBuilder
import com.horizam.pro.elean.data.model.requests.FavouriteRequest
import com.horizam.pro.elean.data.model.response.*
import com.horizam.pro.elean.databinding.FragmentUserGigsBinding
import com.horizam.pro.elean.ui.base.ViewModelFactory
import com.horizam.pro.elean.ui.main.adapter.UserGigsAdapter
import com.horizam.pro.elean.ui.main.callbacks.FavouriteHandler
import com.horizam.pro.elean.ui.main.callbacks.GenericHandler
import com.horizam.pro.elean.ui.main.callbacks.OnItemClickListener
import com.horizam.pro.elean.ui.main.view.activities.UserGigDetailsActivity
import com.horizam.pro.elean.ui.main.viewmodel.ProfileViewModel
import com.horizam.pro.elean.utils.PrefManager
import com.horizam.pro.elean.utils.Resource
import com.horizam.pro.elean.utils.Status
import java.lang.Exception


class GigsUserFragment : Fragment(), OnItemClickListener, FavouriteHandler {

    private lateinit var binding: FragmentUserGigsBinding
    private lateinit var adapter: UserGigsAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: ProfileViewModel
    private lateinit var genericHandler: GenericHandler
    private lateinit var prefManager: PrefManager


    override fun onAttach(context: Context) {
        super.onAttach(context)
        genericHandler = context as GenericHandler
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserGigsBinding.inflate(layoutInflater, container, false)
        initComponents()
        setupViewModel()
        setupObservers()
        setRecyclerView()
        exeApi()
        setupFavoritesObservers()
        return binding.root
    }

    private fun initComponents() {
        prefManager = PrefManager(requireContext())
    }

    private fun setupFavoritesObservers() {
        viewModel.makeFavourite.observe(viewLifecycleOwner, makeFavouriteObserver)
    }

    private val makeFavouriteObserver = Observer<Resource<GeneralResponse>> {
        it?.let { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    genericHandler.showProgressBar(false)
                    val id = requireActivity().intent.getStringExtra("id")
                    viewModel.userServicesCall(id!!)
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

    private fun exeApi() {
        viewModel.userServicesCall(prefManager.userId)
    }

    private fun setRecyclerView() {
        recyclerView = binding.rvUserGigs
        adapter = UserGigsAdapter(this, this , prefManager.userId)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.userServices.observe(viewLifecycleOwner, {
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
                        genericHandler.showMessage(it.message.toString())
                    }
                    Status.LOADING -> {
                        genericHandler.showProgressBar(true)
                    }
                }
            }
        })
    }

    private fun handleResponse(servicesResponse: ServicesResponse) {
        try {
            binding.apply {
                servicesResponse.serviceList.let { services ->
                    if (services.isNotEmpty()) {
                        adapter.submitList(services)
                        tvPlaceholder.isVisible = false
                        recyclerView.isVisible = true
                    } else {
                        tvPlaceholder.isVisible = true
                        recyclerView.isVisible = false
                    }
                }
            }
        } catch (e: Exception) {
            genericHandler.showMessage(e.message.toString())
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            requireActivity(),
            ViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(ProfileViewModel::class.java)
    }

    override fun <T> onItemClick(item: T) {
        if (item is ServiceDetail) {
            Intent(requireActivity(), UserGigDetailsActivity::class.java).also {
                it.putExtra("uuid", item.id)
                startActivity(it)
            }
        }
    }

    override fun <T> addRemoveWishList(item: T) {
        if (item is ServiceDetail) {
            viewModel.addToWishlistCall(FavouriteRequest(item.id))
        }
    }

}