package com.horizam.pro.elean.ui.main.view.fragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.horizam.pro.elean.App
import com.horizam.pro.elean.R
import com.horizam.pro.elean.data.api.ApiHelper
import com.horizam.pro.elean.data.api.RetrofitBuilder
import com.horizam.pro.elean.data.model.response.EarningsResponse
import com.horizam.pro.elean.databinding.*
import com.horizam.pro.elean.ui.base.ViewModelFactory
import com.horizam.pro.elean.ui.main.callbacks.GenericHandler
import com.horizam.pro.elean.ui.main.viewmodel.EarningsViewModel
import com.horizam.pro.elean.utils.Status
import java.lang.Exception
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.data.PieData

import com.github.mikephil.charting.data.PieDataSet
import com.horizam.pro.elean.data.model.response.EarningsData


class EarningsFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var binding: FragmentEarningsBinding
    private lateinit var viewModel: EarningsViewModel
    private lateinit var genericHandler: GenericHandler
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private var accountVerified: Int = 0
    private lateinit var dialogWithdrawalAmount: Dialog
    private lateinit var layoutWithdrawlAmountBinding: LayoutWithdrawlAmountBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        genericHandler = context as GenericHandler
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEarningsBinding.inflate(layoutInflater, container, false)
        setToolbarData()
        initViews()
        setupViewModel()
        setupObservers()
        setupWithDrawlAmountObserver()
        setOnClickListeners()
        initChooseImageDialog()
        return binding.root
    }

    private fun setupWithDrawlAmountObserver() {
        viewModel.withdrawalAmount.observe(viewLifecycleOwner, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        genericHandler.showProgressBar(false)
                        genericHandler.showSuccessMessage(it.data!!.message.toString())
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

    override fun onRefresh() {
        if (swipeRefreshLayout.isRefreshing) {
            swipeRefreshLayout.isRefreshing = false
        }
        setupObservers()
    }

    private fun setPieChartGraph(data: EarningsData) {
        binding.chart.isDrawHoleEnabled = true
        binding.chart.setDrawEntryLabels(false)
        binding.chart.setHoleColor(ContextCompat.getColor(requireContext(), R.color.colorAccent))
        val pieEntries: ArrayList<PieEntry> = ArrayList()
        val label = ""

        //initializing data
        val amountMap: HashMap<String, Int> = HashMap()
        val colors: ArrayList<Int> = ArrayList()

        amountMap["Total Earning"] = data.total_earning.toInt()
        colors.add(ContextCompat.getColor(requireContext(), R.color.colorThree))
        amountMap["This Year Earning"] = data.year_earning.toInt()
        colors.add(ContextCompat.getColor(requireContext(), R.color.color_green))
        amountMap["This Month Earning"] = data.monthly_earning.toInt()
        colors.add(ContextCompat.getColor(requireContext(), R.color.colorGolden))

        for (type in amountMap.keys) {
            pieEntries.add(PieEntry(amountMap[type]!!.toFloat(), type))
        }

        //collecting the entries with label name
        val pieDataSet = PieDataSet(pieEntries, label)
        //setting text size of the value
        pieDataSet.valueTextSize = 12f
        //providing color list for coloring different entries
        pieDataSet.colors = colors
        //grouping the data set from entry to chart
        val pieData = PieData(pieDataSet)
        //showing the value of the entries, default true if not set
        pieData.setDrawValues(true)

        binding.chart.data = pieData
        binding.chart.description.setEnabled(false);
        binding.chart.invalidate()

    }

    private fun initViews() {
        // init views here
        swipeRefreshLayout = binding.swipeRefresh
        swipeRefreshLayout.setOnRefreshListener(this)
    }

    private fun setOnClickListeners() {
        binding.apply {
            toolbar.ivToolbar.setOnClickListener {
                findNavController().popBackStack()
            }
        }
        binding.btnWithdraw.setOnClickListener {
            if (accountVerified == 0) {
                this.findNavController().navigate(R.id.bankDetailsFragment)
            } else {
                dialogWithdrawalAmount.show()
                val window: Window = dialogWithdrawalAmount.window!!
                window.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }
        }
    }

    private fun initChooseImageDialog() {
        dialogWithdrawalAmount = Dialog(requireContext())
        layoutWithdrawlAmountBinding = LayoutWithdrawlAmountBinding.inflate(layoutInflater)
        dialogWithdrawalAmount.setContentView(layoutWithdrawlAmountBinding.root)
        layoutWithdrawlAmountBinding.btnWithdraw.setOnClickListener {
            dialogWithdrawalAmount.dismiss()
            exeWithdrawalRequest(layoutWithdrawlAmountBinding.etAmount.text.toString())
        }
    }

    private fun exeWithdrawalRequest(amount: String) {
        viewModel.withdrawalAmount(amount.toDouble())
    }

    private fun setToolbarData() {
        binding.toolbar.ivToolbar.setImageResource(R.drawable.ic_back)
        binding.toolbar.tvToolbar.text = App.getAppContext()!!.getString(R.string.str_earnings)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(EarningsViewModel::class.java)
    }

    private fun setupObservers() {
        viewModel.earnings.observe(viewLifecycleOwner, {
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

    private fun handleResponse(response: EarningsResponse) {
        try {
            accountVerified = response.data.bankaccount_verified
            setPieChartGraph(response.data)
            setUIData(response.data)
        } catch (e: Exception) {
            genericHandler.showErrorMessage(e.message.toString())
        }
    }

    private fun setUIData(earningsData: EarningsData) {
        binding.apply {
            tvCurrentBalanceValue.text =
                "${getString(R.string.str_currency_sign)}${earningsData.current_balance}"
            tvTotalEarningValue.text =
                "${getString(R.string.str_currency_sign)}${earningsData.total_earning}"
            tvEarningThisYearValue.text =
                "${getString(R.string.str_currency_sign)}${earningsData.year_earning}"
            tvEarningThisMonthValue.text =
                "${getString(R.string.str_currency_sign)}${earningsData.monthly_earning}"
            tvEarningThisWeekValue.text =
                "${getString(R.string.str_currency_sign)}${earningsData.weekly_earning}"
        }
    }
}