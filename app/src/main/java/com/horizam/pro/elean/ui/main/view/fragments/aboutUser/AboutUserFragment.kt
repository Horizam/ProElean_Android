package com.horizam.pro.elean.ui.main.view.fragments.aboutUser

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.horizam.pro.elean.Constants
import com.horizam.pro.elean.R
import com.horizam.pro.elean.data.api.ApiHelper
import com.horizam.pro.elean.data.api.RetrofitBuilder
import com.horizam.pro.elean.data.model.SellerActionModel
import com.horizam.pro.elean.data.model.response.FreelancerUserResponse
import com.horizam.pro.elean.data.model.response.ProfileInfo
import com.horizam.pro.elean.databinding.FragmentLoginBinding
import com.horizam.pro.elean.databinding.FragmentSignUpBinding
import com.horizam.pro.elean.databinding.FragmentUserAboutBinding
import com.horizam.pro.elean.ui.base.ViewModelFactory
import com.horizam.pro.elean.ui.main.adapter.NotificationsAdapter
import com.horizam.pro.elean.ui.main.adapter.SellerActionAdapter
import com.horizam.pro.elean.ui.main.adapter.SkillsAdapter
import com.horizam.pro.elean.ui.main.callbacks.GenericHandler
import com.horizam.pro.elean.ui.main.callbacks.OnItemClickListener
import com.horizam.pro.elean.ui.main.callbacks.SellerActionModeHandler
import com.horizam.pro.elean.ui.main.viewmodel.ProfileViewModel
import com.horizam.pro.elean.utils.PrefManager
import com.horizam.pro.elean.utils.Status
import java.lang.Exception


class AboutUserFragment : Fragment(), OnItemClickListener {

    private lateinit var binding: FragmentUserAboutBinding
    private lateinit var adapter: SkillsAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: ProfileViewModel
    private lateinit var genericHandler: GenericHandler
    private lateinit var prefManager: PrefManager
    private lateinit var sellerActionModeHandler: SellerActionModeHandler
    private lateinit var sellerActionAdapter: SellerActionAdapter
    private lateinit var sellerActionList: ArrayList<SellerActionModel>
    private lateinit var linearLayoutManager: LinearLayoutManager

    override fun onAttach(context: Context) {
        super.onAttach(context)
        genericHandler = context as GenericHandler
        sellerActionModeHandler = context as SellerActionModeHandler
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserAboutBinding.inflate(layoutInflater, container, false)
        initComponent()
        setupViewModel()
        setRecyclerview()
        setupObservers()
        setClickListener()
        executeApi()
        setAdapter()
        return binding.root
    }

    private fun setAdapter() {
        setDetailBuyerActionList()
        sellerActionAdapter = SellerActionAdapter(sellerActionList, this)
        linearLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvBuyerActions.layoutManager = linearLayoutManager
        binding.rvBuyerActions.adapter = sellerActionAdapter
    }

    private fun setDetailBuyerActionList() {
        sellerActionList.add(
            SellerActionModel(
                title = "Post a Job",
                image = R.drawable.ic_create_service
            )
        )
        sellerActionList.add(
            SellerActionModel(
                title = "View Posted Jobs",
                image = R.drawable.ic_manage_service
            )
        )
    }

    private fun setClickListener() {
//        binding.btnPostAJob.setOnClickListener {
//            findNavController().navigate(R.id.postJobFragment)
//        }
//        binding.btnViewPostedJob.setOnClickListener {
//            findNavController().navigate(R.id.postedJobsFragment)
//        }

        binding.switchSellerMode.setOnCheckedChangeListener { compoundButton, b ->
            if (b){
                prefManager.sellerMode =  1
                sellerActionModeHandler.sellerActionMode(1)
            }else{
               prefManager.sellerMode = 0
                sellerActionModeHandler.sellerActionMode(0)
            }
        }
    }

    private fun initComponent() {
        sellerActionList = ArrayList()
        prefManager = PrefManager(requireContext())
        binding.switchSellerMode.isChecked = prefManager.sellerMode != 0
    }

    private fun executeApi() {
        viewModel.freelancerProfileDataCall(prefManager.userId)
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

    private fun handleResponse(response: ProfileInfo) {
        try {
            setUiData(response)
        } catch (e: Exception) {
            genericHandler.showMessage(e.message.toString())
        }
    }

    private fun setUiData(profileInfo: ProfileInfo) {
        binding.apply {
            profileInfo.let { profile ->
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
                if (profile.user_languages.isEmpty()) {
                    tvLanguage.text = getString(R.string.str_no_language_available)
                } else {
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
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = adapter
    }

    override fun <T> onItemClick(item: T) {

    }
}