package com.horizam.pro.elean.ui.main.view.fragments

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.horizam.pro.elean.App
import com.horizam.pro.elean.Constants
import com.horizam.pro.elean.R
import com.horizam.pro.elean.data.api.ApiHelper
import com.horizam.pro.elean.data.api.RetrofitBuilder
import com.horizam.pro.elean.data.model.Image
import com.horizam.pro.elean.data.model.SpinnerModel
import com.horizam.pro.elean.data.model.requests.CreateServiceRequest
import com.horizam.pro.elean.data.model.requests.UpdateServiceRequest
import com.horizam.pro.elean.data.model.response.CategoriesDaysResponse
import com.horizam.pro.elean.data.model.response.GeneralResponse
import com.horizam.pro.elean.data.model.response.SpinnerSubcategoriesResponse
import com.horizam.pro.elean.databinding.FragmentUpdateServiceBinding
import com.horizam.pro.elean.ui.base.ViewModelFactory
import com.horizam.pro.elean.ui.main.adapter.ImagesAdapter
import com.horizam.pro.elean.ui.main.adapter.SpinnerAdapter
import com.horizam.pro.elean.ui.main.callbacks.GenericHandler
import com.horizam.pro.elean.ui.main.callbacks.ImagesHandler
import com.horizam.pro.elean.ui.main.viewmodel.CreateServiceViewModel
import com.horizam.pro.elean.utils.*
import com.horizam.pro.elean.utils.BaseUtils.Companion.hideKeyboard
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.lang.Exception


class UpdateServiceFragment : Fragment(), AdapterView.OnItemSelectedListener, ImagesHandler {

    private lateinit var binding: FragmentUpdateServiceBinding
    private lateinit var adapterImages: ImagesAdapter
    private lateinit var imagesArrayList: ArrayList<Image>
    private lateinit var deletedImagesArrayList: ArrayList<String>
    private lateinit var viewModel: CreateServiceViewModel
    private lateinit var genericHandler: GenericHandler
    private lateinit var categoriesArrayList: List<SpinnerModel>
    private lateinit var subcategoriesArrayList: List<SpinnerModel>
    private lateinit var daysArrayList: List<String>
    private lateinit var noOfRevisionArrayList: List<String>
    private lateinit var categoriesAdapter: ArrayAdapter<SpinnerModel>
    private lateinit var subcategoriesAdapter: ArrayAdapter<SpinnerModel>
    private lateinit var daysAdapter: ArrayAdapter<String>
    private lateinit var noOfRevisionAdapter: ArrayAdapter<String>
    private val args: UpdateServiceFragmentArgs by navArgs()
    private var categoryId: Int = -1
    private var subcategoryId: Int = -1
    private var deliveryTime = ""
    private var noOfRevision = -1

    override fun onAttach(context: Context) {
        super.onAttach(context)
        genericHandler = context as GenericHandler
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUpdateServiceBinding.inflate(layoutInflater, container, false)
        setToolbarData()
        initViews()
        setupViewModel()
        setupObservers()
        setRecyclerView()
        setClickListeners()
        setData()
        return binding.root
    }

    private fun setData() {
        try {
            val service = args.service
            binding.apply {
                etShortDes.setText(service.s_description)
                etDescription.setText(service.description)
                etPrice.setText(service.price.toString())
                etInfo.setText(service.additional_info)
                service.serviceMedia.forEach { media ->
                    val image = Image(id = 1, path = media.media)
                    imagesArrayList.add(image)
                    adapterImages.addImages(imagesArrayList)
                }
            }
        } catch (exception: Exception) {
            genericHandler.showMessage(exception.message.toString())
        }
    }

    private fun setClickListeners() {
        binding.apply {
            toolbar.ivToolbar.setOnClickListener {
                findNavController().popBackStack()
            }
            btnPublish.setOnClickListener {
                hideKeyboard()
                validateData()
            }
        }
    }

    private fun validateData() {
        binding.apply {
            when {
                etShortDes.editableText.trim().isEmpty() -> {
                    genericHandler.showMessage(getString(R.string.str_enter_valid_short_description))
                    return
                }
                categoryId == -1 -> {
                    genericHandler.showMessage(getString(R.string.str_enter_valid_category))
                    return
                }
                subcategoryId == -1 -> {
                    genericHandler.showMessage(getString(R.string.str_enter_valid_subcategory))
                    return
                }
                etDescription.editableText.trim().isEmpty() -> {
                    genericHandler.showMessage(getString(R.string.str_enter_valid_description))
                    return
                }
                etPrice.editableText.trim().isEmpty() -> {
                    genericHandler.showMessage(getString(R.string.str_enter_valid_price))
                    return
                }
                deliveryTime.isEmpty() -> {
                    genericHandler.showMessage(getString(R.string.str_enter_valid_delivery_time))
                    return
                }
                noOfRevision == -1 ->{
                    genericHandler.showMessage(getString(R.string.str_enter_a_valid_no_of_Revision))
                    return
                }
                imagesArrayList.isEmpty() -> {
                    genericHandler.showMessage(getString(R.string.str_enter_valid_images))
                    return
                }
                etInfo.editableText.trim().isEmpty() -> {
                    genericHandler.showMessage(getString(R.string.str_enter_valid_info))
                    return
                }
                else -> {
                    createMultipartData()
                }
            }
        }
    }

    private fun createMultipartData() {
        lifecycleScope.launch {
            val images: ArrayList<MultipartBody.Part> = ArrayList()
            imagesArrayList.forEach {
                if (it.id == 0) {
                    images.add(
                        BaseUtils.compressAndCreateImageData(
                            it.path,
                            "banner[]",
                            requireContext()
                        )
                    )
                }
            }
            val deletedImages: ArrayList<String> = ArrayList()
            deletedImagesArrayList.forEach {
                deletedImages.add(it)
            }
            val map: HashMap<String, RequestBody> = HashMap()
            map["description"] =
                BaseUtils.createRequestBodyFromString(binding.etDescription.text.toString().trim())
            map["s_description"] =
                BaseUtils.createRequestBodyFromString(binding.etShortDes.text.toString().trim())
            map["price"] =
                BaseUtils.createRequestBodyFromString(binding.etPrice.text.toString().trim())
            map["_method"] = BaseUtils.createRequestBodyFromString("PUT")
            map["additional_info"] =
                BaseUtils.createRequestBodyFromString(binding.etInfo.text.toString().trim())
            map["delivery_time"] = BaseUtils.createRequestBodyFromString(deliveryTime)
            map["revision"] = BaseUtils.createRequestBodyFromString(noOfRevision.toString())
            map["sub_category_id"] = BaseUtils.createRequestBodyFromString(subcategoryId.toString())
            map["category_id"] = BaseUtils.createRequestBodyFromString(categoryId.toString())
            val prefManager = PrefManager(requireContext())
            if (prefManager.location != null) {
                val location = prefManager.location!!
                map["lat"] = BaseUtils.createRequestBodyFromString(location.lat.toString())
                map["lng"] = BaseUtils.createRequestBodyFromString(location.long.toString())
            }
            exeApi(images, map, deletedImages)
        }
    }

    private fun exeApi(
        images: ArrayList<MultipartBody.Part>,
        map: java.util.HashMap<String, RequestBody>,
        deletedImages: ArrayList<String>
    ) {
        genericHandler.showProgressBar(true)
        viewModel.updateServiceCall(
            UpdateServiceRequest(
                partMap = map,
                images = images,
                deletedImages = deletedImages,
                id = args.service.id
            )
        )
    }

    private fun initViews() {
        imagesArrayList = ArrayList()
        deletedImagesArrayList = ArrayList()
        adapterImages = ImagesAdapter(this)
        categoriesArrayList = ArrayList()
        subcategoriesArrayList = ArrayList()
        daysArrayList = ArrayList()
        noOfRevisionArrayList = ArrayList()
        binding.spinnerCategory.onItemSelectedListener = this
        binding.spinnerSubCategory.onItemSelectedListener = this
        binding.spinnerDeliveryTime.onItemSelectedListener = this
        binding.spinnerNoOfRevision.onItemSelectedListener = this
    }

    private fun setRecyclerView() {
        binding.rvImages.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = adapterImages
        }
    }

    private fun setToolbarData() {
        binding.toolbar.ivToolbar.setImageResource(R.drawable.ic_back)
        binding.toolbar.tvToolbar.text =
            App.getAppContext()!!.getString(R.string.str_update_service)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(CreateServiceViewModel::class.java)
    }

    private fun setupObservers() {
        viewModel.categoriesDays.observe(viewLifecycleOwner, {
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
                        genericHandler.showMessage(it.message.toString())
                    }
                    Status.LOADING -> {
                        genericHandler.showProgressBar(true)
                    }
                }
            }
        })
        viewModel.spinnerSubcategories.observe(viewLifecycleOwner, subcategoriesObserver)
        viewModel.updateService.observe(viewLifecycleOwner, updateServiceObserver)
    }

    private fun <T> handleResponse(response: T) {
        try {
            when (response) {
                is CategoriesDaysResponse -> {
                    setUIData(response)
                }
                is SpinnerSubcategoriesResponse -> {
                    setSpinnerSubcategories(response)
                }
                is GeneralResponse -> {
                    genericHandler.showMessage(response.message)
                    if (response.status == Constants.STATUS_OK) {
                        findNavController().popBackStack()
                    }
                }
            }
        } catch (e: Exception) {
            genericHandler.showMessage(e.message.toString())
        }
    }

    private fun setSpinnerSubcategories(response: SpinnerSubcategoriesResponse) {
        subcategoriesArrayList = response.subcategories.map { spinnerSubcategories ->
            SpinnerModel(id = spinnerSubcategories.id, value = spinnerSubcategories.title)
        }
        val selectedSubCategoryPosition: Int = subcategoriesArrayList.indexOfFirst {
            it.id == args.service.subcategoryId
        }
        subcategoriesAdapter = SpinnerAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item, subcategoriesArrayList
        ).also {
            it.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            binding.spinnerSubCategory.adapter = it
            binding.spinnerSubCategory.setSelection(selectedSubCategoryPosition)
        }
    }

    private fun setUIData(response: CategoriesDaysResponse) {
        categoriesArrayList = response.categories.map { spinnerCategories ->
            SpinnerModel(id = spinnerCategories.id, value = spinnerCategories.title)
        }
        val selectedCategoryPosition: Int = categoriesArrayList.indexOfFirst {
            it.id == args.service.categoryId
        }
        daysArrayList = response.days
        val selectedDaysPosition: Int = daysArrayList.indexOfFirst {
            it == args.service.delivery_time
        }

        noOfRevisionArrayList = response.noOfRevision
        val selectedRevisionPosition: Int = noOfRevisionArrayList.indexOfFirst {
            it.toInt() == args.service.noOfRevision
        }

        categoriesAdapter = SpinnerAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item, categoriesArrayList
        ).also {
            it.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            binding.spinnerCategory.adapter = it
            binding.spinnerCategory.setSelection(selectedCategoryPosition)
        }
        daysAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item, daysArrayList
        ).also {
            it.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            binding.spinnerDeliveryTime.adapter = it
            binding.spinnerDeliveryTime.setSelection(selectedDaysPosition)
        }

        noOfRevisionAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item, noOfRevisionArrayList
        ).also {
            it.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            binding.spinnerNoOfRevision.adapter = it
            binding.spinnerNoOfRevision.setSelection(selectedRevisionPosition)
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (parent!!.id) {
            binding.spinnerCategory.id -> {
                val spinnerModel = parent.selectedItem as SpinnerModel
                categoryId = spinnerModel.id
                viewModel.spinnerSubcategoriesCall(spinnerModel.id)
            }
            binding.spinnerSubCategory.id -> {
                val spinnerModel = parent.selectedItem as SpinnerModel
                subcategoryId = spinnerModel.id
            }
            binding.spinnerDeliveryTime.id -> {
                deliveryTime = parent.selectedItem.toString()
            }
            binding.spinnerNoOfRevision.id ->{
                noOfRevision = parent.selectedItem.toString().toInt()
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    private val subcategoriesObserver = Observer<Resource<SpinnerSubcategoriesResponse>> {
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
                    genericHandler.showMessage(it.message.toString())
                }
                Status.LOADING -> {
                    genericHandler.showProgressBar(true)
                }
            }
        }
    }

    private val updateServiceObserver = Observer<Resource<GeneralResponse>> {
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
                    genericHandler.showMessage(it.message.toString())
                }
                Status.LOADING -> {
                    genericHandler.showProgressBar(true)
                }
            }
        }
    }

    override fun addImages() {
        onClickRequestPermission()
    }

    override fun <T> removeImage(item: T) {
        if (item is Image) {
            val index = imagesArrayList.indexOf(item)
            imagesArrayList.remove(item)
            adapterImages.notifyItemRemoved(index)
            deletedImagesArrayList.add(item.path)
        }
    }

    private fun onClickRequestPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                val imageIntent = Intent().apply {
                    type = "image/*"
                    putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                    action = Intent.ACTION_GET_CONTENT
                }
                resultLauncher.launch(imageIntent)
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) -> {
                showSnackbar(
                    requireView(),
                    getString(R.string.permission_required),
                    Snackbar.LENGTH_INDEFINITE,
                    getString(R.string.str_ok)
                ) {
                    requestPermissionLauncher.launch(
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                }
            }
            else -> {
                requestPermissionLauncher.launch(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            }
        }
    }

    fun showSnackbar(
        view: View, msg: String, length: Int, actionMessage: CharSequence?,
        action: (View) -> Unit
    ) {
        val snackbar = Snackbar.make(view, msg, length)
        if (actionMessage != null) {
            snackbar.setAction(actionMessage) {
                action(requireActivity().findViewById(android.R.id.content))
            }.show()
        } else {
            snackbar.show()
        }
    }

    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (result.data != null) {
                    handlePickerResult(result.data!!)
                } else {
                    genericHandler.showMessage("Invalid data")
                }
            }
        }

    private fun handlePickerResult(data: Intent) {
        // if multiple images are selected
        if (data.clipData != null) {
            val count = data.clipData!!.itemCount
            for (i in 0 until count) {
                val imageUri: Uri = data.clipData!!.getItemAt(i).uri
                addImagesToList(imageUri)
            }
        } else if (data.data != null) {
            // if single image is selected
            val imageUri: Uri = data.data!!
            addImagesToList(imageUri)
        }
    }

    private fun addImagesToList(imageUri: Uri) {
        imageUri.let {
            val uriPathHelper = URIPathHelper()
            val imagePath = uriPathHelper.getPath(requireContext(), it)
            if (!imagePath.isNullOrEmpty()) {
                val image = Image(id = 0, path = imagePath)
                imagesArrayList.add(image)
                adapterImages.addImages(imagesArrayList)
            } else {
                genericHandler.showMessage("Choose valid images")
            }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Log.i("Permission: ", "Granted")
                val imageIntent = Intent().apply {
                    type = "image/*"
                    putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                    action = Intent.ACTION_GET_CONTENT
                }
                resultLauncher.launch(imageIntent)
            } else {
                Log.i("Permission: ", "Denied")
                genericHandler.showMessage(
                    getString(R.string.permission_required)
                        .plus(". Please enable it settings")
                )
            }
        }
}