package com.horizam.pro.elean.ui.main.view.fragments

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.horizam.pro.elean.R
import com.horizam.pro.elean.databinding.FragmentDelieverFileBottomSheetBinding
import com.horizam.pro.elean.ui.main.callbacks.DeliverOrderFileHandler
import com.horizam.pro.elean.ui.main.callbacks.GenericHandler
import com.horizam.pro.elean.utils.URIPathHelper
import java.io.File

class DeliverFileBottomSheetFragment(
    var fragment: OrderDetailsFragment

) : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentDelieverFileBottomSheetBinding
    private lateinit var genericHandler: GenericHandler
    private var imagePath: String = ""
    private var deliverOrderFileHandler = fragment as DeliverOrderFileHandler

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDelieverFileBottomSheetBinding.inflate(layoutInflater, container, false)

        setOnClickListener()

        return binding.root
    }

    private fun setOnClickListener() {
        binding.rlChooseFile.setOnClickListener {
            onClickRequestPermission()
        }
        binding.btnSubmit.setOnClickListener {
            if (imagePath.isNotEmpty() && binding.etDescription.text!!.length >= 10) {
                deliverOrderFileHandler.sendFilePath(
                    filePath = imagePath,
                    description = binding.etDescription.text.toString()
                )
                dismiss()
            } else if(binding.etDescription.text.toString().trimStart().length < 15){
                binding.etDescription.error = "Description is too short"
            }else{

            }
        }

        binding.tvRemove.setOnClickListener {
            binding.tvFileName.text = ""
            imagePath = ""
            binding.rlFileName.visibility = View.INVISIBLE
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
        // if single file is selected
        val imageUri = data.data!!
        val uriPathHelper = URIPathHelper()
        imagePath = uriPathHelper.getPath(requireContext(), imageUri).toString()
        binding.tvFileName.text = File(imagePath).name
        binding.rlFileName.visibility = View.VISIBLE
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

    private fun onClickRequestPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                val imageIntent = Intent().apply {
                    type = "image/*"
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