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
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.glide.slider.library.SliderLayout
import com.glide.slider.library.animations.DescriptionAnimation
import com.glide.slider.library.slidertypes.BaseSliderView
import com.glide.slider.library.slidertypes.DefaultSliderView
import com.glide.slider.library.tricks.ViewPagerEx
import com.google.gson.Gson
import com.horizam.pro.elean.App
import com.horizam.pro.elean.Constants
import com.horizam.pro.elean.R
import com.horizam.pro.elean.data.api.ApiHelper
import com.horizam.pro.elean.data.api.RetrofitBuilder
import com.horizam.pro.elean.data.model.requests.ReviewsRequest
import com.horizam.pro.elean.data.model.response.ServiceDetail
import com.horizam.pro.elean.databinding.FragmentServiceDetailsBinding
import com.horizam.pro.elean.ui.base.ViewModelFactory
import com.horizam.pro.elean.ui.main.adapter.ReviewsAdapter
import com.horizam.pro.elean.ui.main.callbacks.GenericHandler
import com.horizam.pro.elean.ui.main.callbacks.OnItemClickListener
import com.horizam.pro.elean.ui.main.viewmodel.GigDetailsViewModel
import java.lang.Exception


class ServiceDetailsFragment : Fragment(), BaseSliderView.OnSliderClickListener,
    ViewPagerEx.OnPageChangeListener, OnItemClickListener {

    private lateinit var binding: FragmentServiceDetailsBinding
    private lateinit var viewModel: GigDetailsViewModel
    private lateinit var genericHandler: GenericHandler
    private lateinit var glideSliderLayout: SliderLayout
    private lateinit var requestOptions: RequestOptions
    private val args: ServiceDetailsFragmentArgs by navArgs()
    private var service: ServiceDetail? = null
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
        binding = FragmentServiceDetailsBinding.inflate(layoutInflater, container, false)
        setToolbarData()
        initViews()
        setupViewModel()
        setUpObserver()
        setRecyclerview()
        setClickListeners()
        return binding.root
    }

    private fun setUpObserver() {
        viewModel.reviewList.observe(viewLifecycleOwner) {
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
        }
    }

    private fun exeGetReviews(id: String) {
        val reviewsRequest = ReviewsRequest(id)
        viewModel.getReviews(reviewsRequest)
    }

    override fun onStart() {
        super.onStart()
        changeViewVisibility(textView = false, button = false, layout = true)
        executeApi()
    }

    private fun executeApi() {
        val gson = Gson()
        val serviceDetail = gson.fromJson(args.serviceDetail, ServiceDetail::class.java)
        handleResponse(serviceDetail)
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
                    ServiceDetailsFragmentDirections.actionServiceDetailsFragmentToUpdateServiceFragment(
                        it
                    ).also { navDirections ->
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
        binding.toolbar.tvToolbar.text =
            App.getAppContext()!!.getString(R.string.str_service_details)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(GigDetailsViewModel::class.java)
    }

    private fun changeViewVisibility(textView: Boolean, button: Boolean, layout: Boolean) {
        binding.textViewError.isVisible = textView
        binding.btnRetry.isVisible = button
        binding.mainLayout.isVisible = layout
    }

    private fun handleResponse(serviceDetail: ServiceDetail) {
        try {
            setUIData(serviceDetail)
        } catch (e: Exception) {
            genericHandler.showErrorMessage(e.message.toString())
        }
    }

    private fun setUIData(serviceDetail: ServiceDetail) {
        binding.apply {
            tvServiceDetailTitle.text = serviceDetail.s_description
            tvServiceDetailDescription.text = serviceDetail.description
            tvPrice.text = Constants.CURRENCY.plus(" ").plus(serviceDetail.price.toString())
            tvDeliveryTime.text = serviceDetail.delivery_time
            ratingBar.rating = serviceDetail.service_rating.toFloat()
            tvRatingValue.text = " (${serviceDetail.total_reviews})"
            tvCategoryTitle.text = serviceDetail.category.title
            tvSubcategoryTitle.text = serviceDetail.sub_category.title
            tvNoOfRevision.text = serviceDetail.revision.toString()
            btnEditService.isVisible = args.isEditable
            service = serviceDetail
            exeGetReviews(service!!.id)
            setImageSlider(serviceDetail)
//            if (serviceDetail.serviceReviewsList.isEmpty()) {
//                recyclerView.isVisible = false
//                binding.tvPlaceholder.isVisible = true
//            } else {
//                adapter.submitList(serviceDetail.serviceReviewsList)
//                recyclerView.isVisible = true
//                binding.tvPlaceholder.isVisible = false
//            }
        }
    }

    private fun setImageSlider(serviceDetail: ServiceDetail) {
        serviceDetail.service_media.let { imagesList ->
            if (imagesList.isNotEmpty()) {
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