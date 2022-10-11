package com.horizam.pro.elean.ui.main.view.fragments

import android.Manifest
import android.content.Context
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
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.horizam.pro.elean.App
import com.horizam.pro.elean.R
import com.horizam.pro.elean.data.api.ApiHelper
import com.horizam.pro.elean.data.api.RetrofitBuilder
import com.horizam.pro.elean.data.model.Freelancer
import com.horizam.pro.elean.data.model.requests.BecomeFreelancerRequest
import com.horizam.pro.elean.data.model.response.*
import com.horizam.pro.elean.databinding.*
import com.horizam.pro.elean.ui.base.ViewModelFactory
import com.horizam.pro.elean.ui.main.callbacks.GenericHandler
import com.horizam.pro.elean.ui.main.callbacks.UpdateHomeHandler
import com.horizam.pro.elean.ui.main.viewmodel.BecomeFreelancerViewModel
import com.horizam.pro.elean.utils.*
import com.horizam.pro.elean.utils.BaseUtils.Companion.hideKeyboard
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.lang.Exception


class BecomeFreelancerTwoFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private lateinit var binding: FragmentBecomeFreelancerTwoBinding
    private lateinit var viewModel: BecomeFreelancerViewModel
    private lateinit var genericHandler: GenericHandler
    private lateinit var updateHomeHandler: UpdateHomeHandler
    private lateinit var languagesArrayList: List<Languages>
    private lateinit var availabilityArrayList: List<String>
    private lateinit var languagesAdapter: ArrayAdapter<Languages>
    private lateinit var availabilityAdapter: ArrayAdapter<String>
    private lateinit var prefManager: PrefManager
    private val args: BecomeFreelancerTwoFragmentArgs by navArgs()
    private var languageString = ""
    private var availabilityString = ""
    private var cnicPath = ""

    override fun onAttach(context: Context) {
        super.onAttach(context)
        genericHandler = context as GenericHandler
        updateHomeHandler = context as UpdateHomeHandler
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBecomeFreelancerTwoBinding.inflate(layoutInflater, container, false)
        setToolbarData()
        initViews()
        setupViewModel()
        setupObservers()
        setClickListeners()
        return binding.root
    }

    private fun setClickListeners() {
        binding.apply {
            toolbar.ivToolbar.setOnClickListener {
                findNavController().popBackStack()
            }
            etFileUpload.setOnClickListener {
                cnicPath = ""
                etFileUpload.text = ""
                onClickRequestPermission()
            }
            btnSubmit.setOnClickListener {
                hideKeyboard()
                validateData()
            }
        }
    }

    private fun validateData() {
        binding.apply {
            when {
                languageString.isEmpty() -> {
                    genericHandler.showErrorMessage(getString(R.string.str_enter_valid_language))
                    return
                }
                cnicPath.isEmpty() -> {
                    genericHandler.showErrorMessage(getString(R.string.str_enter_valid_cnic))
                    return
                }
                BaseUtils.getFileSize(cnicPath) >= 2 -> {
                    genericHandler.showErrorMessage(getString(R.string.str_enter_valid_cnic_size))
                    return
                }
                availabilityString.isEmpty() -> {
                    genericHandler.showErrorMessage(getString(R.string.str_enter_valid_availability))
                    return
                }
                else -> {
                    createMultipartData()
                }
            }
        }
    }

    private fun createMultipartData() {
        val freelancer: Freelancer = args.freelancer
        val cnic = BaseUtils.compressAndCreateFormData(cnicPath, "cinic")
        val map: HashMap<String, RequestBody> = HashMap()
        /*map["username"] = BaseUtils.createRequestBodyFromString(freelancer.username)*/
        map["freelancer_title"] = BaseUtils.createRequestBodyFromString(freelancer.shortDescription)
        map["category_id"] = BaseUtils.createRequestBodyFromString(freelancer.categoryId.toString())
        map["sub_category_id"] =
            BaseUtils.createRequestBodyFromString(freelancer.subcategoryId.toString())
        map["description"] = BaseUtils.createRequestBodyFromString(freelancer.description)
        map["lang"] = BaseUtils.createRequestBodyFromString(languageString)
        map["portfolio"] =
            BaseUtils.createRequestBodyFromString(binding.etWebsite.text.toString().trim())
        map["facebook"] =
            BaseUtils.createRequestBodyFromString(binding.etFacebookUrl.text.toString().trim())
        map["linked_in"] =
            BaseUtils.createRequestBodyFromString(binding.etLinkedin.text.toString().trim())
        map["twitter"] =
            BaseUtils.createRequestBodyFromString(binding.etTwitter.text.toString().trim())
        map["instagram"] =
            BaseUtils.createRequestBodyFromString(binding.etInstagram.text.toString().trim())
        map["availability"] = BaseUtils.createRequestBodyFromString(availabilityString)
        map["country_id"] = BaseUtils.createRequestBodyFromString(freelancer.countryId.toString())
        exeApi(cnic, map)
    }

    private fun exeApi(cnic: MultipartBody.Part, map: HashMap<String, RequestBody>) {
        genericHandler.showProgressBar(true)
        viewModel.becomeFreelancerCall(BecomeFreelancerRequest(partMap = map, file = cnic))
    }

    private fun initViews() {
        languagesArrayList = ArrayList()
        availabilityArrayList = ArrayList()
        binding.spinnerLanguage.onItemSelectedListener = this
        binding.spinnerAvailability.onItemSelectedListener = this
        prefManager = PrefManager(requireContext())
    }

    private fun setToolbarData() {
        binding.toolbar.ivToolbar.setImageResource(R.drawable.ic_back)
        binding.toolbar.ivToolbar.isVisible=true
        binding.toolbar.tvToolbar.text = getString(R.string.str_become_freelancer)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(BecomeFreelancerViewModel::class.java)
    }

    private fun setupObservers() {
        viewModel.spinnerData.observe(viewLifecycleOwner) {
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
        viewModel.becomeFreelancer.observe(viewLifecycleOwner, becomeFreelancerObserver)
    }

    private val becomeFreelancerObserver = Observer<Resource<GeneralResponse>> {
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

    private fun <T> handleResponse(response: T) {
        try {
            when (response) {
                is CategoriesCountriesResponse -> {
                    setUIData(response)
                }
                is GeneralResponse -> {
                    prefManager.isFreelancer = 1
                    updateHomeHandler.callHomeApi()
                    genericHandler.showSuccessMessage(response.message)
//                    findNavController().popBackStack(R.id.homeFragment, false)
                    findNavController().popBackStack()
                }
            }
        } catch (e: Exception) {
            genericHandler.showErrorMessage(e.message.toString())
        }
    }

    private fun setUIData(response: CategoriesCountriesResponse) {
        languagesArrayList =response.categoriesCountriesData.languages
        availabilityArrayList = arrayListOf(getString(R.string.str_full_time), getString(R.string.str_part_time))
        languagesAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item, languagesArrayList
        ).also {
            it.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
            binding.spinnerLanguage.adapter = it
        }
        availabilityAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item, availabilityArrayList
        ).also {
            it.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
            binding.spinnerAvailability.adapter = it
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (parent!!.id) {
            binding.spinnerLanguage.id -> {
                languageString = parent.selectedItem.toString()
            }
            binding.spinnerAvailability.id -> {
                availabilityString = parent.selectedItem.toString()
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Log.i("Permission: ", "Granted")
                getContent.launch("*/*")
            } else {
                Log.i("Permission: ", "Denied")
                genericHandler.showErrorMessage(
                    getString(R.string.permission_required)
                        .plus(". ").plus(getString(R.string.str_please_enable))
                )
            }
        }

    private val getContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            // Handle the returned Uri
            uri?.let {
                val uriPathHelper = URIPathHelper()
                val filePath = uriPathHelper.getPath(requireContext(), uri)
                if (!filePath.isNullOrEmpty()) {
                    binding.etFileUpload.text = filePath
                    cnicPath = filePath
                    //genericHandler.showMessage(BaseUtils.isImageFile(cnicPath).toString())
                } else {
                    genericHandler.showErrorMessage(getString(R.string.str_choose_valid_document))
                }
            }
        }

    private fun onClickRequestPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                getContent.launch("*/*")
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
}