package com.horizam.pro.elean.ui.main.view.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
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
import com.google.gson.Gson
import com.horizam.pro.elean.App
import com.horizam.pro.elean.Constants
import com.horizam.pro.elean.R
import com.horizam.pro.elean.data.api.ApiHelper
import com.horizam.pro.elean.data.api.RetrofitBuilder
import com.horizam.pro.elean.data.model.MessageGig
import com.horizam.pro.elean.data.model.requests.ReviewsRequest
import com.horizam.pro.elean.data.model.response.ServiceDetail
import com.horizam.pro.elean.databinding.FragmentGigDetailsBinding
import com.horizam.pro.elean.ui.base.ViewModelFactory
import com.horizam.pro.elean.ui.main.adapter.ReviewsAdapter
import com.horizam.pro.elean.ui.main.callbacks.GenericHandler
import com.horizam.pro.elean.ui.main.callbacks.OnItemClickListener
import com.horizam.pro.elean.ui.main.view.activities.AuthenticationActivity
import com.horizam.pro.elean.ui.main.viewmodel.GigDetailsViewModel
import com.horizam.pro.elean.utils.PrefManager
import com.horizam.pro.elean.utils.Status


@Suppress("DEPRECATION")
class GigDetailsFragment : Fragment(), OnItemClickListener,
    BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {
    private val navController: NavController by lazy {
        this.findNavController()
    }
    private lateinit var binding: FragmentGigDetailsBinding
    private lateinit var adapter: ReviewsAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: GigDetailsViewModel
    private lateinit var genericHandler: GenericHandler
    private lateinit var glideSliderLayout: SliderLayout
    private lateinit var requestOptions: RequestOptions
    private lateinit var deliveryDaysList: ArrayList<String>
    private lateinit var prefManager: PrefManager
    private var gig: ServiceDetail? = null
    private lateinit var serviceGigsFragment:ServiceGigsFragment
    private val args: GigDetailsFragmentArgs by navArgs()
    private var userId: String = ""
    private val bundle = Bundle()



    override fun onAttach(context: Context) {
        super.onAttach(context)
        genericHandler = context as GenericHandler
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentGigDetailsBinding.inflate(layoutInflater, container, false)
        setToolbarData()
        initViews()
//        inData()
        setupViewModel()
        setupObservers()
        setRecyclerview()
        setOnClickListeners()
        executeApi()
        return binding.root
    }

//    private fun inData() {
//        val gson = Gson()
//        val serviceDetail = gson.fromJson(args.serviceDetail, ServiceDetail::class.java)
//        serviceId = bundle.get(Constants.ORDER_ID).toString()
//        if(serviceId!=null)
//        {
//            exeGetReviews(serviceDetail!!.id)
//        }
//    }

    private fun executeApi() {
        val gson = Gson()
        val serviceDetail = gson.fromJson(args.serviceDetail, ServiceDetail::class.java)
//        viewModel.addClickGigs(serviceDetail.id)
        exeGetReviews(serviceDetail!!.id)
        setData(serviceDetail)
    }
    private fun initViews() {
        prefManager = PrefManager(requireContext())
        deliveryDaysList = ArrayList()
        serviceGigsFragment= ServiceGigsFragment()
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
        recyclerView = binding.rvReviews
        adapter = ReviewsAdapter(this)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                recyclerView.context,
                (recyclerView.layoutManager as LinearLayoutManager).orientation
            )
        )
        recyclerView.adapter = adapter
    }

    private fun setOnClickListeners() {
        binding.toolbar.ivToolbar.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.ivUser.setOnClickListener {
//            if (userId == "") {
//                return@setOnClickListener
//            }
//            Intent(requireActivity(), UserAboutActivity::class.java).also {
//                it.putExtra("id", userId)
//                startActivity(it)
//            }
        }
        binding.btnRetry.setOnClickListener {
            executeApi()
        }
        binding.btnPurchase.setOnClickListener {
            if (prefManager.accessToken.isEmpty()) {
                var intent = Intent(activity, AuthenticationActivity::class.java)
                startActivity(intent)
            } else {
                val serviceId: String = gig!!.id
                if (serviceId.isNotEmpty()) {
                    val customOrderBottomSheet = CustomOrderBottomSheet()
                    bundle.putString(Constants.SERVICE_ID, serviceId)
                    bundle.putStringArrayList(Constants.DAYS_LIST, deliveryDaysList)
                    customOrderBottomSheet.arguments = bundle
                    customOrderBottomSheet.show(
                        requireActivity().supportFragmentManager,
                        CustomOrderBottomSheet.TAG
                    )
                }
            }
        }
        binding.btnContactSeller.setOnClickListener {
            if (prefManager.accessToken.isEmpty()) {
                var intent = Intent(activity, AuthenticationActivity::class.java)
                startActivity(intent)
            } else {
                try {
                    if (prefManager.userId != userId && userId != "" && gig != null) {
                        val serviceGig = gig!!
                        val messageGig = MessageGig(
                            gigId = serviceGig.id,
                            gigImage = serviceGig.service_media[0].media,
                            gigTitle = serviceGig.s_description,
                            gigUsername = serviceGig.service_user.name
                        )
                        GigDetailsFragmentDirections.actionGigDetailsFragmentToMessagesFragment(
                            userName = serviceGig.service_user.name,
                            photo = serviceGig.service_user.image,
                            id = userId,
                            refersGig = true,
                            messageGig = messageGig
                        ).also {
                            findNavController().navigate(it)
                        }
                    }
                } catch (e: Exception) {
                    genericHandler.showErrorMessage(e.message.toString())
                }
            }
        }
    }

    private fun setToolbarData() {
        binding.toolbar.ivToolbar.setImageResource(R.drawable.ic_back)
        binding.toolbar.ivToolbar.isVisible=true
        binding.toolbar.tvToolbar.text =getString(R.string.str_gig_details)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(GigDetailsViewModel::class.java)
    }

    private fun exeGetReviews(id: String) {
        val reviewsRequest = ReviewsRequest(id)
        if(reviewsRequest!=null)
        {
            recyclerView.isVisible = true
            binding.tvPlaceholder.isVisible = false
            viewModel.getReviews(reviewsRequest)
        } else {
             recyclerView.isVisible = false
            binding.tvPlaceholder.isVisible = true
        }


    }
    private fun setupObservers() {
        viewModel.gigDetails.observe(viewLifecycleOwner) {
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
                        genericHandler.showErrorMessage(it.message.toString())
                        changeViewVisibility(textView = true, button = true, layout = false)
                    }
                    Status.LOADING -> {
                        genericHandler.showProgressBar(true)
                        changeViewVisibility(textView = false, button = false, layout = false)
                    }
                }
            }
        }

        viewModel.clickGigs.observe(viewLifecycleOwner) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        genericHandler.showProgressBar(false)
                        resource.data?.let { response ->
//                            genericHandler.showSuccessMessage(response.message.toString())
                            changeViewVisibility(textView = false, button = false, layout = true)
                        }
                    }
                    Status.ERROR -> {
                        genericHandler.showProgressBar(false)
                        genericHandler.showErrorMessage(it.message.toString())
                        changeViewVisibility(textView = true, button = true, layout = false)
                    }
                    Status.LOADING -> {
                        genericHandler.showProgressBar(true)
                        changeViewVisibility(textView = false, button = false, layout = false)
                    }
                }
            }
        }
            viewModel.reviewList.observe(viewLifecycleOwner) {
                adapter.submitData(viewLifecycleOwner.lifecycle, it)

            }
    }

    private fun changeViewVisibility(textView: Boolean, button: Boolean, layout: Boolean) {
        binding.textViewError.isVisible = textView
        binding.btnRetry.isVisible = button
        binding.mainLayout.isVisible = layout
    }

    private fun setData(serviceDetail: ServiceDetail) {
        try {
            changeViewVisibility(textView = false, button = false, layout = true)
            setUIData(serviceDetail)
            gig = serviceDetail
            bundle.putString("service_name", serviceDetail.s_description)
            bundle.putString("seller_name", serviceDetail.service_user.name)
            bundle.putString("seller_username", serviceDetail.service_user.username)
            bundle.putString("price", serviceDetail.price.toString())
        } catch (e: Exception) {
            genericHandler.showErrorMessage(e.message.toString())
        }
    }

    private fun setUIData(serviceDetail: ServiceDetail) {
        binding.apply {
            if(serviceDetail.service_user.name.isNullOrBlank())
            {
                tvUserName.text = serviceDetail.service_user.username
            }
            else {
                tvUserName.text = serviceDetail.service_user.name
            }
            tvCategoryPrice.text = Constants.CURRENCY.plus(" ").plus(serviceDetail.price.toString())
            tvServiceDetailTitle.text = serviceDetail.s_description
            if(prefManager.setLanguage=="0")
            {
                tvCategoryName.text = serviceDetail.category.title
                tvSubcategoryName.text = serviceDetail.sub_category.title
            }
            else {
                tvCategoryName.text = serviceDetail.category.fiTitle
                tvSubcategoryName.text = serviceDetail.sub_category.fiTitle
            }
            tvServiceDetailDescription.setText(Html.fromHtml(Html.fromHtml(serviceDetail.description).toString()))
            tvNoOfRevision.text = serviceDetail.revision.toString()
            ratingBar.rating = serviceDetail.service_rating.toFloat()
            noOfRating.text = "(${serviceDetail.total_reviews})"
            userId = serviceDetail.service_user.id
            if (serviceDetail.service_media.size > 0) {
                Glide.with(this@GigDetailsFragment)
                    .load("${Constants.BASE_URL}${serviceDetail.service_user.image}")
                    .placeholder(R.drawable.img_profile)
                    .error(R.drawable.img_profile)
                    .into(ivUser)
                setImageSlider(serviceDetail)
            }
        }
//        if (serviceDetail.service_rating.isEmpty()) {
//            recyclerView.isVisible = false
//            binding.tvPlaceholder.isVisible = true
//        } else {
//            adapter.submitList(serviceDetail.serviceReviewsList)
//            recyclerView.isVisible = true
//            binding.tvPlaceholder.isVisible = false
//        }
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