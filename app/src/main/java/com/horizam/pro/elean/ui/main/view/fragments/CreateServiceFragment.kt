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
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.horizam.pro.elean.Constants
import com.horizam.pro.elean.R
import com.horizam.pro.elean.data.api.ApiHelper
import com.horizam.pro.elean.data.api.RetrofitBuilder
import com.horizam.pro.elean.data.model.Image
import com.horizam.pro.elean.data.model.SpinnerModel
import com.horizam.pro.elean.data.model.requests.CreateServiceRequest
import com.horizam.pro.elean.data.model.response.*
import com.horizam.pro.elean.databinding.FragmentCreateServiceBinding
import com.horizam.pro.elean.ui.base.ViewModelFactory
import com.horizam.pro.elean.ui.main.adapter.ImagesAdapter
import com.horizam.pro.elean.ui.main.adapter.SpinnerAdapter
import com.horizam.pro.elean.ui.main.callbacks.GenericHandler
import com.horizam.pro.elean.ui.main.callbacks.ImagesHandler
import com.horizam.pro.elean.ui.main.viewmodel.ManageServicesViewModel
import com.horizam.pro.elean.utils.*
import com.horizam.pro.elean.utils.BaseUtils.Companion.hideKeyboard
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.lang.Exception


class CreateServiceFragment : Fragment(), AdapterView.OnItemSelectedListener, ImagesHandler {

    private lateinit var binding: FragmentCreateServiceBinding
    private lateinit var adapterImages: ImagesAdapter
    private lateinit var imagesArrayList: ArrayList<Image>
    private lateinit var viewModel: ManageServicesViewModel
    private lateinit var genericHandler: GenericHandler
    private lateinit var categoriesArrayList: List<SpinnerModel>
    private lateinit var subcategoriesArrayList: List<SpinnerModel>
    private lateinit var daysArrayList: List<String>
    private lateinit var noOfRevisionArrayList: List<String>
    private lateinit var categoriesAdapter: ArrayAdapter<SpinnerModel>
    private lateinit var subcategoriesAdapter: ArrayAdapter<SpinnerModel>
    private lateinit var daysAdapter: ArrayAdapter<String>
    private lateinit var noOfRevisionAdapter: ArrayAdapter<String>
    private var categoryId: String = ""
    private var subcategoryId: String = ""
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
        binding = FragmentCreateServiceBinding.inflate(layoutInflater, container, false)
        setToolbarData()
        initViews()
        setupViewModel()
        setupObservers()
        setRecyclerView()
        setClickListeners()
        return binding.root
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
                    genericHandler.showErrorMessage(getString(R.string.str_enter_valid_short_description))
                    return
                }
                etShortDes.editableText.trim().length < 15 -> {
                    genericHandler.showErrorMessage(getString(R.string.str_enter_valid_short_description_length))
                    return
                }
                categoryId == "" -> {
                    genericHandler.showErrorMessage(getString(R.string.str_enter_valid_category))
                    return
                }
                subcategoryId == "" -> {
                    genericHandler.showErrorMessage(getString(R.string.str_enter_valid_subcategory))
                    return
                }
                etDescription.editableText.trim().isEmpty() -> {
                    genericHandler.showErrorMessage(getString(R.string.str_enter_valid_description))
                    return
                }
                etDescription.editableText.trim().length < 20 -> {
                    genericHandler.showErrorMessage(getString(R.string.str_enter_valid_description_length))
                    return
                }
                etPrice.editableText.trim().isEmpty() -> {
                    genericHandler.showErrorMessage(getString(R.string.str_enter_valid_price))
                    return
                }
                etPrice.text.toString().toDouble() < Constants.MINIMUM_ORDER_PRICE -> {
                    genericHandler.showErrorMessage("Minimum ${Constants.MINIMUM_ORDER_PRICE}${Constants.CURRENCY} must be entered")
                    return
                }
                deliveryTime.isEmpty() -> {
                    genericHandler.showErrorMessage(getString(R.string.str_enter_valid_delivery_time))
                    return
                }
                noOfRevision == -1 -> {
                    genericHandler.showErrorMessage(getString(R.string.str_select_no_of_revision))
                    return
                }
                imagesArrayList.isEmpty() -> {
                    genericHandler.showErrorMessage(getString(R.string.str_enter_valid_images))
                    return
                }
                etInfo.editableText.trim().isEmpty() -> {
                    genericHandler.showErrorMessage(getString(R.string.str_enter_valid_info))
                    return
                }
                etInfo.editableText.trim().length < 4 -> {
                    genericHandler.showErrorMessage(getString(R.string.str_enter_valid_info_length))
                    return
                }
                else -> {
                    createMultipartData()
                }
            }
        }
    }

    private fun createMultipartData() {
        val job = lifecycleScope.launch {
            val images: ArrayList<MultipartBody.Part> = ArrayList()
            imagesArrayList.forEach {
                images.add(
                    BaseUtils.compressAndCreateImageData(
                        it.path,
                        "banner[]",
                        requireContext()
                    )
                )
            }
            val map: HashMap<String, RequestBody> = HashMap()
            map["description"] =
                BaseUtils.createRequestBodyFromString(binding.etDescription.text.toString().trim())
            map["s_description"] =
                BaseUtils.createRequestBodyFromString(binding.etShortDes.text.toString().trim())
            map["price"] =
                BaseUtils.createRequestBodyFromString(binding.etPrice.text.toString().trim())
            map["additional_info"] =
                BaseUtils.createRequestBodyFromString(binding.etInfo.text.toString().trim())
            map["delivery_time"] = BaseUtils.createRequestBodyFromString(deliveryTime)
            map["revision"] = BaseUtils.createRequestBodyFromString(noOfRevision.toString())
            map["sub_category_id"] = BaseUtils.createRequestBodyFromString(subcategoryId)
            map["category_id"] = BaseUtils.createRequestBodyFromString(categoryId)
            val prefManager = PrefManager(requireContext())
            if (prefManager.location != null) {
                val location = prefManager.location!!
                map["lat"] = BaseUtils.createRequestBodyFromString(location.lat.toString())
                map["lng"] = BaseUtils.createRequestBodyFromString(location.long.toString())
            }
            exeApi(images, map)
        }
    }

    private fun exeApi(
        images: ArrayList<MultipartBody.Part>,
        map: java.util.HashMap<String, RequestBody>
    ) {
        genericHandler.showProgressBar(true)
        viewModel.createServiceCall(CreateServiceRequest(partMap = map, images = images))
    }

    private fun initViews() {
        imagesArrayList = ArrayList()
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
        binding.toolbar.ivToolbar.isVisible=true
        binding.toolbar.tvToolbar.text = getString(R.string.str_create_service)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(ManageServicesViewModel::class.java)
    }

    private fun setupObservers() {
        viewModel.categoriesRevisionDeliveryTimeResponse.observe(viewLifecycleOwner, {
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
                        genericHandler.showErrorMessage(it.message.toString())
                    }
                    Status.LOADING -> {
                        genericHandler.showProgressBar(true)
                    }
                }
            }
        })
        viewModel.spinnerSubcategories.observe(viewLifecycleOwner, subcategoriesObserver)
        viewModel.createService.observe(viewLifecycleOwner, createServiceObserver)
    }

    private fun <T> handleResponse(response: T) {
        try {
            when (response) {
                is CategoriesCountriesResponse -> {
                    setUIData(response)
                }
                is SubcategoriesDataResponse -> {
                    setSpinnerSubcategories(response)
                }
                is ServiceResponse -> {
                    findNavController().popBackStack()
                    findNavController().navigate(R.id.manageServicesFragment)
                }
            }
        } catch (e: Exception) {
            genericHandler.showErrorMessage(e.message.toString())
        }
    }

    private fun setSpinnerSubcategories(response: SubcategoriesDataResponse) {
        subcategoriesArrayList = response.subcategoriesList.map { spinnerSubcategories ->
            SpinnerModel(id = spinnerSubcategories.id, value = spinnerSubcategories.title)
        }
        subcategoriesAdapter = SpinnerAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item, subcategoriesArrayList
        ).also {
            it.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            binding.spinnerSubCategory.adapter = it
        }
    }

    private fun setUIData(response: CategoriesCountriesResponse) {
        categoriesArrayList = response.categoriesCountriesData.categories.map { spinnerCategories ->
            SpinnerModel(id = spinnerCategories.id!!, value = spinnerCategories.title!!)
        }
        categoriesAdapter = SpinnerAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item, categoriesArrayList
        ).also {
            it.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            binding.spinnerCategory.adapter = it
        }

        daysArrayList = response.categoriesCountriesData.deliveryDays
        daysAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item, daysArrayList
        ).also {
            it.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            binding.spinnerDeliveryTime.adapter = it
        }

        noOfRevisionArrayList = response.categoriesCountriesData.revisions
        noOfRevisionAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item, noOfRevisionArrayList
        ).also {
            it.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            binding.spinnerNoOfRevision.adapter = it
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
            binding.spinnerNoOfRevision.id -> {
                noOfRevision = parent.selectedItem.toString().toInt()
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    private val subcategoriesObserver = Observer<Resource<SubcategoriesDataResponse>> {
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
                    genericHandler.showErrorMessage(it.message.toString())
                }
                Status.LOADING -> {
                    genericHandler.showProgressBar(true)
                }
            }
        }
    }

    private val createServiceObserver = Observer<Resource<ServiceResponse>> {
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
                    genericHandler.showErrorMessage(it.message.toString())
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
            //Log.i("HELL",imagesArrayList.size.toString())
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
                    genericHandler.showErrorMessage("Invalid data")
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
                genericHandler.showErrorMessage("Choose valid images")
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
                genericHandler.showErrorMessage(
                    getString(R.string.permission_required)
                        .plus(". Please enable it settings")
                )
            }
        }
}