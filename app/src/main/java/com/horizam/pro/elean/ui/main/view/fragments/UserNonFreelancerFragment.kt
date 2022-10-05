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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.horizam.pro.elean.App
import com.horizam.pro.elean.Constants
import com.horizam.pro.elean.R
import com.horizam.pro.elean.data.api.ApiHelper
import com.horizam.pro.elean.data.api.RetrofitBuilder
import com.horizam.pro.elean.data.model.response.ProfileInfo
import com.horizam.pro.elean.databinding.FragmentUserNonFreelancerBinding
import com.horizam.pro.elean.ui.base.ViewModelFactory
import com.horizam.pro.elean.ui.main.adapter.SkillsAdapter
import com.horizam.pro.elean.ui.main.callbacks.GenericHandler
import com.horizam.pro.elean.ui.main.callbacks.OnItemClickListener
import com.horizam.pro.elean.ui.main.viewmodel.ProfileViewModel
import com.horizam.pro.elean.utils.Status
import java.lang.Exception


class UserNonFreelancerFragment : Fragment(), OnItemClickListener {

    private lateinit var binding: FragmentUserNonFreelancerBinding
    private lateinit var adapter: SkillsAdapter
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
        binding = FragmentUserNonFreelancerBinding.inflate(layoutInflater, container, false)
        setToolbarData()
        initViews()
        setupViewModel()
        setupObservers()
        setOnClickListeners()
        setRecyclerview()
        return binding.root
    }

    private fun initViews() {
        adapter = SkillsAdapter(this)
        recyclerView = binding.rvSkills
    }

    private fun setRecyclerview() {
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = adapter
    }

    private fun setOnClickListeners() {
        binding.apply {
            toolbar.ivToolbar.setOnClickListener {
                findNavController().popBackStack()
            }
            btnRetry.setOnClickListener {
                executeApi()
            }
        }
    }

    private fun executeApi() {
        genericHandler.showProgressBar(true)
        viewModel.profileDataCall()
    }

    private fun setToolbarData() {
        binding.toolbar.ivToolbar.setImageResource(R.drawable.ic_back)
        binding.toolbar.ivToolbar.isVisible=true
        binding.toolbar.tvToolbar.text = App.getAppContext()!!.getString(R.string.str_user_profile)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(ProfileViewModel::class.java)
    }

    private fun setupObservers() {
        viewModel.profileData.observe(viewLifecycleOwner, {
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
                        genericHandler.showErrorMessage(it.message.toString())
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

    private fun changeViewVisibility(textView: Boolean, button: Boolean, layout: Boolean) {
        binding.textViewError.isVisible = textView
        binding.btnRetry.isVisible = button
        binding.mainLayout.isVisible = layout
    }

    private fun handleResponse(response: ProfileInfo) {
        try {
            setUiData(response)
        } catch (e: Exception) {
            genericHandler.showErrorMessage(e.message.toString())
        }
    }

    private fun setUiData(profileInfo: ProfileInfo) {
        binding.apply {
            Glide.with(this@UserNonFreelancerFragment)
                .load(Constants.BASE_URL.plus(profileInfo.image))
                .error(R.drawable.img_profile)
                .into(binding.ivUser)
            tvUserName.text = profileInfo.username
            tvUserRating.text = profileInfo.user_rating.toString()
            tvRatingNumber.text = "(".plus(profileInfo.total_reviews.toString()).plus(")")
            tvLocation.text = profileInfo.address
            tvResponse.text = profileInfo.created_at
            tvRecentDelivery.text = profileInfo.recent_delivery
            tvPhone.text = profileInfo.phone
            tvEmail.text = profileInfo.email
            tvLanguage.text = profileInfo.user_languages //.joinToString(separator = ", ")
            tvDescAboutUser.text = profileInfo.description
            adapter.submitList(profileInfo.user_skills)
        }
    }

    override fun <T> onItemClick(item: T) {

    }
}