package com.horizam.pro.elean.ui.main.view.activities

import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.horizam.pro.elean.R
import com.horizam.pro.elean.data.api.ApiHelper
import com.horizam.pro.elean.data.api.RetrofitBuilder
import com.horizam.pro.elean.data.model.response.FreelancerUserResponse
import com.horizam.pro.elean.data.model.response.GeneralResponse
import com.horizam.pro.elean.databinding.ActivityUserAboutBinding
import com.horizam.pro.elean.ui.base.ViewModelFactory
import com.horizam.pro.elean.ui.main.adapter.ViewPagerFragmentAdapter
import com.horizam.pro.elean.ui.main.callbacks.GenericHandler
import com.horizam.pro.elean.ui.main.viewmodel.HomeViewModel
import com.horizam.pro.elean.ui.main.viewmodel.ProfileViewModel
import com.horizam.pro.elean.utils.Status

class UserAboutActivity : AppCompatActivity(), GenericHandler {

    private lateinit var binding: ActivityUserAboutBinding
    private lateinit var listFragmentTitles: ArrayList<String>
    private lateinit var viewPagerFragmentAdapter: ViewPagerFragmentAdapter
    private lateinit var viewModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarColor()
        binding = ActivityUserAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
        setTabs()
        setupViewModel()
        setupObservers()
        setClickListeners()
        executeApi()
    }

    private fun executeApi() {
        val id = intent.getIntExtra("id", -1)
        if (id != -1) {
            showProgressBar(true)
            viewModel.freelancerProfileDataCall(id)
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(ProfileViewModel::class.java)
    }

    private fun setupObservers() {
        viewModel.freelancerProfileData.observe(this, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        showProgressBar(false)
                        resource.data?.let { response ->
                            handleResponse(response)
                        }
                    }
                    Status.ERROR -> {
                        showProgressBar(false)
                        showMessage(it.message.toString())
                    }
                    Status.LOADING -> {
                        showProgressBar(true)
                    }
                }
            }
        })
    }

    private fun handleResponse(response: FreelancerUserResponse) {
        //showMessage(response.message)
    }

    override fun showProgressBar(show: Boolean) {
        binding.progressLayout.isVisible = show
    }

    override fun showMessage(message: String) {
        Snackbar.make(
            findViewById(android.R.id.content),
            message, Snackbar.LENGTH_LONG
        ).show()
    }

    override fun showNoInternet(show: Boolean) {

    }

    private fun setStatusBarColor() {
        val window: Window = this.window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorOne)
    }

    private fun setClickListeners() {
        binding.ivToolbar.setOnClickListener {
            finish()
        }
    }

    private fun setTabs() {
        binding.viewPager.adapter = viewPagerFragmentAdapter
        TabLayoutMediator(
            binding.tabLayout, binding.viewPager
        ) { tab: TabLayout.Tab, position: Int ->
            tab.text = listFragmentTitles[position]
        }.attach()
    }

    private fun initViews() {
        listFragmentTitles = arrayListOf("About", "Services", "Reviews")
        viewPagerFragmentAdapter = ViewPagerFragmentAdapter(this, listFragmentTitles)
    }
}