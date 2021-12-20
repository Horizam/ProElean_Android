package com.horizam.pro.elean.ui.main.view.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import com.google.android.exoplayer2.util.Log
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.horizam.pro.elean.Constants
import com.horizam.pro.elean.R
import com.horizam.pro.elean.data.api.ApiHelper
import com.horizam.pro.elean.data.api.RetrofitBuilder
import com.horizam.pro.elean.data.model.response.Order
import com.horizam.pro.elean.databinding.ActivityOrderDetailsBinding
import com.horizam.pro.elean.ui.base.ViewModelFactory
import com.horizam.pro.elean.ui.main.adapter.ViewPagerOrderDetailsAdapter
import com.horizam.pro.elean.ui.main.callbacks.GenericHandler
import com.horizam.pro.elean.ui.main.viewmodel.SellerOrdersViewModel
import com.horizam.pro.elean.utils.PrefManager
import com.horizam.pro.elean.utils.Status

class OrderDetailsActivity : AppCompatActivity(), GenericHandler {

    private lateinit var binding: ActivityOrderDetailsBinding
    private lateinit var listFragmentTitles:ArrayList<String>
    private lateinit var viewPagerFragmentAdapter: ViewPagerOrderDetailsAdapter
    private lateinit var prefManager: PrefManager
    private lateinit var viewModel: SellerOrdersViewModel
    private lateinit var genericHandler: GenericHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarColor()
        binding = ActivityOrderDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViewModel()
        initViews()
        setupObservers()
        setClickListeners()
    }

    private fun setupObservers() {
        viewModel.orderByID.observe(this, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        genericHandler.showProgressBar(false)
                        resource.data?.let { response ->
                            handleResponse(response.order)
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

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(SellerOrdersViewModel::class.java)
    }

    private fun exeGetOrderById(orderID: Int) {
        viewModel.getOrderById(orderID)
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
        try {
            prefManager = PrefManager(this)
            genericHandler = this
            listFragmentTitles = arrayListOf("Order Details","Timeline" , "Chat")
            if(intent.hasExtra(Constants.ORDER)){
                loadData()
            }else if(intent.hasExtra(Constants.ORDER_ID)){
                exeGetOrderById(intent.getStringExtra(Constants.ORDER_ID)!!.toInt())
            }
        }catch (e:Exception){
            Log.e("exception",e.message.toString())
        }
    }

    private fun loadData() {
        val gson = Gson()
        val order = gson.fromJson(
            intent.getStringExtra(Constants.ORDER),
            Order::class.java
        )
        val pair: Pair<Int, Int> = Pair(
            intent.getIntExtra(Constants.ORDER_USER_ROLE, -1),
            intent.getIntExtra(Constants.ORDER_USER_ACTION, -1)
        )
        viewPagerFragmentAdapter =
            ViewPagerOrderDetailsAdapter(this, listFragmentTitles, order, pair)
        setData(order)
        setTabs()
    }

    private fun handleResponse(order: Order) {
        var userRole = 0
        if(prefManager.userId == order.buyerId){
            userRole = Constants.BUYER_USER
        }else{
            userRole = Constants.SELLER_USER
        }
        val pair: Pair<Int, Int> = Pair(
            userRole,
            order.statusId
        )
        viewPagerFragmentAdapter =
            ViewPagerOrderDetailsAdapter(this, listFragmentTitles, order, pair)
        setData(order)
        setTabs()
    }

    private fun setData(order: Order) {
        binding.tvUserMode.text = if (prefManager.userId == order.sellerId) {
            getString(R.string.str_seller)
        } else {
            getString(R.string.str_buyer)
        }
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