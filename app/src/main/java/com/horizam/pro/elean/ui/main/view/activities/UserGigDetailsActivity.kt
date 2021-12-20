package com.horizam.pro.elean.ui.main.view.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.glide.slider.library.SliderLayout
import com.glide.slider.library.animations.DescriptionAnimation
import com.glide.slider.library.slidertypes.BaseSliderView
import com.glide.slider.library.slidertypes.DefaultSliderView
import com.glide.slider.library.tricks.ViewPagerEx
import com.google.android.material.snackbar.Snackbar
import com.horizam.pro.elean.App
import com.horizam.pro.elean.Constants
import com.horizam.pro.elean.R
import com.horizam.pro.elean.data.api.ApiHelper
import com.horizam.pro.elean.data.api.RetrofitBuilder
import com.horizam.pro.elean.data.model.response.GigDetailsResponse
import com.horizam.pro.elean.data.model.response.ServiceInfo
import com.horizam.pro.elean.databinding.ActivityUserGigDetailsBinding
import com.horizam.pro.elean.ui.base.ViewModelFactory
import com.horizam.pro.elean.ui.main.callbacks.GenericHandler
import com.horizam.pro.elean.ui.main.callbacks.OnItemClickListener
import com.horizam.pro.elean.ui.main.viewmodel.GigDetailsViewModel
import com.horizam.pro.elean.ui.main.viewmodel.ProfileViewModel
import com.horizam.pro.elean.utils.Status
import java.lang.Exception

class UserGigDetailsActivity : AppCompatActivity(), GenericHandler,
    BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {

    private lateinit var binding: ActivityUserGigDetailsBinding
    private lateinit var viewModel: GigDetailsViewModel
    private lateinit var glideSliderLayout: SliderLayout
    private lateinit var requestOptions: RequestOptions
    private var uuid: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarColor()
        binding = ActivityUserGigDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setToolbarData()
        initViews()
        setupViewModel()
        setupObservers()
        setClickListeners()
        executeApi()
    }

    private fun executeApi() {
        val uuid = intent.getStringExtra("uuid")
        if (!uuid.isNullOrEmpty()) {
            showProgressBar(true)
            viewModel.gigDetailsCall(uuid)
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(GigDetailsViewModel::class.java)
    }

    private fun setupObservers() {
        viewModel.gigDetails.observe(this, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        showProgressBar(false)
                        resource.data?.let { response ->
                            handleResponse(response)
                            changeViewVisibility(textView = false, button = false, layout = true)
                        }
                    }
                    Status.ERROR -> {
                        showProgressBar(false)
                        showMessage(it.message.toString())
                        changeViewVisibility(textView = true, button = true, layout = false)
                    }
                    Status.LOADING -> {
                        showProgressBar(true)
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

    private fun handleResponse(response: GigDetailsResponse) {
        try {
            setUIData(response.serviceInfo)
        } catch (e: Exception) {
            showMessage(e.message.toString())
        }
    }

    private fun setUIData(serviceInfo: ServiceInfo) {
        binding.apply {
            tvUserName.text = serviceInfo.gigUser.name
            tvServiceDetailTitle.text = serviceInfo.s_description
            tvServiceDetailDescription.text = serviceInfo.description
            tvInfo.text = serviceInfo.additional_info
            tvDate.visibility = View.GONE
            ratingBar.rating = serviceInfo.average_rating.toFloat()
            Glide.with(this@UserGigDetailsActivity)
                .load("${Constants.BASE_URL}${serviceInfo.gigUser.image}")
                .placeholder(R.drawable.img_profile)
                .error(R.drawable.img_profile)
                .into(ivUser)
            setImageSlider(serviceInfo)
        }
    }

    private fun setImageSlider(serviceInfo: ServiceInfo) {
        serviceInfo.serviceMedia.let { imagesList ->
            if (imagesList.isNotEmpty()) {
                imagesList.forEach { image ->
                    val defaultSliderView = DefaultSliderView(this)
                    defaultSliderView
                        .image(Constants.BASE_URL.plus(image.media))
                        .setRequestOption(requestOptions)
                        .setProgressBarVisible(true)
                        .setOnSliderClickListener(this)
                    glideSliderLayout.addSlider(defaultSliderView)
                }
            }
        }
    }

    private fun initViews() {
        requestOptions = RequestOptions().centerCrop()
        setSliderProperties()
    }

    private fun setSliderProperties() {
        glideSliderLayout = binding.imgSlider.apply {
            setPresetTransformer(SliderLayout.Transformer.Accordion)
            setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom)
            setCustomAnimation(DescriptionAnimation())
            setDuration(4000)
            addOnPageChangeListener(this@UserGigDetailsActivity)
            stopCyclingWhenTouch(false)
        }
    }

    private fun setClickListeners() {
        binding.toolbar.ivToolbar.setOnClickListener {
            finish()
        }
        binding.btnRetry.setOnClickListener {
            executeApi()
        }
    }

    private fun setToolbarData() {
        binding.toolbar.ivToolbar.setImageResource(R.drawable.ic_back)
        binding.toolbar.tvToolbar.text = App.getAppContext()!!.getString(R.string.str_gig_details)
    }

    private fun setStatusBarColor() {
        val window: Window = this.window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorOne)
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

    override fun onSliderClick(slider: BaseSliderView?) {

    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {

    }

    override fun onPageScrollStateChanged(state: Int) {

    }

}