package com.horizam.pro.elean.ui.main.view.fragments.aboutUser

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.horizam.pro.elean.Constants
import com.horizam.pro.elean.R
import com.horizam.pro.elean.data.api.ApiHelper
import com.horizam.pro.elean.data.api.RetrofitBuilder
import com.horizam.pro.elean.data.model.SellerActionModel
import com.horizam.pro.elean.data.model.response.ProfileInfo
import com.horizam.pro.elean.databinding.DialogDeleteBinding
import com.horizam.pro.elean.databinding.FragmentUserAboutBinding
import com.horizam.pro.elean.ui.base.ViewModelFactory
import com.horizam.pro.elean.ui.main.adapter.SellerActionAdapter
import com.horizam.pro.elean.ui.main.adapter.SkillsAdapter
import com.horizam.pro.elean.ui.main.callbacks.GenericHandler
import com.horizam.pro.elean.ui.main.callbacks.OnItemClickListener
import com.horizam.pro.elean.ui.main.callbacks.SellerActionModeHandler
import com.horizam.pro.elean.ui.main.view.activities.AuthenticationActivity
import com.horizam.pro.elean.ui.main.viewmodel.ProfileViewModel
import com.horizam.pro.elean.utils.BaseUtils
import com.horizam.pro.elean.utils.PrefManager
import com.horizam.pro.elean.utils.Status
import kotlinx.android.synthetic.main.dialog_delete.*
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
    private lateinit var dialogDelete: Dialog
    private lateinit var bindingDeleteDialog: DialogDeleteBinding
    private lateinit var mAuth: FirebaseAuth

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
        initDeleteDialog()
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
                image = R.drawable.img_post_job
            )
        )
        sellerActionList.add(
            SellerActionModel(
                title = "View Posted Jobs",
                image = R.drawable.img_posted_job
            )
        )
    }

    private fun setClickListener() {
        binding.switchSellerMode.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                prefManager.sellerMode = 1
                sellerActionModeHandler.sellerActionMode(1)
            } else {
                prefManager.sellerMode = 0
                sellerActionModeHandler.sellerActionMode(0)
            }
        }
        binding.ivSetting.setOnClickListener {
            findNavController().navigate(R.id.settingsFragment)
        }
        binding.clBecomeFreelancer.setOnClickListener {
            findNavController().navigate(R.id.becomeFreelancerOneFragment)
        }
        binding.clLogout.setOnClickListener {
            dialogDelete.show()
            dialogDelete.btn_yes.setOnClickListener {
                mAuth.signOut()
                prefManager.anonymousUser = ""
                executeLogoutApi()
                dialogDelete.dismiss()
            }
            dialogDelete.btn_no.setOnClickListener {
                dialogDelete.dismiss()
            }
        }
        binding.clSupport.setOnClickListener {
            findNavController().navigate(R.id.supportFragment)
        }
        binding.clSaved.setOnClickListener {
            findNavController().navigate(R.id.savedFragment)
        }
    }

    private fun executeLogoutApi() {
        genericHandler.showProgressBar(true)
        viewModel.logoutCall()
    }

    private fun initDeleteDialog() {
        dialogDelete = Dialog(requireActivity())
        bindingDeleteDialog = DialogDeleteBinding.inflate(layoutInflater)
        bindingDeleteDialog.tvTitle.text = "Are you sure you want to logout?"
        dialogDelete.setContentView(bindingDeleteDialog.root)
    }

    private fun initComponent() {
        mAuth = FirebaseAuth.getInstance()
        sellerActionList = ArrayList()
        prefManager = PrefManager(requireContext())
        binding.switchSellerMode.isChecked = prefManager.sellerMode != 0
        if (prefManager.sellerMode == 0) {
            binding.clSaved.visibility = View.VISIBLE
        } else {
            binding.clSaved.visibility = View.GONE
            binding.rvBuyerActions.visibility = View.GONE
            binding.tvBuyerAction.visibility = View.GONE
        }
        if (prefManager.isFreelancer == 0) {
            binding.cvSellerMode.visibility = View.GONE
            binding.cvBecomeFreelancer.visibility = View.VISIBLE
        } else {
            binding.cvSellerMode.visibility = View.VISIBLE
            binding.cvBecomeFreelancer.visibility = View.GONE
        }
    }

    private fun executeApi() {
        viewModel.freelancerProfileDataCall(prefManager.userId)
    }

    private fun setupObservers() {
        viewModel.freelancerProfileData.observe(viewLifecycleOwner) {
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

        // logout observer
        viewModel.logoutUser.observe(viewLifecycleOwner, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        genericHandler.showProgressBar(false)
                        resource.data?.let { response ->
                            genericHandler.showSuccessMessage(response.message)
                            logout()
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

    private fun logout() {
        val fcmToken = prefManager.fcmToken
        prefManager.clearAll()
        prefManager.fcmToken = fcmToken
        this.findNavController().popBackStack()
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
            profileInfo.let { profile ->
                Glide.with(this@AboutUserFragment)
                    .load(Constants.BASE_URL.plus(profile.image))
                    .error(R.drawable.img_profile)
                    .into(ivUser)
                tvUserName.text = profile.name
                tvUserRating.text = profile.user_rating.toString()
                tvRatingNumber.text = "(".plus(profile.total_reviews.toString()).plus(")")
                tvLocation.text = profile.address
                tvResponse.text = profile.created_at
                tvRecentDelivery.text = profile.recent_delivery
                if (profile.user_languages==null) {
                    tvLanguage.text = getString(R.string.str_no_language_available)
                } else {
                    tvLanguage.text = profile.user_languages  //.joinToString(separator = ", ")
                }
                if (profile.languages==null) {
                    tvLanguage.text = getString(R.string.str_no_language_available)
                } else {
                    tvLanguage.text = profile.languages  //.joinToString(separator = ", ")
                }
                if (profile.description.trim().isEmpty()) {
                    tvDescAboutUser.text = "No description has been added"
                } else {
                    tvDescAboutUser.text = profile.description
                }
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
        if (item is Int) {
            when (item) {
                0 -> {
                    findNavController().navigate(
                        R.id.postJobFragment,
                        null,
                        BaseUtils.animationOpenScreen()
                    )
                }
                1 -> {
                    findNavController().navigate(
                        R.id.postedJobsFragment,
                        null,
                        BaseUtils.animationOpenScreen()
                    )
                }
            }
        }
    }

}