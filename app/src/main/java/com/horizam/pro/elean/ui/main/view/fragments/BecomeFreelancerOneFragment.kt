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
import androidx.navigation.fragment.findNavController
import com.horizam.pro.elean.App
import com.horizam.pro.elean.R
import com.horizam.pro.elean.data.api.ApiHelper
import com.horizam.pro.elean.data.api.RetrofitBuilder
import com.horizam.pro.elean.data.model.Freelancer
import com.horizam.pro.elean.data.model.SpinnerModel
import com.horizam.pro.elean.data.model.response.CategoriesCountriesResponse
import com.horizam.pro.elean.data.model.response.SubcategoriesDataResponse
import com.horizam.pro.elean.databinding.FragmentBecomeFreelancerOneBinding
import com.horizam.pro.elean.ui.base.ViewModelFactory
import com.horizam.pro.elean.ui.main.adapter.SpinnerAdapter
import com.horizam.pro.elean.ui.main.callbacks.GenericHandler
import com.horizam.pro.elean.ui.main.callbacks.OnItemClickListener
import com.horizam.pro.elean.ui.main.viewmodel.BecomeFreelancerViewModel
import com.horizam.pro.elean.utils.BaseUtils.Companion.hideKeyboard
import com.horizam.pro.elean.utils.Resource
import com.horizam.pro.elean.utils.Status
import kotlinx.android.synthetic.main.fragment_become_freelancer_one.*
import java.lang.Exception


class BecomeFreelancerOneFragment : Fragment(), AdapterView.OnItemSelectedListener , OnItemClickListener {

    private lateinit var binding: FragmentBecomeFreelancerOneBinding
    private lateinit var viewModel: BecomeFreelancerViewModel
    private lateinit var genericHandler: GenericHandler
    private lateinit var categoriesArrayList: List<SpinnerModel>
    private lateinit var subcategoriesArrayList: List<SpinnerModel>
    private lateinit var countriesArrayList: List<SpinnerModel>
    private lateinit var categoriesAdapter: ArrayAdapter<SpinnerModel>
    private lateinit var subcategoriesAdapter: ArrayAdapter<SpinnerModel>
    private lateinit var countriesAdapter: ArrayAdapter<SpinnerModel>
    private var categoryId: String = ""
    private var subcategoryId: String = ""
    private var countryId: String = ""

    override fun onAttach(context: Context) {
        super.onAttach(context)
        genericHandler = context as GenericHandler
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBecomeFreelancerOneBinding.inflate(layoutInflater, container, false)
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
            tvCountry.setOnClickListener {
                val selectCountryBottomSheet = SelectCountryBottomSheet(countriesArrayList , (this@BecomeFreelancerOneFragment as OnItemClickListener))
                selectCountryBottomSheet.show(
                    requireActivity().supportFragmentManager, ""
                )
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
                    genericHandler.showErrorMessage(getString(R.string.str_enter_valid_short_description))
                    return
                }
                etShortDes.editableText.trim().length < 15 -> {
                    genericHandler.showErrorMessage(getString(R.string.str_enter_valid_short_description_length))
                    return
                }
                countryId == "" -> {
                    genericHandler.showErrorMessage(getString(R.string.str_enter_valid_country))
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
                        .actionBecomeFreelancerOneFragmentToBecomeFreelancerTwoFragment(
                            freelancerData
                        )
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
//        binding.spinnerCountry.onItemSelectedListener = this
    }

    private fun setToolbarData() {
        binding.toolbar.ivToolbar.setImageResource(R.drawable.ic_back)
        binding.toolbar.tvToolbar.text =
            App.getAppContext()!!.getString(R.string.str_become_freelancer)
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
                        genericHandler.showErrorMessage(it.message.toString())
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
                is SubcategoriesDataResponse -> {
                    setSpinnerSubcategories(response)
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
            SpinnerModel(id = spinnerCategories.id, value = spinnerCategories.title)
        }
        countriesArrayList = response.categoriesCountriesData.countries.map { countries ->
            SpinnerModel(id = countries.id, value = countries.name , image = countries.image)
        }
        categoriesAdapter = SpinnerAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item, categoriesArrayList
        ).also {
            it.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
            binding.spinnerCategory.adapter = it
        }
//        countriesAdapter = SpinnerAdapter(
//            requireContext(),
//            android.R.layout.simple_spinner_item, countriesArrayList
//        ).also {
//            it.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
//            binding.spinnerCountry.adapter = it
//        }
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
//            binding.spinnerCountry.id -> {
//                val spinnerModel = parent.selectedItem as SpinnerModel
//                countryId = spinnerModel.id
//            }
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

    override fun <T> onItemClick(item: T) {
        when(item){
            is SpinnerModel ->{
                binding.tvCountry.text = item.value
                countryId = item.id
            }
        }
    }
}