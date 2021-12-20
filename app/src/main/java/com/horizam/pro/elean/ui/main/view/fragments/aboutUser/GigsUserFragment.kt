package com.horizam.pro.elean.ui.main.view.fragments.aboutUser

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.horizam.pro.elean.Constants
import com.horizam.pro.elean.R
import com.horizam.pro.elean.data.api.ApiHelper
import com.horizam.pro.elean.data.api.RetrofitBuilder
import com.horizam.pro.elean.data.model.response.FreelancerUserResponse
import com.horizam.pro.elean.data.model.response.User_services
import com.horizam.pro.elean.databinding.FragmentUserGigsBinding
import com.horizam.pro.elean.ui.base.ViewModelFactory
import com.horizam.pro.elean.ui.main.adapter.UserGigsAdapter
import com.horizam.pro.elean.ui.main.callbacks.GenericHandler
import com.horizam.pro.elean.ui.main.callbacks.OnItemClickListener
import com.horizam.pro.elean.ui.main.view.activities.UserAboutActivity
import com.horizam.pro.elean.ui.main.view.activities.UserGigDetailsActivity
import com.horizam.pro.elean.ui.main.viewmodel.ProfileViewModel
import com.horizam.pro.elean.utils.Status
import java.lang.Exception


class GigsUserFragment : Fragment(), OnItemClickListener {

    private lateinit var binding: FragmentUserGigsBinding
    private lateinit var adapter: UserGigsAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: ProfileViewModel
    private lateinit var genericHandler: GenericHandler

    override fun onAttach(context: Context) {
        super.onAttach(context)
        genericHandler = context as GenericHandler
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserGigsBinding.inflate(layoutInflater,container,false)
        setupViewModel()
        setupObservers()
        setRecyclerView()
        return binding.root
    }

    private fun setRecyclerView() {
        recyclerView = binding.rvUserGigs
        adapter = UserGigsAdapter(this)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.freelancerProfileData.observe(viewLifecycleOwner, {
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

    private fun handleResponse(response: FreelancerUserResponse) {
        try {
            binding.apply {
                response.user_services.let { services ->
                    if(services.isNotEmpty()){
                        adapter.submitList(services)
                        tvPlaceholder.isVisible = false
                        recyclerView.isVisible = true
                    }else{
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
        if (item is User_services){
            Intent(requireActivity(), UserGigDetailsActivity::class.java).also {
                it.putExtra("uuid",item.uuid)
                startActivity(it)
            }
        }
    }

}