package com.horizam.pro.elean.ui.main.view.fragments

import android.content.Context
import android.content.Intent
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
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.glide.slider.library.SliderLayout
import com.glide.slider.library.slidertypes.BaseSliderView
import com.glide.slider.library.slidertypes.DefaultSliderView
import com.horizam.pro.elean.App
import com.horizam.pro.elean.Constants
import com.horizam.pro.elean.R
import com.horizam.pro.elean.data.api.ApiHelper
import com.horizam.pro.elean.data.api.RetrofitBuilder
import com.horizam.pro.elean.data.model.response.ServiceInfo
import com.horizam.pro.elean.databinding.FragmentGigDetailsBinding
import com.horizam.pro.elean.ui.base.ViewModelFactory
import com.horizam.pro.elean.ui.main.callbacks.GenericHandler
import com.horizam.pro.elean.ui.main.callbacks.OnItemClickListener
import com.horizam.pro.elean.ui.main.view.activities.UserAboutActivity
import com.horizam.pro.elean.ui.main.viewmodel.GigDetailsViewModel
import com.horizam.pro.elean.utils.Status
import java.lang.Exception
import com.glide.slider.library.animations.DescriptionAnimation
import com.glide.slider.library.tricks.ViewPagerEx
import com.google.gson.Gson
import com.horizam.pro.elean.data.model.response.ServiceDetail
import com.horizam.pro.elean.utils.PrefManager


class GigDetailsFragment : Fragment(), OnItemClickListener,
    BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {

    private lateinit var binding: FragmentGigDetailsBinding

    //    private lateinit var adapter: ReviewsAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: GigDetailsViewModel
    private lateinit var genericHandler: GenericHandler
    private lateinit var glideSliderLayout: SliderLayout
    private lateinit var requestOptions: RequestOptions
    private lateinit var deliveryDaysList: ArrayList<String>
    private lateinit var prefManager: PrefManager
    private var gig: ServiceDetail? = null
    private val args: GigDetailsFragmentArgs by navArgs()
    private var userId: String = ""

    override fun onAttach(context: Context) {
        super.onAttach(context)
        genericHandler = context as GenericHandler
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGigDetailsBinding.inflate(layoutInflater, container, false)
        setToolbarData()
        initViews()
        setupViewModel()
        setupObservers()
        setRecyclerview()
        setOnClickListeners()
        executeApi()
        return binding.root
    }

    private fun executeApi() {
//        if (viewModel.gigDetails.value?.data == null) {
//            genericHandler.showProgressBar(true)
//            viewModel.gigDetailsCall(args.serviceDetail)
//        }
        val gson = Gson()
        val serviceData = gson.fromJson(args.serviceDetail, ServiceDetail::class.java)
        handleResponse(serviceData)
    }

    private fun initViews() {
        prefManager = PrefManager(requireContext())
        deliveryDaysList = ArrayList()
//        adapter = ReviewsAdapter(this)
        recyclerView = binding.rvReviews
        requestOptions = RequestOptions().centerCrop()
        setSliderProperties()
    }

    private fun setSliderProperties() {
        glideSliderLayout = binding.imgSlider.apply {
            setPresetTransformer(SliderLayout.Transformer.Accordion)
            setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom)
            setCustomAnimation(DescriptionAnimation())
            setDuration(4000)
            addOnPageChangeListener(this@GigDetailsFragment)
            stopCyclingWhenTouch(false)
        }
    }

    private fun setRecyclerview() {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                recyclerView.context,
                (recyclerView.layoutManager as LinearLayoutManager).orientation
            )
        )
//        recyclerView.adapter = adapter
    }

    private fun setOnClickListeners() {
        binding.toolbar.ivToolbar.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.ivUser.setOnClickListener {
            if (userId == "") {
                return@setOnClickListener
            }
            Intent(requireActivity(), UserAboutActivity::class.java).also {
                it.putExtra("id", userId)
                startActivity(it)
            }
        }
        binding.btnRetry.setOnClickListener {
            executeApi()
        }
        binding.btnCustomOrder.setOnClickListener {
//            val serviceId: String = args.uid
//            if (serviceId.isNotEmpty()){
//                val customOrderBottomSheet = CustomOrderBottomSheet()
//                bundle.putString(Constants.SERVICE_ID,serviceId)
//                bundle.putStringArrayList(Constants.DAYS_LIST,deliveryDaysList)
//                customOrderBottomSheet.arguments = bundle
//                customOrderBottomSheet.show(requireActivity().supportFragmentManager, CustomOrderBottomSheet.TAG)
//            }
        }
        binding.btnContactSeller.setOnClickListener {
//            try {
//                if (prefManager.userId != userId && userId != "" && gig != null){
//                    val serviceGig = gig!!
//                    val messageGig = MessageGig(
//                        gigId = serviceGig.id,
//                        gigImage = serviceGig.serviceMedia[0].media,
//                        gigTitle = serviceGig.s_description,
//                        gigUsername = serviceGig.gigUser.name
//                    )
//                    GigDetailsFragmentDirections.actionGigDetailsFragmentToMessagesFragment(
//                        id = userId,
//                        refersGig = true,
//                        messageGig = messageGig
//                    ).also {
//                        findNavController().navigate(it)
//                    }
//                }
//            }catch (e:Exception){
//                genericHandler.showMessage(e.message.toString())
//            }
        }
    }

    private fun setToolbarData() {
        binding.toolbar.ivToolbar.setImageResource(R.drawable.ic_back)
        binding.toolbar.tvToolbar.text = App.getAppContext()!!.getString(R.string.str_gig_details)
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
//                            handleResponse(response)
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

    private fun handleResponse(response: ServiceDetail) {
        try {
            changeViewVisibility(textView = false, button = false, layout = true)
            setUIData(response)
            gig = response
//            if (response.days != null){
//                deliveryDaysList = response.days as ArrayList<String>
//                bundle.putString("service_name" , response.serviceInfo.s_description)
//                bundle.putString("seller_name" , response.serviceInfo.gigUser.name)
//                bundle.putString("price" , response.serviceInfo.price.toString())
//            }
        } catch (e: Exception) {
            genericHandler.showMessage(e.message.toString())
        }
    }

    private fun setUIData(serviceDetail: ServiceDetail) {
        binding.apply {
            tvUserName.text = serviceDetail.service_user.name
            tvCategoryPrice.text = Constants.CURRENCY.plus(" ").plus(serviceDetail.price.toString())
            tvServiceDetailTitle.text = serviceDetail.s_description
            tvCategoryName.text = serviceDetail.category.title
            tvSubcategoryName.text = serviceDetail.sub_category.title
            tvServiceDetailDescription.text = serviceDetail.description
            tvNoOfRevision.text = serviceDetail.revision.toString()
            ratingBar.rating = serviceDetail.service_rating.toFloat()
            noOfRating.text = "(${serviceDetail.total_reviews})"
            userId = serviceDetail.user_id
            Glide.with(this@GigDetailsFragment)
                .load("${Constants.BASE_URL}${serviceDetail.service_media[0].media}")
                .placeholder(R.drawable.img_profile)
                .error(R.drawable.img_profile)
                .into(ivUser)
//            setImageSlider(serviceInfo)
        }
        if (serviceDetail.service_reviews.isEmpty()) {
            recyclerView.isVisible = false
            binding.tvPlaceholder.isVisible = true
        } else {
//            adapter.submitList(serviceData.service_reviews)
//            recyclerView.isVisible = true
//            binding.tvPlaceholder.isVisible = false
        }
    }

    private fun setImageSlider(serviceInfo: ServiceInfo) {
        serviceInfo.serviceMedia.let { imagesList ->
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

    override fun <T> onItemClick(item: T) {

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

}