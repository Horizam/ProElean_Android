package com.horizam.pro.elean.ui.main.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.horizam.pro.elean.App
import com.horizam.pro.elean.R
import com.horizam.pro.elean.databinding.FragmentLoginBinding
import com.horizam.pro.elean.databinding.FragmentSettingsBinding
import com.horizam.pro.elean.databinding.FragmentSignUpBinding
import com.horizam.pro.elean.databinding.FragmentTermsBinding
import com.horizam.pro.elean.ui.main.callbacks.LockHandler


class TermsFragment : Fragment() {

    private lateinit var binding: FragmentTermsBinding
    private val args:TermsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTermsBinding.inflate(layoutInflater,container,false)
        setToolbarData()
        setClickListeners()
        setData()
        return binding.root
    }

    private fun setData() {
        binding.tvDescTerms.text = args.terms
    }

    private fun setClickListeners() {
        binding.toolbar.ivToolbar.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setToolbarData() {
        binding.toolbar.ivToolbar.setImageResource(R.drawable.ic_back)
        binding.toolbar.tvToolbar.text = App.getAppContext()!!.getString(R.string.term_of_services)
    }
}