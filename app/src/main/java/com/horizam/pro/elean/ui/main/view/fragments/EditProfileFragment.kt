package com.horizam.pro.elean.ui.main.view.fragments

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.horizam.pro.elean.App
import com.horizam.pro.elean.Constants
import com.horizam.pro.elean.R
import com.horizam.pro.elean.data.api.ApiHelper
import com.horizam.pro.elean.data.api.RetrofitBuilder
import com.horizam.pro.elean.data.model.Image
import com.horizam.pro.elean.data.model.requests.CreateServiceRequest
import com.horizam.pro.elean.data.model.requests.FavouriteRequest
import com.horizam.pro.elean.data.model.requests.UpdateProfileRequest
import com.horizam.pro.elean.data.model.response.GeneralResponse
import com.horizam.pro.elean.data.model.response.NonFreelancerUserResponse
import com.horizam.pro.elean.databinding.*
import com.horizam.pro.elean.ui.base.ViewModelFactory
import com.horizam.pro.elean.ui.main.adapter.SkillsAdapter
import com.horizam.pro.elean.ui.main.callbacks.GenericHandler
import com.horizam.pro.elean.ui.main.viewmodel.ProfileViewModel
import com.horizam.pro.elean.utils.*
import com.horizam.pro.elean.utils.BaseUtils.Companion.hideKeyboard
import kotlinx.coroutines.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap
import kotlin.coroutines.CoroutineContext


class EditProfileFragment : Fragment() {

    private lateinit var profileImage: String
    private lateinit var binding: FragmentEditProfileBinding
    private lateinit var viewModel: ProfileViewModel
    private lateinit var genericHandler: GenericHandler
    private lateinit var dialogChooseImage: Dialog
    private lateinit var bindingChooseImageDialog: DialogChoosePictureBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        genericHandler = context as GenericHandler
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditProfileBinding.inflate(layoutInflater, container, false)
        setToolbarData()
        initViews()
        setupViewModel()
        setupObservers()
        setClickListeners()
        return binding.root
    }

    private fun initViews() {
        profileImage = ""
        initChooseImageDialog()
    }

    private fun initChooseImageDialog() {
        dialogChooseImage = Dialog(requireContext())
        bindingChooseImageDialog = DialogChoosePictureBinding.inflate(layoutInflater)
        dialogChooseImage.setContentView(bindingChooseImageDialog.root)
    }

    private fun setClickListeners() {
        binding.apply {
            toolbar.ivToolbar.setOnClickListener {
                findNavController().popBackStack()
            }
            btnRetry.setOnClickListener {
                executeApi()
            }
            submitBtn.setOnClickListener {
                hideKeyboard()
                validateData()
            }
            cardEditPicture.setOnClickListener {
                dialogChooseImage.show()
                bindingChooseImageDialog.btnCamera.setOnClickListener {
                    dialogChooseImage.dismiss()
                    onClickRequestCameraPermission()
                }
                bindingChooseImageDialog.btnGallery.setOnClickListener {
                    dialogChooseImage.dismiss()
                    onClickRequestPermission()
                }
                bindingChooseImageDialog.btnNo.setOnClickListener { dialogChooseImage.dismiss() }
            }
        }
    }

    private fun onClickRequestCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                dispatchTakePictureIntent()
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.CAMERA
            ) -> {
                showSnackbar(
                    requireView(),
                    getString(R.string.permission_required),
                    Snackbar.LENGTH_INDEFINITE,
                    getString(R.string.str_ok)
                ) {
                    requestCameraPermissionLauncher.launch(
                        Manifest.permission.CAMERA
                    )
                }
            }
            else -> {
                requestCameraPermissionLauncher.launch(
                    Manifest.permission.CAMERA
                )
            }
        }
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(requireContext().packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    genericHandler.showMessage(ex.message.toString())
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        requireContext(),
                        "com.horizam.pro.elean.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    resultCameraLauncher.launch(photoURI)
                }
            }
        }
    }

    private fun executeApi() {
        genericHandler.showProgressBar(true)
        viewModel.profileDataCall()
    }

    private fun setToolbarData() {
        binding.toolbar.ivToolbar.setImageResource(R.drawable.ic_back)
        binding.toolbar.tvToolbar.text = App.getAppContext()!!.getString(R.string.edit_profile)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(ProfileViewModel::class.java)
    }

    private fun setupObservers() {
        viewModel.profileData.observe(viewLifecycleOwner, {
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
        viewModel.updateProfile.observe(viewLifecycleOwner, updateProfileObserver)
    }

    private val updateProfileObserver = Observer<Resource<GeneralResponse>> {
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

    private fun changeViewVisibility(textView: Boolean, button: Boolean, layout: Boolean) {
        binding.textViewError.isVisible = textView
        binding.btnRetry.isVisible = button
        binding.mainLayout.isVisible = layout
    }

    private fun <T> handleResponse(response: T) {
        try {
            if (response is NonFreelancerUserResponse) {
                setUiData(response)
            } else if (response is GeneralResponse) {
                genericHandler.showMessage(response.message)
                if (response.status == Constants.STATUS_OK) {
                    viewModel.profileDataCall()
                }
            }
        } catch (e: Exception) {
            genericHandler.showMessage(e.message.toString())
        }
    }

    private fun setUiData(response: NonFreelancerUserResponse) {
        binding.apply {
            response.userProfile.let { profile ->
                Glide.with(this@EditProfileFragment)
                    .load(Constants.BASE_URL.plus(profile.image))
                    .error(R.drawable.img_profile)
                    .placeholder(R.drawable.img_loading)
                    .into(binding.ivProfile)
                etFullName.setText(profile.name)
                etAddress.setText(profile.address)
                etPhone.setText(profile.phone)
                //etCarrierNumber.setText(profile.phone)
            }
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
                    putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
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

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (result.data != null) {
                    try {
                        handlePickerResult(result.data!!)
                    } catch (e: Exception) {
                        genericHandler.showMessage(e.message.toString())
                    }
                } else {
                    genericHandler.showMessage("Invalid data")
                }
            }
        }

    private var resultCameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { result ->
            if (result) {
                try {
                    Glide.with(this)
                        .load(profileImage)
                        .error(R.drawable.img_profile)
                        .placeholder(R.drawable.img_loading)
                        .into(binding.ivProfile)
                } catch (ex: Exception) {
                    genericHandler.showMessage(ex.message.toString())
                }
            } else {
                genericHandler.showMessage("Invalid data")
            }
        }


    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            profileImage = absolutePath
        }
    }


    private fun handlePickerResult(data: Intent) {
        if (data.data != null) {
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
                profileImage = imagePath
                Glide.with(this)
                    .load(profileImage)
                    .error(R.drawable.img_profile)
                    .placeholder(R.drawable.img_loading)
                    .into(binding.ivProfile)
            } else {
                genericHandler.showMessage("Choose valid image")
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
                    putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
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

    private val requestCameraPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Log.i("Permission: ", "Granted")
                dispatchTakePictureIntent()
            } else {
                Log.i("Permission: ", "Denied")
                genericHandler.showMessage(
                    getString(R.string.permission_required)
                        .plus(". Please enable it settings")
                )
            }
        }

    private fun validateData() {
        binding.apply {
            when {
                etFullName.editableText.trim().isEmpty() -> {
                    genericHandler.showMessage(getString(R.string.str_enter_valid_username))
                    return
                }
                etPhone.editableText.trim().isEmpty() -> {
                    genericHandler.showMessage(getString(R.string.str_enter_valid_phone))
                    return
                }
                etAddress.editableText.trim().isEmpty() -> {
                    genericHandler.showMessage(getString(R.string.str_enter_valid_address))
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
            var image: MultipartBody.Part? = null
            if (profileImage.isNotEmpty()) {
                image =
                    BaseUtils.compressAndCreateImageData(profileImage, "image", requireContext())
            }
            val map: HashMap<String, RequestBody> = HashMap()
            map["name"] =
                BaseUtils.createRequestBodyFromString(binding.etFullName.text.toString().trim())
            map["phone"] =
                BaseUtils.createRequestBodyFromString(binding.etPhone.text.toString().trim())
            map["address"] =
                BaseUtils.createRequestBodyFromString(binding.etAddress.text.toString().trim())
            exeApi(image, map)
        }
    }

    private fun exeApi(image: MultipartBody.Part?, map: java.util.HashMap<String, RequestBody>) {
        genericHandler.showProgressBar(true)
        viewModel.updateProfileCall(UpdateProfileRequest(partMap = map, image = image))
    }

}