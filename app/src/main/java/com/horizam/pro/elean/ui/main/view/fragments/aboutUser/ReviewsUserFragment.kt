package com.horizam.pro.elean.ui.main.view.fragments.aboutUser

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.horizam.pro.elean.data.api.ApiHelper
import com.horizam.pro.elean.data.api.RetrofitBuilder
import com.horizam.pro.elean.data.model.response.ServiceReviews
import com.horizam.pro.elean.data.model.response.UserReview
import com.horizam.pro.elean.databinding.FragmentUserReviewsBinding
import com.horizam.pro.elean.ui.base.ViewModelFactory
import com.horizam.pro.elean.ui.main.adapter.UserReviewsAdapter
import com.horizam.pro.elean.ui.main.callbacks.GenericHandler
import com.horizam.pro.elean.ui.main.callbacks.OnItemClickListener
import com.horizam.pro.elean.ui.main.viewmodel.ProfileViewModel
import com.horizam.pro.elean.utils.Status


class ReviewsUserFragment : Fragment(), OnItemClickListener {

    private lateinit var binding: FragmentUserReviewsBinding
    private lateinit var adapter: UserReviewsAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: ProfileViewModel
    private lateinit var genericHandler: GenericHandler

    override fun onAttach(context: Context) {
        super.onAttach(context)
        genericHandler = context as GenericHandler
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserReviewsBinding.inflate(layoutInflater, container, false)
        setupViewModel()
        setupObservers()
        setRecyclerView()
        return binding.root
    }

    private fun setRecyclerView() {
        recyclerView = binding.rvUserReviews
        adapter = UserReviewsAdapter(this)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                recyclerView.context,
                (recyclerView.layoutManager as LinearLayoutManager).orientation
            )
        )
        recyclerView.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.reviewsData.observe(viewLifecycleOwner, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        genericHandler.showProgressBar(false)
                        resource.data?.let { response ->
                            handleResponse(response.data , response.avg_rating , response.total_reviews)
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

    private fun handleResponse(reviews: List<UserReview>, avgRating: String, totalReviews: String) {
        try {
            binding.apply {
                tvUserRating.text = avgRating
                tvRatingNumber.text = "(".plus(totalReviews).plus(")")
                if (reviews.isNotEmpty()){
                    adapter.submitList(reviews)
                    tvPlaceholder.isVisible = false
                    recyclerView.isVisible = true
                }else{
                    tvPlaceholder.isVisible = true
                    recyclerView.isVisible = false
                }
            }
        } catch (e: Exception) {
            genericHandler.showErrorMessage(e.message.toString())
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            requireActivity(),
            ViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(ProfileViewModel::class.java)
    }

    override fun <T> onItemClick(item: T) {
        if (item is ServiceReviews) {
            // add feature
        }
    }

}