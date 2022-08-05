package com.horizam.pro.elean.ui.main.view.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.horizam.pro.elean.data.model.SpinnerModel
import com.horizam.pro.elean.databinding.FragmentSelectCountryBottomSheetBinding
import com.horizam.pro.elean.ui.main.adapter.CountryAdapter
import com.horizam.pro.elean.ui.main.callbacks.OnItemClickListener

class SelectCountryBottomSheet(private var countriesArrayList: List<SpinnerModel>
, private var onItemClickListener: OnItemClickListener) :
    BottomSheetDialogFragment(){

    private lateinit var binding: FragmentSelectCountryBottomSheetBinding
    private lateinit var countryAdapter: CountryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSelectCountryBottomSheetBinding.inflate(layoutInflater, container, false)

        initView()
        setRecyclerView()
        setClickListener()

        return binding.root
    }

    private fun setClickListener() {
        binding.ivToolbar.setOnClickListener {
            dismiss()
        }
        binding.etSearch.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                filter(p0.toString())
            }

        })
    }

    private fun filter(value: String){
        var filteredList: ArrayList<SpinnerModel> = ArrayList()
        for(item in countriesArrayList){
            if(item.value.toLowerCase().contains(value.toLowerCase())){
                filteredList.add(item)
            }
        }
        countryAdapter.filterList(filteredList)
    }

    private fun setRecyclerView() {
        binding.rvCountries.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        countryAdapter = CountryAdapter(countriesArrayList as ArrayList<SpinnerModel> , onItemClickListener , this)

        binding.rvCountries.adapter = countryAdapter
    }

    private fun initView() {

    }
}