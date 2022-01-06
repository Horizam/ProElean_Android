package com.horizam.pro.elean.ui.main.view.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.horizam.pro.elean.R
import com.horizam.pro.elean.databinding.ActivityManageSalesBinding
import com.horizam.pro.elean.ui.main.adapter.ViewPagerManageOrdersAdapter
import com.horizam.pro.elean.ui.main.adapter.ViewPagerManageSalesAdapter
import com.horizam.pro.elean.ui.main.callbacks.GenericHandler

class ManageSalesActivity : AppCompatActivity(), GenericHandler {

    private lateinit var binding:ActivityManageSalesBinding
    private lateinit var listFragmentTitles:ArrayList<String>
    private lateinit var viewPagerFragmentAdapter: ViewPagerManageSalesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarColor()
        binding = ActivityManageSalesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
        setTabs()
        setClickListeners()
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
//        listFragmentTitles = arrayListOf("Active", "Late" ,"Delivered","Completed","Revision", "Cancel" ,"Disputed")
//        viewPagerFragmentAdapter = ViewPagerManageSalesAdapter(this,listFragmentTitles)
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

}