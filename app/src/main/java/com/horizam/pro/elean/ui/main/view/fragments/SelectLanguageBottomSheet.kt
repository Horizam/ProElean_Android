package com.horizam.pro.elean.ui.main.view.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.horizam.pro.elean.data.model.LanguageChangeListeners
import com.horizam.pro.elean.data.model.LanguageList
import com.horizam.pro.elean.databinding.FragmentSelectLanguageBottomSheetBinding
import com.horizam.pro.elean.ui.main.adapter.LanguagesAdapter
import com.horizam.pro.elean.ui.main.view.activities.SplashActivity
import com.horizam.pro.elean.utils.PrefManager

class SelectLanguageBottomSheet : BottomSheetDialogFragment() ,LanguageChangeListeners {
    private lateinit var adapter: LanguagesAdapter
    private lateinit var languageList: ArrayList<LanguageList>
    private lateinit var binding: FragmentSelectLanguageBottomSheetBinding
    private lateinit var prefManager: PrefManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        prefManager = PrefManager(requireContext())
        binding = FragmentSelectLanguageBottomSheetBinding.inflate(layoutInflater, container, false)

        initViews()
        setRecyclerView()
        // loadData()
        // setOnClickListener()
        // setRadioGroupListener()

        return binding.root
    }

    private fun initViews() {
        languageList = ArrayList()
        languageList.add(LanguageList(id = "1", lName = "English"))
        languageList.add(LanguageList(id = "2", lName = "Finnish"))
    }

    private fun setRecyclerView() {
        binding.rvLanguage.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        adapter = LanguagesAdapter(languageList, this)
        binding.rvLanguage.adapter = adapter
    }

    override fun onLanguageChange() {
        startActivity(Intent(requireContext(), SplashActivity::class.java))
    }









































//
//    private fun loadData() {
//        for (element in languageList) {
//            val radioButton = RadioButton(requireContext())
//            radioButton.text = element.toString()
//            radioButton.id = View.generateViewId()
//            val radioGroup = RadioGroup.LayoutParams(RadioGroup.LayoutParams.FILL_PARENT,
//                RadioGroup.LayoutParams.WRAP_CONTENT)
//            binding.rvLanguage.addView(radioButton, radioGroup)
//        }

//    private fun setRadioGroupListener() {
//        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
//            val radioButton = binding.root.findViewById<RadioButton>(checkedId)
//                if (radioButton.isChecked) {
//                    if(checkedId==0)
//                    {
//                        prefManager.setLang("en")
//                        var intent = Intent(requireContext(), SplashActivity::class.java)
//                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                        startActivity(intent)
//                    }
//                    else if(checkedId==1) {
//                        prefManager.setLang("fi")
//                    }
//
//                }
//                    switch(checkedId) {
//                    prefManager.setLang == "en"
//                }
//                else if (checkedId == 1) {
//                    prefManager.setLang == "fi"
//                }
//                dismiss()
    }
//
//    private fun setOnClickListener() {
//        binding.btnSubmit.setOnClickListener{
////            prefManager.setLang=="fi"
////            var intent = Intent(requireContext(), SplashActivity::class.java)
////            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
////            startActivity(intent)
//           // dismiss()
//        }
//    }
