package com.horizam.pro.elean.ui.main.view.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.glide.slider.library.SliderLayout
import com.glide.slider.library.animations.DescriptionAnimation
import com.glide.slider.library.slidertypes.BaseSliderView
import com.glide.slider.library.slidertypes.DefaultSliderView
import com.glide.slider.library.tricks.ViewPagerEx
import com.horizam.pro.elean.App
import com.horizam.pro.elean.Constants
import com.horizam.pro.elean.R
import com.horizam.pro.elean.data.api.ApiHelper
import com.horizam.pro.elean.data.api.RetrofitBuilder
import com.horizam.pro.elean.data.model.response.GigDetailsResponse
import com.horizam.pro.elean.data.model.response.ServiceInfo
import com.horizam.pro.elean.databinding.FragmentLoginBinding
import com.horizam.pro.elean.databinding.FragmentServiceDetailsBinding
import com.horizam.pro.elean.databinding.FragmentSignUpBinding
import com.horizam.pro.elean.ui.base.ViewModelFactory
import com.horizam.pro.elean.ui.main.adapter.ReviewsAdapter
import com.horizam.pro.elean.ui.main.callbacks.GenericHandler
import com.horizam.pro.elean.ui.main.callbacks.OnItemClickListener
import com.horizam.pro.elean.ui.main.viewmodel.GigDetailsViewModel
import com.horizam.pro.elean.utils.Status
import java.lang.Exception


class ServiceDetailsFragment : Fragment(), BaseSliderView.OnSliderClickListener,
    ViewPagerEx.OnPageChangeListener , OnItemClickListener {

    private lateinit var binding: FragmentServiceDetailsBinding
    private lateinit var viewModel: GigDetailsViewModel
    private lateinit var genericHandler: GenericHandler
    private lateinit var glideSliderLayout: SliderLayout
    private lateinit var requestOptions: RequestOptions
    private val args: ServiceDetailsFragmentArgs by navArgs()
    private var service:ServiceInfo? = null
    private lateinit var adapter: ReviewsAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onAttach(context: Context) {
        super.onAttach(context)
        genericHandler = context as GenericHandler
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentServiceDetailsBinding.inflate(layoutInflater,container,false)
        setToolbarData()
        initViews()
        setupViewModel()
        setupObservers()
        setRecyclerview()
        setClickListeners()
        executeApi()
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        executeApi()
    }

    private fun executeApi() {
        /*if (viewModel.gigDetails.value?.data == null) {
            genericHandler.showProgressBar(true)
            viewModel.gigDetailsCall(args.uid)
        }*/
        genericHandler.showProgressBar(true)
        viewModel.gigDetailsCall(args.uid)
    }

    private fun initViews() {
        requestOptions = RequestOptions().centerCrop()
        setSliderProperties()
        adapter = ReviewsAdapter(this)
        recyclerView = binding.rvReviews
    }

    private fun setRecyclerview() {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                recyclerView.context,
                (recyclerView.layoutManager as LinearLayoutManager).orientation
            )
        )
        recyclerView.adapter = adapter
    }

    private fun setSliderProperties() {
        glideSliderLayout = binding.imgSlider.apply {
            setPresetTransformer(SliderLayout.Transformer.Accordion)
            setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom)
            setCustomAnimation(DescriptionAnimation())
            setDuration(4000)
            addOnPageChangeListener(this@ServiceDetailsFragment)
            stopCyclingWhenTouch(false)
        }
    }

    private fun setClickListeners() {
        binding.apply {
            toolbar.ivToolbar.setOnClickListener {
                findNavController().popBackStack()
            }
            btnEditService.setOnClickListener {
                service?.let {
                    ServiceDetailsFragmentDirections.actionServiceDetailsFragmentToUpdateServiceFragment(it).also { navDirections ->
                        findNavController().navigate(navDirections)
                    }
                }
            }
            binding.btnRetry.setOnClickListener {
                executeApi()
            }
        }
    }

    private fun setToolbarData() {
        binding.toolbar.ivToolbar.setImageResource(R.drawable.ic_back)
        binding.toolbar.tvToolbar.text = App.getAppContext()!!.getString(R.string.str_service_details)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(GigDetailsViewModel::class.java)
    }

    private fun setupObservers() {
        viewModel.gigDetails.observe(viewLifecycleOwner, {
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
                        genericHandler.showMessage(it.message.toString())
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

    private fun handleResponse(response: GigDetailsResponse) {
        try {
            setUIData(response.serviceInfo)
        } catch (e: Exception) {
            genericHandler.showMessage(e.message.toString())
        }
    }

    private fun setUIData(serviceInfo: ServiceInfo) {
        binding.apply {
            tvServiceDetailTitle.text = serviceInfo.s_description
            tvServiceDetailDescription.text = serviceInfo.description
            tvPrice.text = Constants.CURRENCY.plus(" ").plus(serviceInfo.price.toString())
            tvDeliveryTime.text = serviceInfo.delivery_time
            ratingBar.rating = serviceInfo.average_rating.toFloat()
            tvRatingValue.text = " (${serviceInfo.reviews.size})"
            tvCategoryTitle.text = serviceInfo.category_title
            tvSubcategoryTitle.text = serviceInfo.sub_category_title
            tvNoOfRevision.text = serviceInfo.noOfRevision.toString()
            btnEditService.isVisible = args.isEditable
            service = serviceInfo
            setImageSlider(serviceInfo)
            if (serviceInfo.reviews.isEmpty()) {
                recyclerView.isVisible = false
                binding.tvPlaceholder.isVisible = true
            } else {
                adapter.submitList(serviceInfo.reviews)
                recyclerView.isVisible = true
                binding.tvPlaceholder.isVisible = false
            }
        }
    }

    private fun setImageSlider(serviceInfo: ServiceInfo) {
        serviceInfo.serviceMedia.let { imagesList ->
            if (imagesList.isNotEmpty()){
                imagesList.forEach { image ->
                    val defaultSliderView = DefaultSliderView(requireContext())
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

    override fun onSliderClick(slider: BaseSliderView?) {
        // Toast.makeText(requireContext(), slider!!.getBundle().getString("extra") + "", Toast.LENGTH_SHORT).show()
    }

    override fun onStop() {
        glideSliderLayout.stopAutoCycle()
        super.onStop()
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {

    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun <T> onItemClick(item: T) {
    }
}