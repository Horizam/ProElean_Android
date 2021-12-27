package com.horizam.pro.elean.ui.main.view.fragments.aboutUser

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
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
import com.horizam.pro.elean.databinding.FragmentLoginBinding
import com.horizam.pro.elean.databinding.FragmentSignUpBinding
import com.horizam.pro.elean.databinding.FragmentUserAboutBinding
import com.horizam.pro.elean.ui.base.ViewModelFactory
import com.horizam.pro.elean.ui.main.adapter.NotificationsAdapter
import com.horizam.pro.elean.ui.main.adapter.SkillsAdapter
import com.horizam.pro.elean.ui.main.callbacks.GenericHandler
import com.horizam.pro.elean.ui.main.callbacks.OnItemClickListener
import com.horizam.pro.elean.ui.main.viewmodel.ProfileViewModel
import com.horizam.pro.elean.utils.Status
import java.lang.Exception


class AboutUserFragment : Fragment(), OnItemClickListener {

    private lateinit var binding: FragmentUserAboutBinding
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
        binding = FragmentUserAboutBinding.inflate(layoutInflater,container,false)
        setupViewModel()
        setRecyclerview()
        setupObservers()
        return binding.root
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
            if (response.status == Constants.STATUS_OK){
                setUiData(response)
            }else{
                genericHandler.showMessage(response.message)
            }
        } catch (e: Exception) {
            genericHandler.showMessage(e.message.toString())
        }
    }

    private fun setUiData(response: FreelancerUserResponse) {
        binding.apply {
            response.profileInfo.let { profile ->
                Glide.with(this@AboutUserFragment)
                    .load(Constants.BASE_URL.plus(profile.image))
                    .error(R.drawable.img_profile)
                    .into(ivSelectedCategory)
                tvUserName.text = profile.name
                tvUserRating.text = profile.user_rating.toString()
                tvRatingNumber.text = "(".plus(profile.total_reviews.toString()).plus(")")
                tvLocation.text = profile.address
                tvResponse.text = profile.created_at
                tvRecentDelivery.text = profile.recent_delivery
                if (profile.user_languages.isEmpty()){
                    tvLanguage.text = getString(R.string.str_no_language_available)
                }else{
                    tvLanguage.text = profile.user_languages.joinToString(separator = ", ")
                }
                tvDescAboutUser.text = profile.description
                adapter.submitList(profile.user_skills)
            }
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            requireActivity(),
            ViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(ProfileViewModel::class.java)
    }

    private fun setRecyclerview() {
        adapter = SkillsAdapter(this)
        recyclerView = binding.rvSkills
        recyclerView.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        recyclerView.adapter = adapter
    }

    override fun <T> onItemClick(item: T) {

    }
}