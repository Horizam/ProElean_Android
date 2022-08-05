package com.horizam.pro.elean.ui.main.view.fragments

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.workDataOf
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.messaging.FirebaseMessaging
import com.horizam.pro.elean.Constants
import com.horizam.pro.elean.MyWorker
import com.horizam.pro.elean.R
import com.horizam.pro.elean.data.api.ApiHelper
import com.horizam.pro.elean.data.api.RetrofitBuilder
import com.horizam.pro.elean.data.model.requests.LoginRequest
import com.horizam.pro.elean.data.model.response.LoginResponse
import com.horizam.pro.elean.databinding.FragmentLoginBinding
import com.horizam.pro.elean.ui.base.ViewModelFactory
import com.horizam.pro.elean.ui.main.callbacks.GenericHandler
import com.horizam.pro.elean.ui.main.view.activities.HomeActivity
import com.horizam.pro.elean.ui.main.viewmodel.LoginViewModel
import com.horizam.pro.elean.utils.BaseUtils.Companion.hideKeyboard
import com.horizam.pro.elean.utils.PrefManager
import com.horizam.pro.elean.utils.Status
import com.horizam.pro.elean.utils.Validator


class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel: LoginViewModel
    private lateinit var genericHandler: GenericHandler
    private lateinit var prefManager: PrefManager

    override fun onAttach(context: Context) {
        super.onAttach(context)
        genericHandler = context as GenericHandler
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViewModel()
        setupObservers()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        initViews()
        getFcmToken()
        //setupViewModel()
        setupUI()
        //setupObservers()
        setClickListeners()
        return binding.root
    }

    private fun getFcmToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(ContentValues.TAG, "Fetching FCM registration token failed", task.exception)
            }
            sendRegistrationToServer(task.result)
        })
    }

    private fun sendRegistrationToServer(token: String?) {
        Log.d("mytag", "sendRegistrationTokenToServer($token)")
        val tokenBuilder = OneTimeWorkRequestBuilder<MyWorker>()
        val data = workDataOf(Constants.KEY_TOKEN to token)
        tokenBuilder.setInputData(data)
        //WorkManager.getInstance(application).enqueue(tokenBuilder.build())
        val prefManager = PrefManager(requireContext())
        if (token != null) {
            prefManager.fcmToken = token
        }
    }

    private fun initViews() {
        prefManager = PrefManager(requireContext())
    }

    private fun setClickListeners() {
        binding.tvSignUp.setOnClickListener {
            findNavController().navigate(R.id.signUpFragment)
        }
        binding.tvForgetPassword.setOnClickListener {
            if (!Validator.isValidEmail(binding.etEmail)) {
                findNavController().navigate(R.id.resetPasswordFragment)
            } else {
                val bundle = Bundle()
                bundle.putString("email", binding.etEmail.text.toString().trim())
                findNavController().navigate(R.id.resetPasswordFragment, bundle)
            }
        }
        binding.btnLogin.setOnClickListener {
            hideKeyboard()
            validateData()
        }
    }

    private fun validateData() {
        removeTextFieldsErrors()

        if (!Validator.isValidEmail(binding.etEmail)) {
            genericHandler.showErrorMessage(getString(R.string.str_enter_valid_email_address))
            return
        } else if (binding.etPassword.text.toString().isEmpty()) {
            genericHandler.showErrorMessage(getString(R.string.str_password_not_entered))
            binding.textFieldPassword.error = getString(R.string.str_password_not_entered)
            return
        } else if (binding.etPassword.text.toString().length < 6) {
            genericHandler.showErrorMessage("Password must be atleast 6 character")
            binding.textFieldPassword.error = "Password must be atleast 6 character"
        } else {
            executeApi()
        }
    }

    private fun removeTextFieldsErrors() {
        binding.textFieldEmail.error = null;
        binding.textFieldPassword.error = null;
    }

    private fun executeApi() {
        if (prefManager.fcmToken.isEmpty()) {
            genericHandler.showErrorMessage("Please try again later")
            return
        }
        genericHandler.showProgressBar(true)
        val loginRequest = LoginRequest(
            email = binding.etEmail.text.toString().trim(),
            password = binding.etPassword.text.toString().trim(),
            fcm_token = prefManager.fcmToken
        )
        viewModel.loginUserCall(loginRequest)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(LoginViewModel::class.java)
    }

    private fun setupUI() {
        // init here
    }

    private fun setupObservers() {
        viewModel.loginUser.observe(this, {
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
    }

    private fun handleResponse(response: LoginResponse) {
        genericHandler.showSuccessMessage(response.message)
        val prefManager = PrefManager(requireContext())
        prefManager.clearAll()
        prefManager.accessToken = response.token
        prefManager.isFreelancer = response.data.isFreelancer
        prefManager.username = response.data.username
        prefManager.userImage = response.data.image
        prefManager.userId = response.data.id
        prefManager.sellerMode = 0
        startActivity(Intent(requireActivity(), HomeActivity::class.java))
        requireActivity().finish()
    }
}