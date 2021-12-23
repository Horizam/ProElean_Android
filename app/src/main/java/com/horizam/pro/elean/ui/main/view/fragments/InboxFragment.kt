package com.horizam.pro.elean.ui.main.view.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.horizam.pro.elean.App
import com.horizam.pro.elean.Constants
import com.horizam.pro.elean.R
import com.horizam.pro.elean.data.api.ApiHelper
import com.horizam.pro.elean.data.api.RetrofitBuilder
import com.horizam.pro.elean.data.model.Inbox
import com.horizam.pro.elean.data.model.Message
import com.horizam.pro.elean.databinding.FragmentInboxBinding
import com.horizam.pro.elean.ui.base.ViewModelFactory
import com.horizam.pro.elean.ui.main.adapter.InboxAdapter
import com.horizam.pro.elean.ui.main.adapter.MessageAdapter
import com.horizam.pro.elean.ui.main.adapter.MyLoadStateAdapter
import com.horizam.pro.elean.ui.main.adapter.NotificationsAdapter
import com.horizam.pro.elean.ui.main.callbacks.GenericHandler
import com.horizam.pro.elean.ui.main.callbacks.InboxHandler
import com.horizam.pro.elean.ui.main.viewmodel.InboxViewModel
import com.horizam.pro.elean.ui.main.viewmodel.MessagesViewModel
import com.horizam.pro.elean.utils.PrefManager
import java.lang.Exception


class InboxFragment : Fragment(), InboxHandler {

    private lateinit var binding: FragmentInboxBinding
    private lateinit var adapter: InboxAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var db: FirebaseFirestore
    private lateinit var inboxReference: CollectionReference
    private lateinit var prefManager: PrefManager
    private lateinit var genericHandler: GenericHandler
    private var myId: String = ""
    private lateinit var viewModel: InboxViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        genericHandler = context as GenericHandler
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInboxBinding.inflate(layoutInflater, container, false)
        setToolbarData()
        initViews()
        setupViewModel()
        setupObservers()
        setRecyclerView()
        setClickListeners()
        getInboxData()
        return binding.root
    }

    private fun getInboxData() {
//        myId = prefManager.userId
//        if (myId != ""){
//            adapter.setMyId(myId)
//            val query: Query = inboxReference.whereArrayContains("members", myId)
//                .orderBy("sentAt", Query.Direction.DESCENDING).limit(10)
//            genericHandler.showProgressBar(true)
//            viewModel.getInboxCall(query)
//        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(InboxViewModel::class.java)
    }

    private fun setupObservers() {
        viewModel.inbox.observe(viewLifecycleOwner) {
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
        }
    }

    private fun initViews() {
        recyclerView = binding.rvInbox
        adapter = InboxAdapter(this)
        db = Firebase.firestore
        inboxReference = db.collection(Constants.FIREBASE_DATABASE_ROOT)
        prefManager = PrefManager(requireContext())
    }

    private fun setRecyclerView() {
        recyclerView.let {
            it.layoutManager = LinearLayoutManager(requireContext())
            it.adapter = adapter.withLoadStateFooter(
                footer = MyLoadStateAdapter {
                    adapter.retry()
                }
            )
            it.addItemDecoration(
                DividerItemDecoration(
                    recyclerView.context,
                    (recyclerView.layoutManager as LinearLayoutManager).orientation
                )
            )
        }
        setAdapterLoadState(adapter)
    }

    private fun setAdapterLoadState(adapter: InboxAdapter) {
        adapter.addLoadStateListener { loadState ->
            binding.apply {
                genericHandler.showProgressBar(loadState.source.refresh is LoadState.Loading)
                recyclerView.isVisible = loadState.source.refresh is LoadState.NotLoading
                btnRetry.isVisible = loadState.source.refresh is LoadState.Error
                textViewError.isVisible = loadState.source.refresh is LoadState.Error
                // no results
                if (loadState.source.refresh is LoadState.NotLoading &&
                    loadState.append.endOfPaginationReached &&
                    adapter.itemCount < 1
                ) {
                    recyclerView.isVisible = false
                    tvPlaceholder.isVisible = true
                } else {
                    tvPlaceholder.isVisible = false
                }
            }
        }
    }

    private fun setClickListeners() {
        binding.apply {
            toolbar.ivToolbar.setOnClickListener {
                findNavController().popBackStack()
            }
            btnRetry.setOnClickListener {
                adapter.retry()
            }
        }
    }

    private fun setToolbarData() {
        binding.toolbar.ivToolbar.setImageResource(R.drawable.ic_back)
        binding.toolbar.tvToolbar.text = App.getAppContext()!!.getString(R.string.str_inbox)
    }

    override fun <T> onItemClick(item: T) {
//        if (item is Inbox) {
//            val userId = item.members.first {
//                it != myId
//            }
//            InboxFragmentDirections.actionInboxFragmentToMessagesFragment(userId).also {
//                findNavController().navigate(it)
//            }
//        }
    }
}