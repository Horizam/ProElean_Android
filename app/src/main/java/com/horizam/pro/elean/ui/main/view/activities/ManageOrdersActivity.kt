package com.horizam.pro.elean.ui.main.view.activities

import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.horizam.pro.elean.R
import com.horizam.pro.elean.databinding.ActivityManageOrdersBinding
import com.horizam.pro.elean.databinding.ActivityUserAboutBinding
import com.horizam.pro.elean.ui.main.adapter.ViewPagerFragmentAdapter
import com.horizam.pro.elean.ui.main.adapter.ViewPagerManageOrdersAdapter
import com.horizam.pro.elean.ui.main.callbacks.GenericHandler

class ManageOrdersActivity : AppCompatActivity(), GenericHandler {

    private lateinit var binding: ActivityManageOrdersBinding
    private lateinit var listFragmentTitles:ArrayList<String>
    private lateinit var viewPagerFragmentAdapter: ViewPagerManageOrdersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarColor()
        binding = ActivityManageOrdersBinding.inflate(layoutInflater)
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
        listFragmentTitles = arrayListOf("Active" , "Late" ,"Delivered","Revision","Completed" , "Cancel" ,"Disputed")
        viewPagerFragmentAdapter = ViewPagerManageOrdersAdapter(this,listFragmentTitles)
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