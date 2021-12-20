package com.horizam.pro.elean.ui.main.view.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.horizam.pro.elean.App
import com.horizam.pro.elean.R
import com.horizam.pro.elean.data.api.ApiHelper
import com.horizam.pro.elean.data.api.RetrofitBuilder
import com.horizam.pro.elean.data.model.Freelancer
import com.horizam.pro.elean.data.model.SpinnerModel
import com.horizam.pro.elean.data.model.response.CategoriesCountriesResponse
import com.horizam.pro.elean.data.model.response.CategoriesDaysResponse
import com.horizam.pro.elean.data.model.response.PostJobResponse
import com.horizam.pro.elean.data.model.response.SpinnerSubcategoriesResponse
import com.horizam.pro.elean.databinding.FragmentBecomeFreelancerOneBinding
import com.horizam.pro.elean.databinding.FragmentCreateServiceBinding
import com.horizam.pro.elean.databinding.FragmentLoginBinding
import com.horizam.pro.elean.databinding.FragmentSignUpBinding
import com.horizam.pro.elean.ui.base.ViewModelFactory
import com.horizam.pro.elean.ui.main.adapter.ImagesAdapter
import com.horizam.pro.elean.ui.main.adapter.NotificationsAdapter
import com.horizam.pro.elean.ui.main.adapter.SpinnerAdapter
import com.horizam.pro.elean.ui.main.callbacks.GenericHandler
import com.horizam.pro.elean.ui.main.viewmodel.BecomeFreelancerViewModel
import com.horizam.pro.elean.ui.main.viewmodel.PostJobViewModel
import com.horizam.pro.elean.utils.BaseUtils.Companion.hideKeyboard
import com.horizam.pro.elean.utils.Resource
import com.horizam.pro.elean.utils.Status
import java.lang.Exception


class BecomeFreelancerOneFragment : Fragment(),AdapterView.OnItemSelectedListener {

    private lateinit var binding: FragmentBecomeFreelancerOneBinding
    private lateinit var viewModel: BecomeFreelancerViewModel
    private lateinit var genericHandler: GenericHandler
    private lateinit var categoriesArrayList: List<SpinnerModel>
    private lateinit var subcategoriesArrayList: List<SpinnerModel>
    private lateinit var countriesArrayList: List<SpinnerModel>
    private lateinit var categoriesAdapter: ArrayAdapter<SpinnerModel>
    private lateinit var subcategoriesAdapter: ArrayAdapter<SpinnerModel>
    private lateinit var countriesAdapter: ArrayAdapter<SpinnerModel>
    private var categoryId: Int = -1
    private var subcategoryId: Int = -1
    private var countryId: Int = -1

    override fun onAttach(context: Context) {
        super.onAttach(context)
        genericHandler = context as GenericHandler
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBecomeFreelancerOneBinding.inflate(layoutInflater,container,false)
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
            btnNext.setOnClickListener {
                hideKeyboard()
                validateData()
            }
        }
    }

    private fun validateData() {
        binding.apply {
            when {
                /*etUsername.editableText.trim().isEmpty() -> {
                    genericHandler.showMessage(getString(R.string.str_enter_valid_username))
                    return
                }
                etUsername.editableText.trim().length < 15 -> {
                    genericHandler.showMessage(getString(R.string.str_enter_valid_username_length))
                    return
                }*/
                etShortDes.editableText.trim().isEmpty() -> {
                    genericHandler.showMessage(getString(R.string.str_enter_valid_short_description))
                    return
                }
                etShortDes.editableText.trim().length < 15 -> {
                    genericHandler.showMessage(getString(R.string.str_enter_valid_short_description_length))
                    return
                }
                countryId == -1 -> {
                    genericHandler.showMessage(getString(R.string.str_enter_valid_country))
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
                etDescription.editableText.trim().length < 20 -> {
                    genericHandler.showMessage(getString(R.string.str_enter_valid_description_length))
                    return
                }
                else -> {
                    val freelancerData = Freelancer(
                        username = etUsername.text.toString().trim(),
                        description = etDescription.text.toString().trim(),
                        shortDescription = etShortDes.text.toString().trim(),
                        countryId = countryId,
                        categoryId = categoryId,
                        subcategoryId = subcategoryId
                    )
                    val action = BecomeFreelancerOneFragmentDirections
                        .actionBecomeFreelancerOneFragmentToBecomeFreelancerTwoFragment(freelancerData)
                    findNavController().navigate(action)
                }
            }
        }
    }

    private fun initViews() {
        categoriesArrayList = ArrayList()
        subcategoriesArrayList = ArrayList()
        countriesArrayList = ArrayList()
        binding.spinnerCategory.onItemSelectedListener = this
        binding.spinnerSubCategory.onItemSelectedListener = this
        binding.spinnerCountry.onItemSelectedListener = this
    }

    private fun setToolbarData() {
        binding.toolbar.ivToolbar.setImageResource(R.drawable.ic_back)
        binding.toolbar.tvToolbar.text = App.getAppContext()!!.getString(R.string.str_become_freelancer)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(BecomeFreelancerViewModel::class.java)
    }

    private fun setupObservers() {
        viewModel.spinnerData.observe(viewLifecycleOwner, {
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
    }

    private fun <T> handleResponse(response: T) {
        try {
            when (response) {
                is CategoriesCountriesResponse -> {
                    setUIData(response)
                }
                is SpinnerSubcategoriesResponse -> {
                    setSpinnerSubcategories(response)
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
        subcategoriesAdapter = SpinnerAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item, subcategoriesArrayList
        ).also {
            it.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            binding.spinnerSubCategory.adapter = it
        }
    }

    private fun setUIData(response: CategoriesCountriesResponse) {
        categoriesArrayList = response.becomeFreelancer.categories.map { spinnerCategories ->
            SpinnerModel(id = spinnerCategories.id, value = spinnerCategories.title)
        }
        countriesArrayList = response.becomeFreelancer.countries.map { countries->
            SpinnerModel(id = countries.id,value = countries.name)
        }
        categoriesAdapter = SpinnerAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item, categoriesArrayList
        ).also {
            it.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
            binding.spinnerCategory.adapter = it
        }
        countriesAdapter = SpinnerAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item, countriesArrayList
        ).also {
            it.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
            binding.spinnerCountry.adapter = it
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
            binding.spinnerCountry.id -> {
                val spinnerModel = parent.selectedItem as SpinnerModel
                countryId = spinnerModel.id
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
}